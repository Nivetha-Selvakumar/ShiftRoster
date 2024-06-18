package com.example.shiftroster.persistence.service.impl;

import com.example.shiftroster.persistence.Enum.EnumDocType;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.secondary.entity.TemplateEntity;
import com.example.shiftroster.persistence.secondary.repository.TemplateRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validation.BusinessValidation;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateImplTest {

    @InjectMocks
    TemplateImpl templateImpl;

    @Mock
    TemplateRepo templateRepo;

    @Mock
    BusinessValidation businessValidation;

    TemplateEntity templateEntity = new TemplateEntity();

    @BeforeEach
    void init(){

    }

    @Test
    void generateShiftRosterTemplateTest() throws IOException, CommonException, ParseException {
        String templateType = "Shiftroster";
        String startDate = "20240404";
        String endDate = "20240505";
        String empId = "Employee123";

        templateEntity.setId(1);
        templateEntity.setDocType(EnumDocType.EXCEL);
        templateEntity.setRefType(templateType);
        templateEntity.setDocumentFile("src\\main\\resources\\templates\\ShiftRoster.xlsx");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(attributes);

        when(templateRepo.findByDocTypeAndRefType(Mockito.any(), Mockito.anyString())).thenReturn(Optional.of(templateEntity));

        // Call the method to be tested
        templateImpl.generateShiftRosterTemplate(templateType, startDate, endDate, empId);

        verify(businessValidation).employeeValidation(empId);
        assert response.getContentType().equals(AppConstant.EXCEL_CONTENT_TYPE);
        assert response.getHeader(AppConstant.CONTENT_DISPOSITION).equals(AppConstant.FILE_NAME);
        assert response.getContentAsByteArray().length > 0;
    }
}