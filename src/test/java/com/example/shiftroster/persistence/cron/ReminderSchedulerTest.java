package com.example.shiftroster.persistence.cron;

import com.example.shiftroster.persistence.Enum.EnumRole;
import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import jakarta.mail.internet.MimeMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReminderSchedulerTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private ShiftRosterRepo shiftRosterRepo;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private ReminderScheduler reminderScheduler = new ReminderScheduler();

    EmployeeEntity appraiser = new EmployeeEntity();
    EmployeeEntity employee1 = new EmployeeEntity();
    EmployeeEntity employee2 = new EmployeeEntity();

    ShiftRosterEntity shiftRoster1 = new ShiftRosterEntity();
    ShiftRosterEntity shiftRoster2 = new ShiftRosterEntity();

    public List<EmployeeEntity> appraisers = new ArrayList<>();
    public List<EmployeeEntity> employees = new ArrayList<>();
    public List<ShiftRosterEntity> shiftRosters = new ArrayList<>();

    @Test
    public void testSendReminderTaskNoActiveEmployees() {
        reminderScheduler.sendReminderTask();
        verify(emailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    public  void testSendReminderTaskAllEmployeesHaveShifts() {

        List<ShiftRosterEntity> shiftRosterEntityList = Arrays.asList(shiftRoster1, shiftRoster2);
        when(employeeRepo.findAllByRoleAndEmpStatus(any(),any()))
                .thenReturn(Collections.singletonList(appraiser));
        when(employeeRepo.findAllByRoleAndEmpStatusAndAppraiserId(any(),any(),any()))
                .thenReturn(employees);
        reminderScheduler.sendReminderTask();
        verify(emailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    public  void sendReminderTaskTest() {
        appraiser.setId(1);
        appraiser.setEmpName("nive");
        appraiser.setEmail("nive@example.com");
        appraiser.setEmpStatus(EnumStatus.ACTIVE);
        appraiser.setRole(EnumRole.APPRAISER);
        appraisers.add(appraiser);

        employee1.setId(2);
        employee1.setEmpName("abi");
        employee1.setEmpCode("E123");
        employee1.setAppraiserId(appraiser);
        employee1.setEmpStatus(EnumStatus.ACTIVE);
        employee1.setRole(EnumRole.EMPLOYEE);

        employee2.setId(3);
        employee2.setEmpName("ravi");
        employee2.setEmpCode("E124");
        employee2.setAppraiserId(appraiser);
        employee2.setEmpStatus(EnumStatus.ACTIVE);
        employee2.setRole(EnumRole.EMPLOYEE);

        employees = Arrays.asList(employee1, employee2);

        shiftRoster1.setEmpId(2);
        shiftRoster1.setDay01(1);

        shiftRoster2.setEmpId(3);
        shiftRosters = Arrays.asList(shiftRoster1, shiftRoster2);

        List<ShiftRosterEntity> shiftRosterEntityList = Arrays.asList(shiftRoster1, shiftRoster2);
        when(employeeRepo.findAllByRoleAndEmpStatus(any(),any()))
                .thenReturn(Collections.singletonList(appraiser));
        when(employeeRepo.findAllByRoleAndEmpStatusAndAppraiserId(any(),any(),any()))
                .thenReturn(employees);
        reminderScheduler.sendReminderTask();
        verify(emailSender, never()).send(any(MimeMessage.class));
    }
}
