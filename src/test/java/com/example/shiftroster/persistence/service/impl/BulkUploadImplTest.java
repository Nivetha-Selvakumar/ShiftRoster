package com.example.shiftroster.persistence.service.impl;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validation.BasicValidation;
import com.example.shiftroster.persistence.validation.BusinessValidation;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BulkUploadImplTest {

    @InjectMocks
    BulkUploadImpl bulkUploadImpl;

    @Mock
    ShiftRosterRepo shiftRosterRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private ShiftRepo shiftRepo;

    @Mock
    BusinessValidation businessValidation;

    @Mock
    BasicValidation basicValidation;

    EmployeeEntity employeeEntity = new EmployeeEntity();

    @BeforeEach
    public void setUp() {
        employeeEntity = new EmployeeEntity();
        employeeEntity.setId(1);
        employeeEntity.setEmpStatus(EnumStatus.ACTIVE);
    }

    @Test
    public void testInvalidEmployeeID() throws Exception {
        when(businessValidation.employeeValidation("invalid"))
                .thenThrow(new CommonException("Invalid Employee ID"));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Shifts");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("EmpID");
        headerRow.createCell(1).setCellValue("2024-01-01");
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("invalid");
        dataRow.createCell(1).setCellValue("D");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        InputStream is = new ByteArrayInputStream(bos.toByteArray());

        MockMultipartFile file = new MockMultipartFile("file", "shifts.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
        HttpServletResponse response = mock(HttpServletResponse.class);
        CommonException exception = assertThrows(CommonException.class, () -> bulkUploadImpl.bulkuploadExcelValidation("invalid", file));
        assertEquals("Invalid Employee ID", exception.getMessage());

        verify(shiftRosterRepo, never()).saveAll(anyList());

        workbook.close();
    }

    private MockMultipartFile createMockMultipartFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(AppConstant.EMP_ID);
        headerRow.createCell(1).setCellValue("2024-01-01 MON");
        headerRow.createCell(2).setCellValue("2024-01-02 TUE");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("456");
        dataRow.createCell(1).setCellValue("Day Shift");
        dataRow.createCell(2).setCellValue("Night Shift");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        return new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bos.toByteArray());
    }

    @Test
    public void testBulkuploadExcelValidationInvalidHeader() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bos.toByteArray());

        CommonException exception = assertThrows(CommonException.class, () -> bulkUploadImpl.bulkuploadExcelValidation(AppConstant.EMP_ID, file));
        assertEquals(AppConstant.HEADER_INVALID_NULL, exception.getMessage());
    }

    @Test
    public void testBulkuploadExcelValidationInvalidEmployee() throws Exception {
        MockMultipartFile file = createMockMultipartFile();

        when(businessValidation.employeeValidation(AppConstant.EMP_ID)).thenThrow(new CommonException(AppConstant.INVALID_EMPLOYEE));

        CommonException exception = assertThrows(CommonException.class, () -> bulkUploadImpl.bulkuploadExcelValidation(AppConstant.EMP_ID, file));
        assertEquals(AppConstant.INVALID_EMPLOYEE, exception.getMessage());
    }
}
