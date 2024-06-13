package com.example.shiftroster.persistance.validation;

import com.example.shiftroster.persistance.Exception.MisMatchException;
import com.example.shiftroster.persistance.util.AppConstant;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Configuration
public class BasicValidation {

    public void empIdValidation(String empId) throws MisMatchException {
        if(empId==null || empId.isEmpty()) {
            throw new MisMatchException(AppConstant.EMP_ID_NULL_ERROR);
        }
        if(empId.length() > AppConstant.EMP_ID_LENGTH){
            throw new MisMatchException(AppConstant.EMP_ID_LENGTH_NOT_VALID);
        }
        if(!empId.matches(AppConstant.EMP_ID_REGEX)){
            throw new MisMatchException(AppConstant.EMP_ID_FORMAT_INVALID);
        }
    }

    public void dateValidation(String date) throws MisMatchException {
        if(date==null || date.isEmpty()) {
            throw new MisMatchException(AppConstant.DATE_NULL_ERROR);
        }
        if(date.length() > AppConstant.DATE_STRING_LENGTH){
            throw new MisMatchException(AppConstant.DATE_LENGTH_INVALID);
        }
        if(!date.matches(AppConstant.DATE_REGEX)){
            throw new MisMatchException(AppConstant.INVALID_DATE);
        }

    }

    public void templateTypeValidation(String templateType) throws MisMatchException {
        if(templateType==null || templateType.isEmpty()) {
            throw new MisMatchException(AppConstant.TEMPLATE_TYPE_INVALID);
        }
        if(templateType.matches(AppConstant.TEMPLATE_TYPE_REGEX)){
            throw new MisMatchException(AppConstant.TEMPLATE_TYPE_FORMAT_INVALID);
        }
        if(templateType.length() > AppConstant.TEMPLATE_LENGTH){
            throw new MisMatchException(AppConstant.TEMPLATE_LENGTH_INVALID);
        }
    }


    public void fileValidation(MultipartFile file) throws MisMatchException, IOException {
        if (file == null || file.isEmpty()) {
            throw new MisMatchException(AppConstant.FILE_NOT_EMPTY);
        }
        String contentType = file.getContentType();
        if (!AppConstant.EXCEL_APPLICATION.equals(contentType) && !AppConstant.EXCEL_CONTENT_TYPE.equals(contentType)) {
            throw new MisMatchException(AppConstant.EXCEL_ONLY_ALLOWED);
        }

        List<String> errors = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.EXCEL_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new MisMatchException(AppConstant.HEADER_ROW_MISSING);
            } else {
                // Validate headers
                if (!AppConstant.EMP_ID.equals(headerRow.getCell(0).getStringCellValue())) {
                    throw new MisMatchException(AppConstant.NOT_EMP_ID);
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
                            throw new MisMatchException(AppConstant.DAY_NOT_MATCH_DATE + headerValue + AppConstant.DAY_LOWERCASE_INVALID);
                        }
                    } catch (ParseException e) {
                        throw new MisMatchException(AppConstant.HEADER_INVALID);
                    }
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Validate EmpId column
                Cell empIdCell = row.getCell(0);
                try {
                    if (empIdCell == null || empIdCell.getCellType() == CellType.BLANK) {
                        throw new MisMatchException(AppConstant.INVALID_EMP_ID + (i + 1));
                    }
                    String empId = getStringValueOfCell(empIdCell).trim();
                    if (!empIdValidationBoolean(empId)) {
                        throw new MisMatchException(AppConstant.INVALID_EMP_ID + (i + 1));
                    }
                    empIdValidation(empId);
                } catch (MisMatchException e) {
                    errors.add(AppConstant.INVALID_ROW + (i + 1));
                    continue;
                }

                // Validate other columns
                boolean rowValid = true;
                for (int j = 1; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        break; // Stop checking further if the cell is null or blank
                    }
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue();
                        if (!cellValue.matches(AppConstant.CELL_VALUE_REGEX)) {
                            errors.add(AppConstant.INVALID_ROW + (i + 1));
                            rowValid = false;
                            break;
                        }
                    }
                }

                if (!rowValid) {
                    continue;
                }
            }
        }
        // Throw exception if there are any errors
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                errorMessage.append(errors.get(i)).append(", ");
            }
            throw new MisMatchException(errorMessage.toString());
        }
    }

    private String getStringValueOfCell(Cell cell) {
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
                switch (cell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        double formulaNumericValue = cell.getNumericCellValue();
                        if (formulaNumericValue == Math.floor(formulaNumericValue)) {
                            return String.valueOf((int) formulaNumericValue);
                        } else {
                            return String.valueOf(formulaNumericValue);
                        }
                    case STRING:
                        return cell.getStringCellValue();
                    default:
                        return AppConstant.EMPTY_STRING;
                }
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
        return empId != null && !empId.isEmpty() && empId.matches(AppConstant.EMP_ID_REGEX) || empId.length() <= AppConstant.EMP_ID_LENGTH;
    }
}

