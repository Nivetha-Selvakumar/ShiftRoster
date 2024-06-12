package com.example.shiftroster.persistance.controller;


import com.example.shiftroster.persistance.cron.ReminderScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/reminder")
public class EmailCronController {
    @Autowired
    ReminderScheduler reminderScheduler;

    @GetMapping(path = "/dailycron/shift")
    public ResponseEntity<String> setReminder()  {
        reminderScheduler.sendReminderTask();
        return ResponseEntity.status(HttpStatus.CREATED).body("Reminder set successfully");
    }
}
