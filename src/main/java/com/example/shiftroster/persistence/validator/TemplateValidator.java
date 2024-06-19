package com.example.shiftroster.persistence.validator;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.validation.BasicValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateValidator {

    @Autowired
    BasicValidation basicValidation;

    public void basicValidation(String templateType, String startDate, String endDate, String empId) throws CommonException {
        basicValidation.templateTypeValidation(templateType);
        basicValidation.dateValidation(startDate);
        basicValidation.dateValidation(endDate);
        basicValidation.empIdValidation(empId);
    }
}

