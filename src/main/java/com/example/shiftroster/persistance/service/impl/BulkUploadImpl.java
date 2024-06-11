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
        if (!isExcelFile(file)) {
            throw new NotFoundException(AppConstant.INVALID_FILE);
        }
        businessValidation.employeeValidation(empId);
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        //=================================================

        List<String> header = new ArrayList<>();
        List<String> headerDateList = new ArrayList<>();
        List<Map<String, String>> rows = new ArrayList<>();

        for(Row r : sheet){
            if(r.getRowNum() == 0){
                getValidHeader(r,header);
            }else{
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < header.size(); i++) {
                    Cell cell = r.getCell(i);
                    rowData.put(header.get(i), cell != null ? getCellValueAsString(cell) : "");
                }
                rows.add(rowData);
            }
        }
        headerDateList.addAll(header);
        headerDateList.remove(0);

        List<String> dateList = headerDateList.stream()
                .map(date -> date.split(" ")[0])
                .collect(Collectors.toList());

        checkShiftValidation(empId, rows, dateList);

        workbook.close();
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
                    // Check if the numeric value is an integer
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

    private void checkShiftValidation(String empId, List<Map<String, String>> rows, List<String> dateList) throws CommonException {
        List<Map<String, String>> validEntries = new ArrayList<>();
        List<Map<String, String>> invalidEntries = new ArrayList<>();
        List<Map<String, String>> shiftInvalidEntries = new ArrayList<>();

        // First pass: validate employees
        for (Map<String, String> row : rows) {
            String empIds = row.get(AppConstant.EMP_ID);

            // Check if the employee is valid (exists and is active)
            boolean isValidEmployee = employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empIds), EnumStatus.ACTIVE).isPresent();

            if (isValidEmployee) {
                validEntries.add(row);
            } else {
                invalidEntries.add(row);
            }
        }

        List<Map<String, String>> entriesToRemove = new ArrayList<>();
        for (Map<String, String> row : validEntries) {
            boolean allShiftsValid = true;

            for (Map.Entry<String, String> entry : row.entrySet()) {
                String header = entry.getKey();
                String shift = entry.getValue();

                // Skip empId key
                if (AppConstant.EMP_ID.equals(header)) {
                    continue;
                }

                // Validate shift: valid if null or if it exists and is active
                if (shift != null && !shift.isEmpty()) {
                    Optional<ShiftEntity> shiftEntityOptional = shiftRepo.findByShiftNameAndStatus(shift, EnumStatus.ACTIVE);
                    if (shiftEntityOptional.isEmpty()) {
                        allShiftsValid = false;
                        break;
                    }
                }
            }

            if (!allShiftsValid) {
                shiftInvalidEntries.add(row);
                entriesToRemove.add(row);
            }
        }

        // Remove invalid shift entries from validEntries
        validEntries.removeAll(entriesToRemove);

        validateShiftAssign(empId, validEntries, dateList);

        if (!invalidEntries.isEmpty()) {
            processInvalidEntries(invalidEntries);
        }

        if (!shiftInvalidEntries.isEmpty()) {
            processInvalidShiftEntries(shiftInvalidEntries);
        }
    }

    private void processInvalidShiftEntries(List<Map<String, String>> shiftInvalidEntries) throws CommonException {
        StringBuilder errorMessage = new StringBuilder("Invalid entries found for shifts:");
        for (Map<String, String> entry : shiftInvalidEntries) {
            String empId = entry.get(AppConstant.EMP_ID);
            errorMessage.append("\n Employee with ID ").append(empId).append(" has invalid shifts.");
        }
        throw new CommonException(errorMessage.toString());
    }


    private void processInvalidEntries(List<Map<String, String>> invalidEntries) throws CommonException {
            StringBuilder errorMessage = new StringBuilder("Invalid entries found:");
            for (Map<String, String> entry : invalidEntries) {
                String empId = entry.get(AppConstant.EMP_ID);
                errorMessage.append("\n Employee with ID ").append(empId).append(" is invalid.");
            }
            throw new CommonException(errorMessage.toString());
    }

    private void validateShiftAssign(String empId, List<Map<String, String>> validEntries, List<String> dateList) throws CommonException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map<String, String> entry : validEntries) {
            String empIds = entry.get(AppConstant.EMP_ID);

            for (String dateStr : dateList) {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                int day = date.getDayOfMonth();
                int month = date.getMonthValue();
                int year = date.getYear();

                String matchingHeader = findMatchingHeader(entry.keySet(), dateStr);

                String shift = entry.get(matchingHeader);

                saveShiftsToRoster(empIds, day, month, year, empId, shift);
            }
        }
    }

    private String findMatchingHeader(Set<String> headers, String dateStr) {
        for (String header : headers) {
            if (header.startsWith(dateStr)) {
                return header;
            }
        }
        return null;
    }

    private void saveShiftsToRoster(String empId, int day, int month, int year, String appraiserId, String shift) throws CommonException {
        // Save the shift assignment to the shift roster repository
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
        shiftRosterEntity.setMonth(month);
        shiftRosterEntity.setYear(year);
        setShiftValue(day,shiftValue,shiftRosterEntity);
        String empName = employeeRepo.findByIdAndEmpStatus(Integer.valueOf(appraiserId),EnumStatus.ACTIVE).get().getEmpName();
        shiftRosterEntity.setCreatedBy(empName);
        shiftRosterEntity.setUpdatedBy(empName);

        shiftRosterRepo.save(shiftRosterEntity);
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
        // Check if the file has Excel extension or content type
        return file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx") ||
                file.getContentType().equals("application/vnd.ms-excel") || file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
}

