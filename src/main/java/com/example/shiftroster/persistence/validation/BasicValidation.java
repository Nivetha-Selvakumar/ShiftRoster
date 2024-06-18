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
            throw new MisMatchException(AppConstant.INVALID_EMPLOYEE_ID);
        }
        if(empId.length() > AppConstant.EMP_ID_LENGTH){
            throw new MisMatchException(AppConstant.INVALID_EMPLOYEE_ID);
        }
        if(!empId.matches(AppConstant.EMP_ID_REGEX)){
            throw new MisMatchException(AppConstant.INVALID_EMPLOYEE_ID);
        }
    }

    public void dateValidation(String date) throws MisMatchException {
        if(date==null || date.isEmpty()) {
            throw new MisMatchException(AppConstant.INVALID_DATE);
        }
        if(date.length() > AppConstant.DATE_STRING_LENGTH){
            throw new MisMatchException(AppConstant.INVALID_DATE);
        }
        if(!date.matches(AppConstant.DATE_REGEX)){
            throw new MisMatchException(AppConstant.INVALID_DATE);
        }

    }

    public void templateTypeValidation(String templateType) throws MisMatchException {
        if(templateType==null || templateType.isEmpty()) {
            throw new MisMatchException(AppConstant.INVALID_TEMPLATE_TYPE);
        }
        if(templateType.matches(AppConstant.TEMPLATE_TYPE_REGEX)){
            throw new MisMatchException(AppConstant.INVALID_TEMPLATE_TYPE);
        }
        if(templateType.length() > AppConstant.TEMPLATE_LENGTH){
            throw new MisMatchException(AppConstant.INVALID_TEMPLATE_TYPE);
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
                throw new MisMatchException(AppConstant.HEADER_INVALID);
            } else {
                // Validate headers
                if (!AppConstant.EMP_ID.equals(headerRow.getCell(0).getStringCellValue())) {
                    throw new MisMatchException(AppConstant.HEADER_INVALID);
                }
                for (int i = 1; i < headerRow.getLastCellNum(); i++) {
                    Cell headerCell = headerRow.getCell(i);
                    if (headerCell == null || headerCell.getCellType() != CellType.STRING || headerCell.getStringCellValue().trim().isEmpty()) {
                        break; // Stop the iteration if header cell is null or empty
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
                        if (!dateParts[1].toUpperCase().equals(AppConstant.OPEN_BRACKET + expectedDay + AppConstant.CLOSE_BRACKET)) {
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
            case BLANK:
                return AppConstant.EMPTY_STRING;
            default:
                return AppConstant.EMPTY_STRING;
        }
    }

    private String getDayOfWeekName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return AppConstant.SUN;
            case Calendar.MONDAY:
                return AppConstant.MON;
            case Calendar.TUESDAY:
                return AppConstant.TUE;
            case Calendar.WEDNESDAY:
                return AppConstant.WED;
            case Calendar.THURSDAY:
                return AppConstant.THU;
            case Calendar.FRIDAY:
                return AppConstant.FRI;
            case Calendar.SATURDAY:
                return AppConstant.SAT;
            default:
                return AppConstant.EMPTY_STRING;
        }
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
            errors.add(AppConstant.INVALID_DATA_IN_ROW + rowNum);
            return errors;
        } else {
            String empId = getStringValueOfCell(empIdCell).trim();
            if (!empIdValidationBoolean(empId)) {
                errors.add(AppConstant.INVALID_DATA_IN_ROW + rowNum);
                return errors;
            }
        }

        // Validate other columns based on headers
        for (int i = 1; i < headerRow.getLastCellNum(); i++) {
            Cell headerCell = headerRow.getCell(i);

            // Stop validation if the header is null
            if (headerCell == null || headerCell.getCellType() == CellType.BLANK) {
                break;
            }

            Cell cell = row.getCell(i);

            if (cell == null || cell.getCellType() == CellType.BLANK) {
                errors.add(AppConstant.INVALID_DATA_IN_ROW + rowNum);

                continue; // Skip further validation for this cell
            }

            if (cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue().trim();
                if (!cellValue.matches(AppConstant.CELL_VALUE_REGEX)) {
                    errors.add(AppConstant.INVALID_DATA_IN_ROW + rowNum);
                }
            } else {
                errors.add(AppConstant.INVALID_DATA_IN_ROW + rowNum);
            }
        }

        // If errors found, add the row number to processedRows to avoid reprocessing
        if (!errors.isEmpty()) {
            processedRows.add(rowNum);
        }
        return errors;
    }
}
