//package com.example.shiftroster.persistence.service.impl;
//
//import com.example.shiftroster.persistence.Exception.CommonException;
//import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
//import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
//import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
//import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
//import com.example.shiftroster.persistence.validation.BasicValidation;
//import com.example.shiftroster.persistence.validation.BusinessValidation;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
//import org.junit.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class BulkUploadImplTest {
//
//    @InjectMocks
//    BulkUploadImpl bulkUploadImpl;
//
//    @Mock
//    ShiftRosterRepo shiftRosterRepo;
//
//    @Mock
//    EmployeeRepo employeeRepo;
//
//    @Mock
//    ShiftRepo shiftRepo;
//
//    @Mock
//    BusinessValidation businessValidation;
//
//    @Mock
//    BasicValidation basicValidation;
//
//    EmployeeEntity employeeEntity = new EmployeeEntity();
//
//    @Test
//    public void testValidExcelUpload() throws Exception {
//        employeeEntity.setId(1);
////        when((Publisher<?>) businessValidation.employeeValidation(Mockito.anyString())).thenReturn(employeeEntity);
//
//        // Create a mock Excel file
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Shifts");
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("EmpID");
//        headerRow.createCell(1).setCellValue("2024-01-01");
//        Row dataRow = sheet.createRow(1);
//        dataRow.createCell(0).setCellValue("123");
//        dataRow.createCell(1).setCellValue("D");
//
//        // Convert workbook to InputStream
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        InputStream is = new ByteArrayInputStream(bos.toByteArray());
//
//        MockMultipartFile file = new MockMultipartFile("file", "shifts.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
//
//        // Mock other interactions as necessary
////        when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(),Mockito.any())).thenReturn(Optional.of(employeeEntity));
////        when(shiftRepo.findByShiftNameAndStatus(anyString(), any())).thenReturn(Optional.of(new ShiftEntity(1, "D", EnumStatus.ACTIVE)));
//
//        // Simulate HTTP response
//        HttpServletResponse response = mock(HttpServletResponse.class);
////        when(RequestContextHolder.getRequestAttributes()).thenReturn(new ServletRequestAttributes(null, response));
//
//        // Invoke the method
//        bulkUploadImpl.bulkuploadExcelValidation("123", file);
//
//        // Assertions
//        verify(shiftRosterRepo, times(1)).saveAll(anyList());
//    }
//
//    @Test
//    public void testInvalidEmployeeID() throws Exception {
//        // Mocking employee validation to throw an exception
////        when(businessValidation.employeeValidation("invalid")).thenThrow(new CommonException("Invalid Employee ID"));
//
//        // Create a mock Excel file
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Shifts");
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("EmpID");
//        headerRow.createCell(1).setCellValue("2024-01-01");
//        Row dataRow = sheet.createRow(1);
//        dataRow.createCell(0).setCellValue("invalid");
//        dataRow.createCell(1).setCellValue("D");
//
//        // Convert workbook to InputStream
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        InputStream is = new ByteArrayInputStream(bos.toByteArray());
//
//        MockMultipartFile file = new MockMultipartFile("file", "shifts.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
//
//        // Simulate HTTP response
//        HttpServletResponse response = mock(HttpServletResponse.class);
////        when(RequestContextHolder.getRequestAttributes()).thenReturn(new ServletRequestAttributes(null, response));
//
//        // Invoke the method and assert the exception
//        CommonException exception = assertThrows(CommonException.class, () -> bulkUploadImpl.bulkuploadExcelValidation("invalid", file));
//        assertEquals("Invalid Employee ID", exception.getMessage());
//    }
//
//    @Test
//    public void testInvalidShiftData() throws Exception {
//        // Mocking employee validation
//        EmployeeEntity mockEmployee = new EmployeeEntity();
//        mockEmployee.setId(1);
////        when(businessValidation.employeeValidation("123")).thenReturn(mockEmployee);
//
//        // Create a mock Excel file
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Shifts");
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("EmpID");
//        headerRow.createCell(1).setCellValue("2024-01-01");
//        Row dataRow = sheet.createRow(1);
//        dataRow.createCell(0).setCellValue("123");
//        dataRow.createCell(1).setCellValue("InvalidShift");
//
//        // Convert workbook to InputStream
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        InputStream is = new ByteArrayInputStream(bos.toByteArray());
//
//        MockMultipartFile file = new MockMultipartFile("file", "shifts.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
//
//        // Mock other interactions
////        when(employeeRepo.findByIdAndEmpStatus(anyInt(), any())).thenReturn(Optional.of(mockEmployee));
////        when(shiftRepo.findByShiftNameAndStatus(eq("InvalidShift"), any())).thenReturn(Optional.empty());
//
//        // Simulate HTTP response
//        HttpServletResponse response = mock(HttpServletResponse.class);
//      //  when(RequestContextHolder.getRequestAttributes()).thenReturn(new ServletRequestAttributes(null, response));
//
//        // Invoke the method and capture the response
//        bulkUploadImpl.bulkuploadExcelValidation("123", file);
//
//        // Verify no shifts were saved
//        verify(shiftRosterRepo, never()).saveAll(anyList());
//    }
//
//}
