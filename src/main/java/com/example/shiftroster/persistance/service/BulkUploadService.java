package com.example.shiftroster.persistance.service;

import com.example.shiftroster.persistance.Exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface BulkUploadService {

    void bulkuploadExcelValidation(String empId, MultipartFile file) throws IOException, CommonException;
}
