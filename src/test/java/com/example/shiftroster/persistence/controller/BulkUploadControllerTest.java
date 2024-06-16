package com.example.shiftroster.persistence.controller;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.service.BulkUploadService;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validator.BulkUploadValidator;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class BulkUploadControllerTest {
    @Mock
    private BulkUploadService bulkUploadService;

    @Mock
    private BulkUploadValidator bulkUploadValidator;

    @InjectMocks
    private BulkUploadController bulkUploadController;

    @Test
    public void testExcelShiftRosterBulkUpload_PositiveCase() throws CommonException, IOException {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a mock MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.ms-excel", "testdata".getBytes());

        // Mocking the behavior of bulkUploadValidator.basicValidation()
        doNothing().when(bulkUploadValidator).basicValidation(any(String.class), any(MultipartFile.class));
        doNothing().when(bulkUploadService).bulkuploadExcelValidation(any(String.class), any(MultipartFile.class));

        // Call the method to be tested
        ResponseEntity<String> response = bulkUploadController.excelShiftRosterBulkUpload("1", file);

        // Verify the response
        assertEquals(AppConstant.SUCCESSFULLY_UPLOAD, response.getBody());
    }
}
