package com.example.shiftroster.persistance.validator;

import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.Exception.MisMatchException;
import com.example.shiftroster.persistance.util.AppConstant;
import com.example.shiftroster.persistance.validation.BasicValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.NoConnectionPendingException;

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

