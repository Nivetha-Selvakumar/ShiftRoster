package com.example.shiftroster.persistance.service;

import com.example.shiftroster.persistance.Exception.CommonException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public interface TemplateService {
    void generateShiftRosterTemplate(String templateType, String periodType, String id) throws FileNotFoundException, CommonException;
}
