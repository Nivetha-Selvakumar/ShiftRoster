package com.example.shiftroster.persistence.controller;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.cron.ReminderScheduler;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.validation.BasicValidation;
import com.example.shiftroster.persistence.validation.BusinessValidation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/reminder")
@Tag(name = "Cron")
public class EmailCronController {

    @Autowired
    ReminderScheduler reminderScheduler;

    @Autowired
    BasicValidation basicValidation;

    @Autowired
    BusinessValidation businessValidation;

    @GetMapping(path = "/dailycron/shift")
    public ResponseEntity<String> setReminder(@RequestHeader String empId) throws CommonException {
        basicValidation.empIdValidation(empId);
        businessValidation.employeeValidation(empId);
        reminderScheduler.sendReminderTask();
        return ResponseEntity.status(HttpStatus.CREATED).body(AppConstant.REMINDER_SENT_SUCCESSFULLY);
    }
}
