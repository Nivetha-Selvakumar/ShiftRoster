package com.example.shiftroster.persistence.controller;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.service.BulkUploadService;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validator.BulkUploadValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/bulkupload")
public class BulkUploadController {

    @Autowired
    BulkUploadService bulkUploadService;

    @Autowired
    BulkUploadValidator bulkUploadValidator;

    @PostMapping(path = "/shiftroster")
    public ResponseEntity<String> excelShiftRosterBulkUpload(@RequestHeader String empId,
                                                             @RequestBody MultipartFile file) throws CommonException, IOException {
        bulkUploadValidator.basicValidation(empId,file);
        bulkUploadService.bulkuploadExcelValidation(empId, file);
        return new ResponseEntity<>(AppConstant.SUCCESSFULLY_UPLOAD , HttpStatus.CREATED);
    }
}
