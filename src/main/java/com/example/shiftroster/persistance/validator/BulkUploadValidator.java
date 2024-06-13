package com.example.shiftroster.persistance.validator;

import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.validation.BasicValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class BulkUploadValidator {
    @Autowired
    BasicValidation basicValidation;

    public void basicValidtion(String empId, MultipartFile file) throws CommonException, IOException {
        basicValidation.empIdValidation(empId);
        basicValidation.fileValidation(file);
    }
}
