package com.example.shiftroster.persistence.cron;

import com.example.shiftroster.persistence.Enum.EnumRole;
import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class ReminderSchedulerTest {
//
//    @Mock
//    private EmployeeRepo employeeRepo;
//
//    @Mock
//    private ShiftRosterRepo shiftRosterRepo;
//
//    @Mock
//    private JavaMailSender emailSender;
//
//    @InjectMocks
//    private ReminderScheduler reminderScheduler;
//
//    EmployeeEntity employeeEntity = new EmployeeEntity();
//
//    List<EmployeeEntity> employeeEntityList = new ArrayList<>();
//
//    @BeforeEach
//    public void setUp() {
//        reminderScheduler.fromMail = "test@example.com";
//
//        employeeEntity.setEmpName("nive");
//        employeeEntity.setId(1);
//
//    }
//
//    @Test
//    public void testSendReminderTask_NoEmployees() {
//        employeeEntityList.add(employeeEntity);
//        when((Publisher<?>) employeeRepo.findAllByRoleAndEmpStatus(Mockito.any(), Mockito.any())).thenReturn(employeeEntityList);
//
//        reminderScheduler.sendReminderTask();
//        verify(employeeRepo, times(1)).findAllByRoleAndEmpStatus(EnumRole.APPRAISER, EnumStatus.ACTIVE);
//        verifyNoMoreInteractions(employeeRepo);
//        verifyNoInteractions(shiftRosterRepo, emailSender);
//    }
//
//    @Test
//    public void testSendReminderTask_NoShiftAssignedEmployees() {
//        EmployeeEntity appraiser = new EmployeeEntity();
//        appraiser.setId(1);
//        appraiser.setEmail("appraiser@example.com");
//
//        EmployeeEntity employee = new EmployeeEntity();
//        employee.setId(2);
//        employee.setEmpName("John Doe");
//        employee.setEmpCode("JD123");
//
//        List<EmployeeEntity> appraiserList = Arrays.asList(appraiser);
//        List<EmployeeEntity> employeeList = Arrays.asList(employee);
//
//        when((Publisher<?>) employeeRepo.findAllByRoleAndEmpStatus(EnumRole.APPRAISER, EnumStatus.ACTIVE)).thenReturn(appraiserList);
//        when((Publisher<?>) employeeRepo.findAllByRoleAndEmpStatusAndAppraiserId(EnumRole.EMPLOYEE, EnumStatus.ACTIVE, appraiser)).thenReturn(employeeList);
//        when((Publisher<?>) shiftRosterRepo.findAllByEmpIdInAndMonthAndYear(anyList(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
//
//        doNothing().when(emailSender).send(any(MimeMessage.class));
//
//        reminderScheduler.sendReminderTask();
//
//        verify(emailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    public void testSendReminderTask_UnassignedShifts() throws MessagingException {
//        EmployeeEntity appraiser = new EmployeeEntity();
//        appraiser.setId(1);
//        appraiser.setEmail("appraiser@example.com");
//
//        EmployeeEntity employee = new EmployeeEntity();
//        employee.setId(2);
//        employee.setEmpName("John Doe");
//        employee.setEmpCode("JD123");
//
//        List<EmployeeEntity> appraiserList = Arrays.asList(appraiser);
//        List<EmployeeEntity> employeeList = Arrays.asList(employee);
//
//        ShiftRosterEntity shiftRoster = new ShiftRosterEntity();
//        shiftRoster.setEmpId(2);
//        shiftRoster.setDay01(1);
//
//        List<ShiftRosterEntity> shiftRosterList = Arrays.asList(shiftRoster);
//
//        when((Publisher<?>) employeeRepo.findAllByRoleAndEmpStatus(EnumRole.APPRAISER, EnumStatus.ACTIVE)).thenReturn(appraiserList);
//        when((Publisher<?>) employeeRepo.findAllByRoleAndEmpStatusAndAppraiserId(EnumRole.EMPLOYEE, EnumStatus.ACTIVE, appraiser)).thenReturn(employeeList);
//        when((Publisher<?>) shiftRosterRepo.findAllByEmpIdInAndMonthAndYear(anyList(), anyInt(), anyInt())).thenReturn(shiftRosterList);
//
//        doNothing().when(emailSender).send(any(MimeMessage.class));
//
//        reminderScheduler.sendReminderTask();
//
//        verify(emailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    public void testSendEmail_InvalidEmail() throws MessagingException {
//        EmployeeEntity appraiser = new EmployeeEntity();
//        appraiser.setEmail("invalid-email");
//
//        Map<Integer, Set<Integer>> unassignedShiftsMap = new HashMap<>();
//        List<EmployeeEntity> noShiftAssignedEmployees = new ArrayList<>();
//
//        reminderScheduler.sendEmail(appraiser, unassignedShiftsMap, noShiftAssignedEmployees, mock(String.valueOf(Logger.class)));
//
//        verify(emailSender, never()).send(any(MimeMessage.class));
//    }
//
//    @Test
//    public void testSendEmail_ValidEmail() throws MessagingException {
//        EmployeeEntity appraiser = new EmployeeEntity();
//        appraiser.setEmail("appraiser@example.com");
//        appraiser.setEmpName("Appraiser");
//
//        Map<Integer, Set<Integer>> unassignedShiftsMap = new HashMap<>();
//        Set<Integer> unassignedDays = new HashSet<>(Arrays.asList(1, 2, 3));
//        unassignedShiftsMap.put(2, unassignedDays);
//
//        List<EmployeeEntity> noShiftAssignedEmployees = new ArrayList<>();
//
//        EmployeeEntity employee = new EmployeeEntity();
//        employee.setId(2);
//        employee.setEmpName("John Doe");
//        employee.setEmpCode("JD123");
//
//        when(employeeRepo.findById(2)).thenReturn(Optional.of(employee));
//
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//        doNothing().when(emailSender).send(mimeMessage);
//
//        reminderScheduler.sendEmail(appraiser, unassignedShiftsMap, noShiftAssignedEmployees, mock(Logger.class));
//
//        verify(emailSender, times(1)).send(any(MimeMessage.class));
//    }

}
