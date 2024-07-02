package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Exception.MisMatchException;
import com.example.shiftroster.persistence.util.AppConstant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BasicValidationTest {

    @InjectMocks
    BasicValidation basicValidation = new BasicValidation();

    @Test
    public void testEmpIdValidationLength() {
        String empId = "1234567898765432123890987654567894";
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.empIdValidation(empId);
        });
        assertEquals(AppConstant.EMPLOYEE_ID_LENGTH_INVALID, exception.getMessage());
    }

    @Test
    public void testEmpIdValidationNull() {
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.empIdValidation(null);
        });
        assertEquals(AppConstant.EMPLOYEE_ID_NULL, exception.getMessage());
    }

    @Test
    public void testEmpIdValidationRegex() {
        String empId = "adfd";
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.empIdValidation(empId);
        });
        assertEquals(AppConstant.EMPLOYEE_ID_FORMAT_INVALID, exception.getMessage());
    }

    @Test
    public void testDateValidationLength() {
        String date = "202409099";
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.dateValidation(date);
        });
        assertEquals(AppConstant.INVALID_DATE_LENGTH, exception.getMessage());
    }

    @Test
    public void testDateValidationNull() {
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.dateValidation(null);
        });
        assertEquals(AppConstant.INVALID_DATE_NULL, exception.getMessage());
    }

    @Test
    public void testDateValidationRegex() {
        String date = "asdfds";
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.dateValidation(date);
        });
        assertEquals(AppConstant.INVALID_DATE_FORMAT, exception.getMessage());
    }

    @Test
    public void testTemplateValidationLength() {
        String templateType = "qwertyuiopoiuytrewawertyuiopdfghjk";
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.templateTypeValidation(templateType);
        });
        assertEquals(AppConstant.INVALID_TEMPLATE_TYPE_LENGTH, exception.getMessage());
    }

    @Test
    public void testTemplateValidationNull() {
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.templateTypeValidation(null);
        });
        assertEquals(AppConstant.INVALID_TEMPLATE_TYPE_NULL, exception.getMessage());
    }

    @Test
    public void testTemplateValidationRegex() {
        String templateType = "a123";
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.templateTypeValidation(templateType);
        });
        assertEquals(AppConstant.INVALID_TEMPLATE_TYPE_FORMAT, exception.getMessage());
    }

    @Test
    public void fileValidationNull(){
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(null);
        });
        assertEquals(AppConstant.FILE_NOT_EMPTY, exception.getMessage());
    }

    @Test
    public void testFileValidationEmptyFile() {
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(emptyFile);
        });
        assertEquals(AppConstant.FILE_NOT_EMPTY, exception.getMessage());
    }

    @Test
    public void testFileValidationInvalidFileType(){
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.isEmpty()).thenReturn(false);
        when(invalidFile.getContentType()).thenReturn("text/plain");
        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(invalidFile);
        });
        assertEquals(AppConstant.INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void testValidateRowBasicEmptyRow() {
        Row row = mock(Row.class);
        Row headerRow = mock(Row.class);
        Set<Integer> processedRows = new HashSet<>();

        when(row.getRowNum()).thenReturn(0);

        List<String> errors = basicValidation.validateRowBasic(row, headerRow, processedRows);
        assertEquals(1, errors.size());
        assertEquals(String.format(AppConstant.INVALID_DATA_IN_ROW, 1) + AppConstant.EMPLOYEE_ID_NULL, errors.get(0));
    }

    @Test
    public void testValidateRowBasicInvalidEmpId() {
        Row row = mock(Row.class);
        Row headerRow = mock(Row.class);
        Set<Integer> processedRows = new HashSet<>();
        Cell empIdCell = mock(Cell.class);

        when(row.getRowNum()).thenReturn(0);
        when(row.getCell(0)).thenReturn(empIdCell);
        when(empIdCell.getCellType()).thenReturn(CellType.STRING);
        when(empIdCell.getStringCellValue()).thenReturn("invalidEmpId");

        List<String> errors = basicValidation.validateRowBasic(row, headerRow, processedRows);
        assertEquals(1, errors.size());
        assertEquals(String.format(AppConstant.INVALID_DATA_IN_ROW, 1) + AppConstant.EMPLOYEE_ID_FORMAT_INVALID, errors.get(0));
    }

    @Test
    public void testValidateRowBasicValidRow() {
        Row row = mock(Row.class);
        Row headerRow = mock(Row.class);
        Set<Integer> processedRows = new HashSet<>();
        Cell empIdCell = mock(Cell.class);

        when(row.getRowNum()).thenReturn(0);
        when(row.getCell(0)).thenReturn(empIdCell);
        when(empIdCell.getCellType()).thenReturn(CellType.STRING);
        when(empIdCell.getStringCellValue()).thenReturn("12345");

        Cell headerCell = mock(Cell.class);
        when(headerRow.getCell(1)).thenReturn(headerCell);
        when(headerCell.getCellType()).thenReturn(CellType.STRING);
        when(headerCell.getStringCellValue()).thenReturn("2024-07-01 MONDAY");

        Cell cell = mock(Cell.class);
        when(row.getCell(1)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("Some valid data");

        List<String> errors = basicValidation.validateRowBasic(row, headerRow, processedRows);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateRowBasicInvalidRow() {
        Row row = mock(Row.class);
        Row headerRow = mock(Row.class);
        Cell empIdCell = mock(Cell.class);
        when(row.getRowNum()).thenReturn(1);
        when(row.getCell(0)).thenReturn(empIdCell);

        when(empIdCell.getCellType()).thenReturn(CellType.BLANK);

        Set<Integer> processedRows = new HashSet<>();
        List<String> errors = basicValidation.validateRowBasic(row, headerRow, processedRows);
        assertFalse(errors.isEmpty());
        assertTrue(errors.contains(String.format(AppConstant.INVALID_DATA_IN_ROW, 2) + AppConstant.EMPLOYEE_ID_NULL));
    }
}
