package com.example.shiftroster.persistence.validator;

import com.example.shiftroster.persistence.Enum.EnumTemplateType;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.validation.BasicValidation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TemplateValidatorTest {

    @InjectMocks
    TemplateValidator templateValidator = new TemplateValidator();

    @Mock
    BasicValidation basicValidation = new BasicValidation();

    @Test
    public void testBasicValidation() throws CommonException {

        templateValidator.basicValidation(String.valueOf(EnumTemplateType.SHIFTROSTER), "20240701", "20240731", "1");

        verify(basicValidation, times(1)).templateTypeValidation(String.valueOf(EnumTemplateType.SHIFTROSTER));
        verify(basicValidation, times(1)).dateValidation("20240701");
        verify(basicValidation, times(1)).dateValidation("20240731");
        verify(basicValidation, times(1)).empIdValidation("1");
    }

    @Test
    public void testBasicValidationAllValid() throws CommonException {
        String templateType = "SHIFTROSTER";
        String startDate = "20230101";
        String endDate = "20230131";
        String empId = "EMP123";

        templateValidator.basicValidation(templateType, startDate, endDate, empId);
        verify(basicValidation).templateTypeValidation(templateType);
        verify(basicValidation).dateValidation(startDate);
        verify(basicValidation).dateValidation(endDate);
        verify(basicValidation).empIdValidation(empId);
    }
}
