package com.example.shiftroster.persistance.service.impl;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.Exception.NotFoundException;
import com.example.shiftroster.persistance.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistance.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistance.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistance.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistance.service.BulkUploadService;
import com.example.shiftroster.persistance.util.AppConstant;
import com.example.shiftroster.persistance.validation.BusinessValidation;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    @Override
    public void bulkuploadExcelValidation(String empId, MultipartFile file) throws IOException, CommonException {
        // Validate if the file is an Excel file
        if (!isExcelFile(file)) {
            throw new NotFoundException(AppConstant.INVALID_FILE);
        }

        // Validate the employee ID
        businessValidation.employeeValidation(empId);

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<String> header = new ArrayList<>();
        List<Map<String, String>> rows = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Extract data from the sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Get headers
                getValidHeader(row, header);
            } else {
                // Extract row data
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < header.size(); i++) {
                    Cell cell = row.getCell(i);
                    rowData.put(header.get(i), cell != null ? getCellValueAsString(cell) : "");
                }
                rows.add(rowData);
            }
        }

        // Process each row individually
        for (Map<String, String> row : rows) {
            try {
                processRow(empId, row, header);
            } catch (CommonException e) {
                errors.add(e.getMessage());
            }
        }

        // Close the workbook
        workbook.close();

        // If there were any errors, throw them as a single exception
        if (!errors.isEmpty()) {
            throw new CommonException(String.join("\n", errors));
        }
    }

    private void processRow(String empId, Map<String, String> row, List<String> header) throws CommonException {
        // Validate employee ID in the row
        String rowEmpId = row.get(AppConstant.EMP_ID);
        if (!employeeRepo.findByIdAndEmpStatus(Integer.valueOf(rowEmpId), EnumStatus.ACTIVE).isPresent()) {
            throw new CommonException("Invalid employee ID: " + rowEmpId);
        }

        // Validate each shift
        for (String dateHeader : header) {
            if (dateHeader.equals(AppConstant.EMP_ID)) {
                continue;
            }
            String shift = row.get(dateHeader);
            if (shift != null && !shift.isEmpty()) {
                if (shiftRepo.findByShiftNameAndStatus(shift, EnumStatus.ACTIVE).isEmpty()) {
                    throw new CommonException("Invalid shift for employee " + rowEmpId + " on date " + dateHeader + ": " + shift);
                }
            }
        }

        // If validation passes, save the shifts
        saveShiftsToRoster(rowEmpId, row, header);
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

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (int) numericValue) {
                        return String.valueOf((int) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private void saveShiftsToRoster(String empId, Map<String, String> row, List<String> header) throws CommonException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (String dateHeader : header) {
            if (dateHeader.equals(AppConstant.EMP_ID)) {
                continue;
            }
            String shift = row.get(dateHeader);
            LocalDate date = LocalDate.parse(dateHeader.split(" ")[0], formatter);

            ShiftRosterEntity shiftRosterEntity = new ShiftRosterEntity();
            Integer shiftValue = null;
            if ("UA".equalsIgnoreCase(shift)) {
                shiftValue = null;
            } else if ("WO".equalsIgnoreCase(shift)) {
                shiftValue = 0;
            } else if (shift != null) {
                ShiftEntity shiftEntity = shiftRepo.findByShiftName(shift);
                if (shiftEntity != null) {
                    shiftValue = shiftEntity.getId();
                }
            }

            shiftRosterEntity.setEmpId(Integer.valueOf(empId));
            shiftRosterEntity.setMonth(date.getMonthValue());
            shiftRosterEntity.setYear(date.getYear());
            setShiftValue(date.getDayOfMonth(), shiftValue, shiftRosterEntity);
            String empName = employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE).get().getEmpName();
            shiftRosterEntity.setCreatedBy(empName);
            shiftRosterEntity.setUpdatedBy(empName);

            shiftRosterRepo.save(shiftRosterEntity);
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
            daySetterMap.put(i, "setDay" + String.format("%02d", i));
        }
        return daySetterMap;
    }

    private boolean isExcelFile(MultipartFile file) {
        return file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx") ||
                file.getContentType().equals("application/vnd.ms-excel") || file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
}
