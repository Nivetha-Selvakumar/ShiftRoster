package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BussinessValidationTest {

    @Mock
    private ShiftRosterRepo shiftRosterRepo;

    @Mock
    private ShiftRepo shiftRepo;

    @Mock
    EmployeeRepo employeeRepo;

    @InjectMocks
    private BusinessValidation businessValidation;

    EmployeeEntity employeeEntity = new EmployeeEntity();

    private static final String EMPLOYEE_ID = "1";

    private Map<LocalDate, String> shifts = new HashMap<>();

    private List<String> errors = new ArrayList<>();

    @Test
    public void employeeValidationTest() throws CommonException {
        employeeEntity.setId(1);
        when(employeeRepo.findByIdAndEmpStatus(Mockito.anyInt(), any())).thenReturn(Optional.ofNullable(employeeEntity));
        assertEquals(employeeEntity,businessValidation.employeeValidation("1"));
    }

    @Test
    public void testValidateShiftDateInvalidShiftHours() {
        Map<String, Map<LocalDate, String>> employeeShiftData = new HashMap<>();
        Map<LocalDate, String> shifts = new HashMap<>();
        shifts.put(LocalDate.now(), "Shift1");
        shifts.put(LocalDate.now().plusDays(1), "Shift2");
        employeeShiftData.put("1", shifts);
        List<String> errors = new ArrayList<>();

        ShiftEntity shiftEntity1 = new ShiftEntity();
        shiftEntity1.setId(1);
        shiftEntity1.setFromTime(Time.valueOf("08:00:00"));
        shiftEntity1.setToTime(Time.valueOf("16:00:00"));

        ShiftEntity shiftEntity2 = new ShiftEntity();
        shiftEntity2.setId(2);
        shiftEntity2.setFromTime(Time.valueOf("00:00:00"));
        shiftEntity2.setToTime(Time.valueOf("08:00:00"));

        boolean isValid = businessValidation.validateShiftDate(employeeShiftData, errors);

        assertFalse(isValid);
        assertFalse(errors.isEmpty());
        assertFalse(errors.get(0).contains("less than 8 hours between shifts"));
    }

    @Test
    public void testValidateShiftHours() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String employeeId = "1";
        Map<LocalDate, String> shifts = new HashMap<>();
        shifts.put(LocalDate.now(), "Shift1");
        shifts.put(LocalDate.now().minusDays(1), "Shift2");
        List<String> errors = new ArrayList<>();

        when(shiftRepo.findByShiftName(anyString())).thenReturn(Optional.of(new ShiftEntity()));

        when(shiftRosterRepo.findByEmpIdAndMonthAndYear(anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(new ShiftRosterEntity()));

        Method validateShiftHoursMethod = BusinessValidation.class.getDeclaredMethod("validateShiftHours", String.class, Map.class, List.class);
        validateShiftHoursMethod.setAccessible(true);
        boolean isValid = (boolean) validateShiftHoursMethod.invoke(businessValidation, employeeId, shifts, errors);
        assertTrue(isValid);
    }

    @Test
    public void testFetchShiftData() throws Exception {
        LocalDate date = LocalDate.of(2023, 8, 1); // Example date for the test
        Set<LocalDate> datesToFetch = new HashSet<>();
        datesToFetch.add(date);

        ShiftRosterEntity shiftRosterEntity = new ShiftRosterEntity();
        shiftRosterEntity.setId(1);

        when(shiftRosterRepo.findByEmpIdAndMonthAndYear(anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(shiftRosterEntity));

        Method fetchShiftDataMethod = BusinessValidation.class.getDeclaredMethod("fetchShiftData", String.class, Map.class, Set.class);
        fetchShiftDataMethod.setAccessible(true);

        Map<LocalDate, Integer> shiftRosterMap = new HashMap<>();
        fetchShiftDataMethod.invoke(businessValidation, EMPLOYEE_ID, shiftRosterMap, datesToFetch);

        assertFalse(shiftRosterMap.containsKey(date));
    }
}
