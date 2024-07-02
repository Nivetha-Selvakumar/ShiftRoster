package com.example.shiftroster.persistence.validator;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.validation.BasicValidation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BulkUploadValidatorTest {

    @InjectMocks
    BulkUploadValidator bulkUploadValidator = new BulkUploadValidator();

    @Mock
    BasicValidation basicValidation = new BasicValidation();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void basicValidationTest() throws CommonException, IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Shifts");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("EmpID");
        headerRow.createCell(1).setCellValue("2024-01-01");
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("123");
        dataRow.createCell(1).setCellValue("D");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        InputStream is = new ByteArrayInputStream(bos.toByteArray());
        MultipartFile file = new MockMultipartFile("file", "shifts.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
        bulkUploadValidator.basicValidation("1",file);
        verify(basicValidation,times(1)).empIdValidation("1");
        verify(basicValidation,times(1)).fileValidation(file);
    }
}
