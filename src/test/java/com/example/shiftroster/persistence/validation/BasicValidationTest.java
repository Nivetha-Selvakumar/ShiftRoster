package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Exception.MisMatchException;
import com.example.shiftroster.persistence.util.AppConstant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
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
        when(headerCell.getStringCellValue()).thenReturn("2024-07-01 MON");

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

    @Test
    public void testFileValidationHeaderNull() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();

        MultipartFile invalidFile = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(invalidFile);
        });

        assertEquals(AppConstant.HEADER_INVALID_NULL, exception.getMessage());
    }

    @Test
    public void testFileValidationEmployeeIdNull() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Row headerRow = sheet.createRow(0);
        Cell firstCell = headerRow.createCell(0);
        firstCell.setCellValue("WrongHeader");

        for (int i = 1; i <= 5; i++) {
            headerRow.createCell(i).setCellValue("Header" + i);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();

        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelBytes
        );

        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(invalidFile);
        });

        assertEquals(AppConstant.HEADER_INVALID_EMP_ID, exception.getMessage());
    }

    @Test
    public void testFileValidationMissingHeaderValueCatch() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        Row headerRow = sheet.createRow(0);
        Cell firstCell = headerRow.createCell(0);
        firstCell.setCellValue(AppConstant.EMP_ID);

        headerRow.createCell(1).setCellValue("2024-01-01 MON");
        headerRow.createCell(2).setCellValue("");
        headerRow.createCell(3).setCellValue("2024-01-03 WED");
        headerRow.createCell(4).setCellValue("2024-01-04 THU");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();

        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelBytes
        );

        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(invalidFile);
        });
        assertEquals(AppConstant.HEADER_INVALID, exception.getMessage());
    }

    @Test
    public void testFileValidationTry() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        Row headerRow = sheet.createRow(0);
        Cell firstCell = headerRow.createCell(0);
        firstCell.setCellValue(AppConstant.EMP_ID);

        headerRow.createCell(1).setCellValue("01/01/2024 (MON)");
        headerRow.createCell(2).setCellValue("02/01/2024 (TUE)");
        headerRow.createCell(3).setCellValue("03/01/2024 (WED)");
        headerRow.createCell(4).setCellValue("04/01/2024 (THU)");
        headerRow.createCell(4).setCellValue("05/01/2024 (FRI)");
        headerRow.createCell(4).setCellValue("06/01/2024 (SAT)");
        headerRow.createCell(4).setCellValue("07/01/2024 (SAT)");
//        headerRow.createCell(4).setCellValue("08/01/2024 (TUE)");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();

        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelBytes
        );

        MisMatchException exception = assertThrows(MisMatchException.class, () -> {
            basicValidation.fileValidation(invalidFile);
        });
        assertEquals(AppConstant.INVALID_DATE_HEADER + "07/01/2024 (SAT)", exception.getMessage());
    }
}
