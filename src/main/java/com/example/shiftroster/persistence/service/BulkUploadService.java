package com.example.shiftroster.persistence.service;

import com.example.shiftroster.persistence.Exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Service
public interface BulkUploadService {

    void bulkuploadExcelValidation(String empId, MultipartFile file) throws IOException, CommonException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}
