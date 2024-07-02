package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Exception.MisMatchException;
import com.example.shiftroster.persistence.util.AppConstant;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
public class BasicValidation {

    public void empIdValidation(String empId) throws MisMatchException {
        if(empId==null || empId.isEmpty()) {
            throw new MisMatchException(AppConstant.EMPLOYEE_ID_NULL);
        }
        if(empId.length() > AppConstant.EMP_ID_LENGTH){
            throw new MisMatchException(AppConstant.EMPLOYEE_ID_LENGTH_INVALID);
        }
        if(!empId.matches(AppConstant.EMP_ID_REGEX)){
            throw new MisMatchException(AppConstant.EMPLOYEE_ID_FORMAT_INVALID);
        }
    }

    public void dateValidation(String date) throws MisMatchException {
        if(date==null || date.isEmpty()) {
            throw new MisMatchException(AppConstant.INVALID_DATE_NULL);
        }
        if(date.length() > AppConstant.DATE_STRING_LENGTH){
            throw new MisMatchException(AppConstant.INVALID_DATE_LENGTH);
        }
        if(!date.matches(AppConstant.DATE_REGEX)){
            throw new MisMatchException(AppConstant.INVALID_DATE_FORMAT);
        }

    }

    public void templateTypeValidation(String templateType) throws MisMatchException {
        if(templateType==null || templateType.isEmpty()) {
            throw new MisMatchException(AppConstant.INVALID_TEMPLATE_TYPE_NULL);
        }
        if(templateType.length() > AppConstant.TEMPLATE_LENGTH){
            throw new MisMatchException(AppConstant.INVALID_TEMPLATE_TYPE_LENGTH);
        }
        if(!templateType.matches(AppConstant.TEMPLATE_TYPE_REGEX)){
            throw new MisMatchException(AppConstant.INVALID_TEMPLATE_TYPE_FORMAT);
        }
    }

    public List<String> fileValidation(MultipartFile file) throws MisMatchException, IOException {
        if (file == null || file.isEmpty()) {
            throw new MisMatchException(AppConstant.FILE_NOT_EMPTY);
        }
        String contentType = file.getContentType();
        if (!AppConstant.EXCEL_APPLICATION.equals(contentType) && !AppConstant.EXCEL_CONTENT_TYPE.equals(contentType)) {
            throw new MisMatchException(AppConstant.INVALID_FILE_TYPE);
        }
        List<String> errors = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.EXCEL_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new MisMatchException(AppConstant.HEADER_INVALID_NULL);
            } else {
                // Validate headers
                if (!AppConstant.EMP_ID.equals(headerRow.getCell(0).getStringCellValue())) {
                    throw new MisMatchException(AppConstant.HEADER_INVALID_EMP_ID);
                }

                boolean previousCellWasEmpty = false;

                for (int i = 1; i < headerRow.getLastCellNum(); i++) {
                    Cell headerCell = headerRow.getCell(i);
                    if (headerCell == null || headerCell.getCellType() != CellType.STRING || headerCell.getStringCellValue().trim().isEmpty()) {
                        if (i < headerRow.getLastCellNum() - 1) {
                            // If the cell is empty, and it's not the last cell, mark previousCellWasEmpty as true
                            previousCellWasEmpty = true;
                            continue;
                        } else {
                            break; // Stop the iteration if header cell is null or empty and it's the last cell
                        }
                    }
                    if (previousCellWasEmpty) {
                        // If the previous cell was empty and current cell is not empty, throw an exception
                        throw new MisMatchException(AppConstant.MISSING_HEADER_VALUE);
                    }

                    String headerValue = headerCell.getStringCellValue();
                    String[] dateParts = headerValue.split(AppConstant.STRING_SPACE);
                    if (dateParts.length != 2) {
                        throw new MisMatchException(AppConstant.HEADER_INVALID);
                    }

                    try {
                        Date date = sdf.parse(dateParts[0]);
                        calendar.setTime(date);
                        String expectedDay = getDayOfWeekName(calendar.get(Calendar.DAY_OF_WEEK)).toUpperCase();
                        if (!dateParts[1].toUpperCase().equals(String.format(AppConstant.EXPECTED_DAY,expectedDay))) {
                            throw new MisMatchException(AppConstant.INVALID_DATE_HEADER + headerValue);
                        }
                    } catch (ParseException e) {
                        throw new MisMatchException(AppConstant.HEADER_INVALID);
                    }
                }
            }
        }
        return errors;
    }

    public String getStringValueOfCell(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat(AppConstant.EXCEL_DATE_FORMAT).format(cell.getDateCellValue());
                } else {
                    // Handle numbers as integers if they have ".0"
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((int) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return AppConstant.EMPTY_STRING;
        }
    }

    private String getDayOfWeekName(int dayOfWeek) {
        return switch (dayOfWeek) {
            case Calendar.SUNDAY -> AppConstant.SUN;
            case Calendar.MONDAY -> AppConstant.MON;
            case Calendar.TUESDAY -> AppConstant.TUE;
            case Calendar.WEDNESDAY -> AppConstant.WED;
            case Calendar.THURSDAY -> AppConstant.THU;
            case Calendar.FRIDAY -> AppConstant.FRI;
            case Calendar.SATURDAY -> AppConstant.SAT;
            default -> AppConstant.EMPTY_STRING;
        };
    }

    private boolean empIdValidationBoolean(String empId) {
        return empId != null && !empId.isEmpty() && empId.matches(AppConstant.EMP_ID_REGEX) && empId.length() <= AppConstant.EMP_ID_LENGTH;
    }

    public List<String> validateRowBasic(Row row, Row headerRow, Set<Integer> processedRows) {
        List<String> errors = new ArrayList<>();
        int rowNum = row.getRowNum() + 1;

        // Check if the row has already been processed and had errors
        if (processedRows.contains(rowNum)) {
            return errors;
        }

        // Validate EmpId column
        Cell empIdCell = row.getCell(0);
        if (empIdCell == null || empIdCell.getCellType() == CellType.BLANK) {
            errors.add(String.format(AppConstant.INVALID_DATA_IN_ROW, rowNum) + AppConstant.EMPLOYEE_ID_NULL);
            return errors;
        } else {
            String empId = getStringValueOfCell(empIdCell).trim();
            if (!empIdValidationBoolean(empId)) {
                errors.add(String.format(AppConstant.INVALID_DATA_IN_ROW, rowNum) + AppConstant.EMPLOYEE_ID_FORMAT_INVALID);
                return errors;
            }
        }

        // Validate other columns based on headers
        int headerCellCount = headerRow.getLastCellNum();
        for (int i = 1; i < row.getLastCellNum(); i++) {
            if (i >= headerCellCount) {
                // There are more columns in the row than in the header
                errors.add(String.format(AppConstant.EMP_ID_INVALID,getStringValueOfCell(empIdCell),rowNum));
                break;
            }

            Cell headerCell = headerRow.getCell(i);

            // Stop validation if the header is null
            if (headerCell == null || headerCell.getCellType() == CellType.BLANK) {
                break;
            }

            Cell cell = row.getCell(i);

            if (cell == null || cell.getCellType() == CellType.BLANK) {
                errors.add(String.format(AppConstant.INVALID_DATA_IN_ROW, rowNum) + AppConstant.EMPTY_CELL);
                break; // Skip further validation for this cell
            }

            if (cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue().trim();
                if (!cellValue.matches(AppConstant.CELL_VALUE_REGEX)) {
                    errors.add(String.format(AppConstant.INVALID_DATA_IN_ROW, rowNum) + AppConstant.SPECIAL_CHARACTER_NOT_ALLOWED);
                }
            } else {
                errors.add(String.format(AppConstant.INVALID_DATA_IN_ROW, rowNum));
            }
        }

        // If errors found, add the row number to processedRows to avoid reprocessing
        if (!errors.isEmpty()) {
            processedRows.add(rowNum);
        }
        return errors;
    }
}

