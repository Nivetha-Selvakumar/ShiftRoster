package com.example.shiftroster.persistance.service.impl;

import com.example.shiftroster.persistance.Enum.EnumDocType;
import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.secondary.repository.TemplateRepo;
import com.example.shiftroster.persistance.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Service
public class TemplateImpl implements TemplateService {

    @Autowired
    TemplateRepo templateRepo;

    @Override
    public void generateShiftRosterTemplate(String templateType, String periodType, String id) throws CommonException, FileNotFoundException {
        String filename = templateRepo.findByDocTypeAndRefType(EnumDocType.EXCEL,templateType);
        FileOutputStream fileOutputStream = new FileOutputStream(filename);


    }
}
