package com.example.shiftroster.persistance.controller;

import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.service.TemplateService;
import com.example.shiftroster.persistance.util.AppConstant;
import com.example.shiftroster.persistance.validator.TemplateValidator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@CrossOrigin
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    TemplateService templateService;

    @Autowired
    TemplateValidator templateValidator;

    @GetMapping(path = "/download")
    //TemplateType takes value as it is ShiftRoster or not
    public ResponseEntity generateTemplate (@RequestHeader String templateType, @RequestHeader String empId ,
                                            @RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response)
            throws IOException, CommonException, ParseException {
        templateValidator.basicValidation(templateType,startDate,endDate,empId);
        templateService.generateShiftRosterTemplate(templateType,startDate,endDate,empId,response);
        return  new ResponseEntity(AppConstant.TEMPLATE_DOWNLOADED, HttpStatus.OK);
    }

}
