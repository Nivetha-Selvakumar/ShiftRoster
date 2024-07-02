package com.example.shiftroster.persistence.cron;

import com.example.shiftroster.persistence.Enum.EnumRole;
import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import jakarta.mail.internet.MimeMessage;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderSchedulerTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private ShiftRosterRepo shiftRosterRepo;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private ReminderScheduler reminderScheduler;

    // Test data
    private List<EmployeeEntity> mockAppraisers;
    private List<EmployeeEntity> mockEmployees;
    private List<ShiftRosterEntity> mockShiftRosters;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        mockAppraisers = new ArrayList<>();
        mockEmployees = new ArrayList<>();
        mockShiftRosters = new ArrayList<>();

        // Mock repository methods
        when(employeeRepo.findAllByRoleAndEmpStatus(EnumRole.APPRAISER, EnumStatus.ACTIVE)).thenReturn(mockAppraisers);
        when(employeeRepo.findAllByRoleAndEmpStatusAndAppraiserId(EnumRole.EMPLOYEE, EnumStatus.ACTIVE, any())).thenReturn(mockEmployees);
        when(shiftRosterRepo.findAllByEmpIdInAndMonthAndYear(any(), anyInt(), anyInt())).thenReturn(mockShiftRosters);
    }

//    @Test
    public void testSendReminderTask() {
        // Mock behavior for EmployeeRepo.findById
        when(employeeRepo.findById(anyInt())).thenReturn(Optional.of(new EmployeeEntity()));

        // Mock JavaMailSender behavior (void method)
        doNothing().when(emailSender).send((MimeMessage) any());

        // Set the 'fromMail' value using ReflectionTestUtils
        ReflectionTestUtils.setField(reminderScheduler, "fromMail", "test@example.com");

        // Call the method under test
        reminderScheduler.sendReminderTask();

        // Verify interactions
        verify(employeeRepo, times(1)).findAllByRoleAndEmpStatus(EnumRole.APPRAISER, EnumStatus.ACTIVE);
        verify(employeeRepo, times(mockAppraisers.size())).findAllByRoleAndEmpStatusAndAppraiserId(EnumRole.EMPLOYEE, EnumStatus.ACTIVE, any());
        verify(shiftRosterRepo, atMostOnce()).findAllByEmpIdInAndMonthAndYear(any(), anyInt(), anyInt());
        verify(emailSender, times(mockAppraisers.size())).send((MimeMessage) any());
    }
}
