package com.example.shiftroster.persistence.controller;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.service.TemplateService;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validator.TemplateValidator;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@CrossOrigin
@RequestMapping("/template")
@Tag(name = "Template")
@SecurityRequirement(name = "bearerAuth")
public class TemplateController {

    @Autowired
    TemplateService templateService;

    @Autowired
    TemplateValidator templateValidator;

    @GetMapping(path = "/download")
    //TemplateType takes value as it is ShiftRoster or not
    public ResponseEntity generateTemplate (@RequestHeader String templateType, @RequestHeader String empId ,
                                            @RequestParam String startDate, @RequestParam String endDate)
            throws IOException, CommonException, ParseException {
        templateValidator.basicValidation(templateType,startDate,endDate,empId);
        templateService.generateShiftRosterTemplate(templateType,startDate,endDate,empId);
        return new ResponseEntity<>(AppConstant.TEMPLATE_DOWNLOADED,HttpStatus.OK);
    }
}
