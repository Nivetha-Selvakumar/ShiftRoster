package com.example.shiftroster.persistence.service.impl;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistence.service.BulkUploadService;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validation.BasicValidation;
import com.example.shiftroster.persistence.validation.BusinessValidation;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BulkUploadImpl implements BulkUploadService {

    @Autowired
    ShiftRepo shiftRepo;

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRosterRepo shiftRosterRepo;

    @Autowired
    BasicValidation basicValidation;

    @Override
    public void bulkuploadExcelValidation(String empId, MultipartFile file) throws IOException, CommonException {
        // Validate the employee ID
        businessValidation.employeeValidation(empId);

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<String> header = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Extract header
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new CommonException(AppConstant.HEADER_INVALID);
        }
        getValidHeader(headerRow, header);

        // Collect shifts data for each employee
        Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();

        // Process each row individually
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row currentRow = sheet.getRow(rowNum);
            if (currentRow == null) {
                // Skip empty rows
                continue;
            }
            // Perform basic validation
            Set<Integer> processedRows = new HashSet<>();
            List<String> rowErrors = basicValidation.validateRowBasic(currentRow, headerRow,processedRows);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                continue;
            }

            try {
                collectShiftData(currentRow, header, employeeShiftData, errors);
            } catch (CommonException e) {
                errors.add(AppConstant.ROW + (rowNum + 1) + AppConstant.COLLEN_SPACE + e.getMessage());
            }
        }

        // Close the workbook
        workbook.close();

        businessValidation.validateShiftDate(employeeShiftData,errors);
        // Save all collected shifts data to the database
        saveAllShiftsToRoster(empId, employeeShiftData, errors);

        // If there were any errors, throw them as a single exception
        if (!errors.isEmpty()) {
            throw new CommonException(String.join(AppConstant.NEW_LINE, errors));
        }
    }

    private void collectShiftData(Row row, List<String> header, Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors) throws CommonException {
        Cell empIdCell = row.getCell(0);
        String rowEmpId = basicValidation.getStringValueOfCell(empIdCell);

        // Validate employee ID
        if (employeeRepo.findByIdAndEmpStatus(Integer.valueOf(rowEmpId), EnumStatus.ACTIVE).isEmpty()) {
            errors.add(AppConstant.INVALID_DATA_IN_ROW + row.getRowNum());
            return;
        }

        Map<LocalDate, String> shifts = new HashMap<>();
        for (int i = 1; i < header.size(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                String shift = basicValidation.getStringValueOfCell(cell);
                if (!shift.isEmpty()) {
                    if (!AppConstant.UA.equalsIgnoreCase(shift) && !AppConstant.WO.equalsIgnoreCase(shift) && shiftRepo.findByShiftNameAndStatus(shift, EnumStatus.ACTIVE).isEmpty() ) {
                        errors.add(AppConstant.INVALID_DATA_IN_ROW + row.getRowNum());
                        return; // Skip processing this row further
                    }
                    LocalDate date = parseDate(header.get(i));
                    shifts.put(date, shift);
                }
            } else {
                errors.add(AppConstant.INVALID_DATA_IN_ROW + row.getRowNum());
                return; // Skip processing this row further
            }
        }
        employeeShiftData.put(rowEmpId, shifts);
    }

    private LocalDate parseDate(String dateStr) throws CommonException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppConstant.EXCEL_DATE_FORMAT);
        try {
            return LocalDate.parse(dateStr.split(AppConstant.STRING_SPACE)[0], formatter);
        } catch (Exception e) {
            throw new CommonException(AppConstant.INVALID_DATE_FORMAT + dateStr);
        }
    }

    private void getValidHeader(Row r, List<String> header) {
        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell cell = r.getCell(i);
            if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
                break;
            }
            header.add(cell.getStringCellValue());
        }
    }


    private void saveAllShiftsToRoster(String empId, Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors) {
        List<ShiftRosterEntity> shiftRosterEntities = new ArrayList<>();

        for (Map.Entry<String, Map<LocalDate, String>> entry : employeeShiftData.entrySet()) {
            String rowEmpId = entry.getKey();
            Map<LocalDate, String> shifts = entry.getValue();

            Map<Integer, ShiftRosterEntity> monthShiftRosterMap = new HashMap<>();

            for (Map.Entry<LocalDate, String> shiftEntry : shifts.entrySet()) {
                LocalDate date = shiftEntry.getKey();
                String shift = shiftEntry.getValue();

                ShiftRosterEntity shiftRosterEntity = monthShiftRosterMap.computeIfAbsent(
                        date.getMonthValue(),
                        month -> {
                            Optional<ShiftRosterEntity> existingShiftRoster = shiftRosterRepo.findByEmpIdAndMonthAndYear(Integer.parseInt(rowEmpId), month, date.getYear());
                            return existingShiftRoster.orElseGet(() -> createNewShiftRosterEntity(empId, rowEmpId, date));
                        }
                );
                Integer shiftValue = getShiftValue(shift, errors);
                setShiftValue(date.getDayOfMonth(), shiftValue, shiftRosterEntity);
            }

            // Collect all ShiftRosterEntities for the employee
            shiftRosterEntities.addAll(monthShiftRosterMap.values());
        }

        // Save all collected ShiftRosterEntities to the repository at once
        shiftRosterRepo.saveAll(shiftRosterEntities);
    }

    private ShiftRosterEntity createNewShiftRosterEntity(String empId, String rowEmpId, LocalDate date) {
        ShiftRosterEntity shiftRosterEntity = new ShiftRosterEntity();
        shiftRosterEntity.setEmpId(Integer.valueOf(rowEmpId));
        shiftRosterEntity.setMonth(date.getMonthValue());
        shiftRosterEntity.setYear(date.getYear());
        String empName = employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE).get().getEmpName();
        shiftRosterEntity.setCreatedBy(empName);
        shiftRosterEntity.setUpdatedBy(empName);
        shiftRosterEntity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        shiftRosterEntity.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return shiftRosterEntity;
    }

    private Integer getShiftValue(String shift, List<String> errors) {
        if (AppConstant.UA.equalsIgnoreCase(shift)) {
            return null;
        } else if (AppConstant.WO.equalsIgnoreCase(shift)) {
            return 0;
        } else {
            Optional<ShiftEntity> shiftEntity = shiftRepo.findByShiftNameAndStatus(shift, EnumStatus.ACTIVE);
            if (shiftEntity.isPresent()) {
                return shiftEntity.get().getId().intValue();
            } else {
                errors.add(AppConstant.INVALID_SHIFT + shift);
                return null;
            }
        }
    }

    private void setShiftValue(int day, Integer shift, ShiftRosterEntity shiftRosterEntity) {
        Map<Integer, String> daySetterMap = createDaySetterMap();
        String setterMethodName = daySetterMap.get(day);
        if (setterMethodName != null) {
            try {
                shiftRosterEntity.getClass()
                        .getMethod(setterMethodName, Integer.class)
                        .invoke(shiftRosterEntity, shift);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Integer, String> createDaySetterMap() {
        Map<Integer, String> daySetterMap = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            daySetterMap.put(i, AppConstant.SET_DAY + String.format(AppConstant.SET_DAY_FORMAT, i));
        }
        return daySetterMap;
    }
}
