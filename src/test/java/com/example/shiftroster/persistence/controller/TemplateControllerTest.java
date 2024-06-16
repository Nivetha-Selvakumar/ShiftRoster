package com.example.shiftroster.persistence.controller;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.service.TemplateService;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validator.TemplateValidator;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class TemplateControllerTest {
    @InjectMocks
    TemplateController templateController;

    @Mock
    TemplateService templateService;

    @Mock
    TemplateValidator templateValidator;

    @Test
    public void generateTemplateTest() throws CommonException, IOException, ParseException {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Mocking the behavior of bulkUploadValidator.basicValidation()
        doNothing().when(templateValidator).basicValidation(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        doNothing().when(templateService).generateShiftRosterTemplate(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString());

        // Call the method to be tested
        ResponseEntity<String> response = templateController.generateTemplate("Shiftroster", "1","20240505","20240909");

        // Verify the response
        assertEquals(AppConstant.TEMPLATE_DOWNLOADED, response.getBody());
    }
}
