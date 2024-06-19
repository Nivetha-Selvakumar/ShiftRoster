package com.example.shiftroster.persistence.service;

import com.example.shiftroster.persistence.Exception.CommonException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Service
public interface TemplateService {

    void generateShiftRosterTemplate(String templateType, String startDate, String endDate, String empId) throws IOException, CommonException, ParseException;
}
