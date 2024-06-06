package com.example.shiftroster.persistance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@CrossOrigin
@RequestMapping("/template")
public class TemplateController {

//    @Autowired
//    TemplateService templateService;

    @GetMapping(path = "/")
    //TemplateType takes value as it is ShiftRoster or not
    public ResponseEntity generateTemplate (@RequestHeader String templateType,@RequestHeader String periodType, @PathVariable String id)
            throws  FileNotFoundException {
//        templateService.generateShiftRosterTemplate(templateType,periodType,id);
        return  null;
    }

}
