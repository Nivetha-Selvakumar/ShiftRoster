package com.example.shiftroster.persistence.service.impl;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validation.BasicValidation;
import com.example.shiftroster.persistence.validation.BusinessValidation;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
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

    ShiftEntity shiftEntity=new ShiftEntity();

    EmployeeEntity appraiser = new EmployeeEntity();

    List<ShiftRosterEntity> shiftRosterEntityList = new ArrayList<>();

    ShiftRosterEntity shiftRosterEntity = new ShiftRosterEntity();

    EmployeeEntity reportee = new EmployeeEntity();

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

    @Test
    public void testBulkuploadExcelValidation() throws IOException, CommonException {
        byte[] excelBytes;

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");

            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Column1");
            headerRow.createCell(1).setCellValue("Column2");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("InvalidData1");
            dataRow1.createCell(1).setCellValue("InvalidData2");

            workbook.write(bos);

            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));

            when(businessValidation.employeeValidation(anyString())).thenReturn(employeeEntity);

            List<String> mockErrors = List.of(AppConstant.INVALID_DATA_IN_ROW);
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
            when(mockAttributes.getResponse()).thenReturn(mockResponse);
            RequestContextHolder.setRequestAttributes(mockAttributes);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
            when(mockResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }

                @Override
                public void write(int b) {
                    responseOutputStream.write(b);
                }
            });
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(basicValidation, times(1)).validateRowBasic(any(Row.class), any(Row.class), anySet());
            verify(mockResponse, atLeastOnce()).setContentType(AppConstant.EXCEL_CONTENT_TYPE);
            verify(mockResponse, atLeastOnce()).setHeader(eq(AppConstant.CONTENT_DISPOSITION), eq(AppConstant.ERROR_FILE_NAME));
        }
    }

    @Test
    public void testBulkuploadExcelValidationTryShift() throws IOException, CommonException {
        byte[] excelBytes;

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");

            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("1");
            headerRow.createCell(1).setCellValue("Shift 1");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("2");
            dataRow1.createCell(1).setCellValue("Shift 2");

            workbook.write(bos);

            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));

            appraiser.setId(2);
            when(businessValidation.employeeValidation(anyString())).thenReturn(appraiser);

            List<String> mockErrors = new ArrayList<>();
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
            Map<LocalDate,String> shift = new HashMap<>();
            Cell empIdCell = dataRow1.getCell(0);
            shift.put(LocalDate.parse("2024-09-09"),"Shift 1");
            employeeShiftData.put("2",shift);

            reportee.setId(1);
            reportee.setAppraiserId(appraiser);
            employeeEntity.setId(1);
            employeeEntity.setAppraiserId(appraiser);
            shiftEntity.setId(1);
            shiftEntity.setShiftName("Shift 1");

            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("1");
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
            when(mockAttributes.getResponse()).thenReturn(mockResponse);
            RequestContextHolder.setRequestAttributes(mockAttributes);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
            when(mockResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }

                @Override
                public void write(int b) {
                    responseOutputStream.write(b);
                }
            });
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(basicValidation, times(1)).validateRowBasic(any(Row.class), any(Row.class), anySet());
            verify(mockResponse, atLeastOnce()).setContentType(AppConstant.EXCEL_CONTENT_TYPE);
            verify(mockResponse, atLeastOnce()).setHeader(eq(AppConstant.CONTENT_DISPOSITION), eq(AppConstant.ERROR_FILE_NAME));
        }
    }

    @Test
    public void testBulkuploadExcelValidationParseDate() throws IOException, CommonException {
        byte[] excelBytes;

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");

            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("1");
            headerRow.createCell(1).setCellValue("Shift 1");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("2");
            dataRow1.createCell(1).setCellValue("Shift 2");

            workbook.write(bos);

            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));

            appraiser.setId(2);
            when(businessValidation.employeeValidation(anyString())).thenReturn(appraiser);

            List<String> mockErrors = new ArrayList<>();
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
            Map<LocalDate,String> shift = new HashMap<>();
            Cell empIdCell = dataRow1.getCell(0);
            shift.put(LocalDate.parse("2024-09-09"),"Shift 1");
            employeeShiftData.put("2",shift);
            reportee.setId(1);
            reportee.setAppraiserId(appraiser);
            employeeEntity.setId(1);
            employeeEntity.setAppraiserId(appraiser);
            shiftEntity.setId(1);
            shiftEntity.setShiftName("Shift 1");

            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("1");
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(shiftRepo.findByShiftNameAndStatus(Mockito.anyString(),Mockito.any())).thenReturn(Optional.of(shiftEntity));
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
            when(mockAttributes.getResponse()).thenReturn(mockResponse);
            RequestContextHolder.setRequestAttributes(mockAttributes);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
            when(mockResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }

                @Override
                public void write(int b) {
                    responseOutputStream.write(b);
                }
            });
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(basicValidation, times(1)).validateRowBasic(any(Row.class), any(Row.class), anySet());
            verify(mockResponse, atLeastOnce()).setContentType(AppConstant.EXCEL_CONTENT_TYPE);
            verify(mockResponse, atLeastOnce()).setHeader(eq(AppConstant.CONTENT_DISPOSITION), eq(AppConstant.ERROR_FILE_NAME));
        }
    }

    @Test
    public void reporteeIdAppraiserIdNullTest() throws IOException, CommonException {
        byte[] excelBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");
            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("EmpId");
            headerRow.createCell(1).setCellValue("06/07/2024 (SAT)");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("2");
            dataRow1.createCell(1).setCellValue("Shift 2");
            workbook.write(bos);
            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));
            when(businessValidation.employeeValidation(anyString())).thenReturn(employeeEntity);

            List<String> mockErrors = new ArrayList<>();
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
            Map<LocalDate,String> shift = new HashMap<>();
            employeeEntity.setId(1);
            employeeEntity.setEmpStatus(EnumStatus.ACTIVE);

            reportee.setId(2);
            reportee.setAppraiserId(reportee);
            reportee.setEmpStatus(EnumStatus.ACTIVE);
            Cell empIdCell = dataRow1.getCell(0);
            shift.put(LocalDate.parse("2024-07-06"),"Shift 1");
            employeeShiftData.put("2",shift);

            shiftEntity.setId(1);
            shiftEntity.setShiftName("Shift 1");

            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
            when(mockAttributes.getResponse()).thenReturn(mockResponse);
            RequestContextHolder.setRequestAttributes(mockAttributes);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
            when(mockResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }

                @Override
                public void write(int b) {
                    responseOutputStream.write(b);
                }
            });
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(basicValidation, times(1)).validateRowBasic(any(Row.class), any(Row.class), anySet());
            verify(mockResponse, atLeastOnce()).setContentType(AppConstant.EXCEL_CONTENT_TYPE);
            verify(mockResponse, atLeastOnce()).setHeader(eq(AppConstant.CONTENT_DISPOSITION), eq(AppConstant.ERROR_FILE_NAME));
        }
    }

    @Test
    public void reporteeIdAppraiserIdEqualTest() throws IOException, CommonException {
        byte[] excelBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");
            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("EmpId");
            headerRow.createCell(1).setCellValue("06/07/2024 (SAT)");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("2");
            dataRow1.createCell(1).setCellValue("Shift 2");
            workbook.write(bos);
            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));
            when(businessValidation.employeeValidation(anyString())).thenReturn(employeeEntity);

            List<String> mockErrors = new ArrayList<>();
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
            Map<LocalDate,String> shift = new HashMap<>();
            employeeEntity.setId(1);
            employeeEntity.setEmpStatus(EnumStatus.ACTIVE);

            Cell empIdCell = dataRow1.getCell(0);
            shift.put(LocalDate.parse("2024-07-06"),"Shift 1");
            employeeShiftData.put("2",shift);

            shiftEntity.setId(1);
            shiftEntity.setShiftName("Shift 1");

            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(employeeEntity));
            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
            when(mockAttributes.getResponse()).thenReturn(mockResponse);
            RequestContextHolder.setRequestAttributes(mockAttributes);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
            when(mockResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }

                @Override
                public void write(int b) {
                    responseOutputStream.write(b);
                }
            });
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(basicValidation, times(1)).validateRowBasic(any(Row.class), any(Row.class), anySet());
            verify(mockResponse, atLeastOnce()).setContentType(AppConstant.EXCEL_CONTENT_TYPE);
            verify(mockResponse, atLeastOnce()).setHeader(eq(AppConstant.CONTENT_DISPOSITION), eq(AppConstant.ERROR_FILE_NAME));
        }
    }

    @Test
    public void testBulkuploadExcelValidationTryTrue() throws IOException, CommonException {
        byte[] excelBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");
            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("EmpId");
            headerRow.createCell(1).setCellValue("06/07/2024 (SAT)");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("2");
            dataRow1.createCell(1).setCellValue("Shift 2");

            workbook.write(bos);

            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));

            when(businessValidation.employeeValidation(anyString())).thenReturn(employeeEntity);

            List<String> mockErrors = new ArrayList<>();
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
            Map<LocalDate,String> shift = new HashMap<>();
            employeeEntity.setId(1);
            employeeEntity.setEmpStatus(EnumStatus.ACTIVE);

            reportee.setId(2);
            reportee.setAppraiserId(employeeEntity);
            reportee.setEmpStatus(EnumStatus.ACTIVE);
            Cell empIdCell = dataRow1.getCell(0);
            shift.put(LocalDate.parse("2024-07-06"),"Shift 1");
            employeeShiftData.put("2",shift);

            shiftEntity.setId(1);
            shiftEntity.setShiftName("Shift 1");

            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(shiftRepo.findByShiftNameAndStatus(Mockito.anyString(),Mockito.any())).thenReturn(Optional.of(shiftEntity));
            when(businessValidation.validateShiftDate(Mockito.any(),Mockito.anyList())).thenReturn(true);

            shiftRosterEntity.setEmpId(2);
            shiftRosterEntity.setMonth(7);
            shiftRosterEntity.setYear(2024);
            shiftRosterEntityList.add(shiftRosterEntity);

            when(shiftRosterRepo.findByEmpIdAndMonthAndYear(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.of(shiftRosterEntity));
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(shiftRosterRepo).saveAll(shiftRosterEntityList);
        }
    }

//    @Test
    public void createNewEntityTest() throws IOException, CommonException {
        byte[] excelBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");
            // Add header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("EmpId");
            headerRow.createCell(1).setCellValue("06/07/2024 (SAT)");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("2");
            dataRow1.createCell(1).setCellValue("Shift 1");

            workbook.write(bos);

            excelBytes = bos.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(excelBytes));

            when(businessValidation.employeeValidation(anyString())).thenReturn(employeeEntity);

            List<String> mockErrors = new ArrayList<>();
            when(basicValidation.validateRowBasic(any(Row.class), any(Row.class), anySet())).thenReturn(mockErrors);

            Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
            Map<LocalDate,String> shift = new HashMap<>();
            employeeEntity.setId(1);
            employeeEntity.setEmpStatus(EnumStatus.ACTIVE);

            reportee.setId(2);
            reportee.setAppraiserId(employeeEntity);
            reportee.setEmpStatus(EnumStatus.ACTIVE);
            Cell empIdCell = dataRow1.getCell(0);
            shift.put(LocalDate.parse("2024-07-06"),"Shift 1");
            employeeShiftData.put("2",shift);

            shiftEntity.setId(1);
            shiftEntity.setShiftName("Shift 1");

            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            when(basicValidation.getStringValueOfCell(Mockito.any())).thenReturn("2");
            when(shiftRepo.findByShiftNameAndStatus(Mockito.anyString(),Mockito.any())).thenReturn(Optional.of(shiftEntity));
            when(businessValidation.validateShiftDate(Mockito.any(),Mockito.anyList())).thenReturn(true);
            when(shiftRosterRepo.findByEmpIdAndMonthAndYear(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.empty());
            when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(reportee));
            shiftRosterEntity.setEmpId(2);
            shiftRosterEntity.setDay06(2);
            shiftRosterEntity.setMonth(7);
            shiftRosterEntity.setYear(2024);
            shiftRosterEntity.setCreatedBy("Nivetha");
            shiftRosterEntity.setCreatedDate(Timestamp.valueOf("2024-07-06 00:00:00"));
            shiftRosterEntity.setUpdatedBy("Nivetha");
            shiftRosterEntity.setUpdatedDate(Timestamp.valueOf("2024-07-06 00:00:00"));
            shiftRosterEntityList.add(shiftRosterEntity);
            bulkUploadImpl.bulkuploadExcelValidation("1", file);
            verify(shiftRosterRepo).saveAll(shiftRosterEntityList);
        }
    }
}
