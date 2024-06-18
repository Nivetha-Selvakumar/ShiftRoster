package com.example.shiftroster.persistence.service.impl;

import com.example.shiftroster.persistence.secondary.entity.TemplateEntity;
import com.example.shiftroster.persistence.secondary.repository.TemplateRepo;
import com.example.shiftroster.persistence.validation.BusinessValidation;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TemplateImplTest {
    @InjectMocks
    TemplateImpl templateImpl;

    @Mock
    TemplateRepo templateRepo;

    @Mock
    BusinessValidation businessValidation;

    TemplateEntity templateEntity = new TemplateEntity();

    @Test
    public void generateShiftRosterTemplateTest(){
        when(templateRepo.findByDocTypeAndRefType(Mockito.any(),Mockito.anyString())).thenReturn(Optional.ofNullable(templateEntity));



    }
}
