package com.example.shiftroster.persistance.service;

import com.example.shiftroster.persistance.Exception.CommonException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Service
public interface TemplateService {

    void generateShiftRosterTemplate(String templateType, String startDate, String endDate, String empId, HttpServletResponse response) throws IOException, CommonException, ParseException;

}
