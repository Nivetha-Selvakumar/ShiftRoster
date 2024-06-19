package com.example.shiftroster.persistence.controller;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.MisMatchException;
import com.example.shiftroster.persistence.cron.ReminderScheduler;
import com.example.shiftroster.persistence.util.AppConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmailCronControllerTest {
    @InjectMocks
    EmailCronController emailCronController;

    @Mock
    ReminderScheduler reminderScheduler;

    @Test
    void setReminderTest() throws CommonException {
        reminderScheduler.sendReminderTask("1");
        String expected = AppConstant.REMINDER_SENT_SUCCESSFULLY;
        ResponseEntity<String> actual = emailCronController.setReminder("1"); ;
        Assertions.assertEquals(expected, actual.getBody());
    }
}
