package com.example.shiftroster.persistance.controller;

import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.service.BulkUploadService;
import com.example.shiftroster.persistance.util.AppConstant;
import com.example.shiftroster.persistance.validator.BulkUploadValidator;
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
        bulkUploadValidator.basicValidtion(empId,file);
        bulkUploadService.bulkuploadExcelValidation(empId, file);
        return new ResponseEntity<>(AppConstant.SUCCESSFULLY_UPLOAD , HttpStatus.CREATED);
    }
}
