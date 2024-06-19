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
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    public void bulkuploadExcelValidation(String empId, MultipartFile file) throws IOException, CommonException{
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
                Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
                if(collectShiftData(currentRow, header, employeeShiftData, errors) && businessValidation.validateShiftDate(employeeShiftData,errors)){
                    saveAllShiftsToRoster(empId, employeeShiftData, errors);
                }
            } catch (CommonException e) {
                errors.add(AppConstant.ROW + (rowNum + 1) + AppConstant.COLLEN_SPACE + e.getMessage());
            }
        }
        // Close the workbook
        workbook.close();

        if (!errors.isEmpty()) {
            Workbook errorWorkbook = createErrorWorkbook(errors);

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new IllegalStateException(AppConstant.INVALID_ATTRIBUTE);
            }
            HttpServletResponse response = attributes.getResponse();
            if (response == null) {
                throw new IllegalStateException(AppConstant.INVALID_RESPONSE);
            }

            response.setContentType(AppConstant.EXCEL_CONTENT_TYPE);
            response.setHeader(AppConstant.CONTENT_DISPOSITION, AppConstant.ERROR_FILE_NAME);

            try (OutputStream outputStream = response.getOutputStream()) {
                errorWorkbook.write(outputStream);
            }
            errorWorkbook.close();
        }
    }

    private Workbook createErrorWorkbook( List<String> errors) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(AppConstant.ERRORS);
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        // Write header to error sheet
        Row headerRow = sheet.createRow(0);
        Cell cell = headerRow.createCell(0);
        cell.setCellValue(AppConstant.ERRORS);
        cell.setCellStyle(style);

        // Write errors to error sheet
        for (int i = 0; i < errors.size(); i++) {
            Row errorRow = sheet.createRow(i + 1);
            Cell errorCell = errorRow.createCell(0);
            errorCell.setCellValue(errors.get(i));
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }


    private boolean collectShiftData(Row row, List<String> header, Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors) throws CommonException {
        Cell empIdCell = row.getCell(0);
        String rowEmpId = basicValidation.getStringValueOfCell(empIdCell);

        // Validate employee ID
        if (employeeRepo.findByIdAndEmpStatus(Integer.valueOf(rowEmpId.trim()), EnumStatus.ACTIVE).isEmpty()) {
            errors.add(AppConstant.INVALID_DATA_IN_ROW + row.getRowNum());
            return false;
        }

        Map<LocalDate, String> shifts = new HashMap<>();
        for (int i = 1; i < header.size(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                String stringShift = basicValidation.getStringValueOfCell(cell);
                String shift = stringShift.trim();
                if (!shift.isEmpty()) {
                    if (!AppConstant.UA.equalsIgnoreCase(shift) && !AppConstant.WO.equalsIgnoreCase(shift) && shiftRepo.findByShiftNameAndStatus(shift, EnumStatus.ACTIVE).isEmpty() ) {
                        errors.add(AppConstant.INVALID_DATA_IN_ROW + row.getRowNum());
                        return false;
                    }
                    LocalDate date = parseDate(header.get(i));
                    shifts.put(date, shift);
                }
            } else {
                errors.add(AppConstant.INVALID_DATA_IN_ROW + row.getRowNum());
                return false;
            }
        }
        employeeShiftData.put(rowEmpId, shifts);
        return true;
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

    private void saveAllShiftsToRoster(String empId, Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors){
        List<ShiftRosterEntity> shiftRosterEntities = new ArrayList<>();
        Map.Entry<String, Map<LocalDate, String>> entry = employeeShiftData.entrySet().iterator().next();

        String rowEmpId = entry.getKey();
        Map<LocalDate, String> shifts = entry.getValue();

        // Split the shifts month-wise
        Map<YearMonth, Map<LocalDate, String>> monthWiseShifts = shifts.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> YearMonth.from(e.getKey()),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));

        for (Map.Entry<YearMonth, Map<LocalDate, String>> monthEntry : monthWiseShifts.entrySet()) {
            YearMonth yearMonth = monthEntry.getKey();
            Map<LocalDate, String> monthShifts = monthEntry.getValue();

            Optional<ShiftRosterEntity> existingShiftRosterOpt = shiftRosterRepo.findByEmpIdAndMonthAndYear(
                    Integer.parseInt(rowEmpId), yearMonth.getMonthValue(), yearMonth.getYear());

            ShiftRosterEntity shiftRosterEntity = existingShiftRosterOpt.orElseGet(() -> createNewShiftRosterEntity(empId, rowEmpId, yearMonth.atDay(1)));

            for (Map.Entry<LocalDate, String> shiftEntry : monthShifts.entrySet()) {
                LocalDate date = shiftEntry.getKey();
                String shift = shiftEntry.getValue();

                Integer shiftValue = getShiftValue(shift, errors);
                if (shiftValue == null || shiftValue != -1) {
                    setShiftValue(date.getDayOfMonth(), shiftValue, shiftRosterEntity);
                }  else {
                    errors.add(AppConstant.INVALID_SHIFT + shift);
                }
            }

            // Update timestamps and add to the list if it's new or modified
            shiftRosterEntity.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
            String empName = employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE).get().getEmpName();
            shiftRosterEntity.setUpdatedBy(empName);
            shiftRosterEntities.add(shiftRosterEntity);
        }

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
                return shiftEntity.get().getId();
            } else {
                errors.add(AppConstant.INVALID_SHIFT + shift);
                return -1;
            }
        }
    }

    private void setShiftValue(int day, Integer shift, ShiftRosterEntity shiftRosterEntity) {
        Map<Integer, String> daySetterMap = createDaySetterMap(day);
        String setterMethodName = daySetterMap.get(day);
        if (setterMethodName != null) {
            try {
                if (shift != null) {
                    // Set the shift value if not null
                    shiftRosterEntity.getClass()
                            .getMethod(setterMethodName, Integer.class)
                            .invoke(shiftRosterEntity, shift);
                } else {
                    // Set the shift value as null
                    shiftRosterEntity.getClass()
                            .getMethod(setterMethodName, Integer.class)
                            .invoke(shiftRosterEntity, (Integer) null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Integer, String> createDaySetterMap(int day) {
        Map<Integer, String> daySetterMap = new HashMap<>();
        daySetterMap.put(day, AppConstant.SET_DAY + String.format(AppConstant.STRING_DAY_FORMAT, day));
        return daySetterMap;
    }
}
