package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.NotFoundException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BusinessValidation {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRepo shiftRepo;

    @Autowired
    ShiftRosterRepo shiftRosterRepo;

    public EmployeeEntity employeeValidation(String empId) throws CommonException {
        return employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(AppConstant.INVALID_EMPLOYEE));
    }

    public boolean validateShiftDate(Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors) {
        Map.Entry<String, Map<LocalDate, String>> entry = employeeShiftData.entrySet().iterator().next();
        String employeeId = entry.getKey();
        Map<LocalDate, String> shifts = entry.getValue();
        if(!validateWeekOffsAndWorkingDays(employeeId,shifts, errors)){
            return false;
        }
        return true;
    }

    private boolean validateWeekOffsAndWorkingDays(String employeeId, Map<LocalDate, String> shifts, List<String> errors) {
        // Get the minimum date in the shifts map
        LocalDate minDate = shifts.keySet().stream().min(LocalDate::compareTo).orElse(null);

        if (minDate == null) {
            errors.add("No shifts available for validation.");
            return false;
        }

        Map<LocalDate, Integer> shiftRosterMap = new HashMap<>();
        // Preload shift roster data for the current and previous months if needed
        getShiftRosterData(employeeId, shiftRosterMap, shifts);

        // Iterate through the shifts data week by week
        LocalDate currentStartDate = minDate;
        while (currentStartDate.isBefore(shifts.keySet().stream().max(LocalDate::compareTo).orElse(minDate))) {
            LocalDate weekStartDate = getWeekStartDate(currentStartDate);
            LocalDate weekEndDate = weekStartDate.plusDays(6);

            // Filter shifts for the current week
            Map<LocalDate, String> currentWeekShifts = shifts.entrySet().stream()
                    .filter(entry -> !entry.getKey().isBefore(weekStartDate) && !entry.getKey().isAfter(weekEndDate))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Calculate the week-off count
            int weekOffCount = (int) currentWeekShifts.entrySet().stream()
                    .filter(entry -> {
                        LocalDate date = entry.getKey();
                        String shift = entry.getValue();
                        int shiftRosterValue = shiftRosterMap.getOrDefault(date, 0);
                        return shift.equalsIgnoreCase("WO") || shiftRosterValue == 0;
                    })
                    .count();

            // Validate the week-off count
            if (weekOffCount < 1 || weekOffCount > 2) {
                errors.add("Employee ID " + employeeId + " has an invalid number of week-offs.");
                return false;
            }

            // Move to the next week
            currentStartDate = weekEndDate.plusDays(1);
        }
        return true;
    }

    private LocalDate getWeekStartDate(LocalDate date) {
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    private void getShiftRosterData(String employeeId, Map<LocalDate, Integer> shiftRosterMap, Map<LocalDate, String> shifts) {
        shifts.keySet().forEach(date -> {
            Optional<ShiftRosterEntity> shiftRosterEntity = shiftRosterRepo.findByEmpIdAndMonthAndYear(Integer.valueOf(employeeId), date.getMonthValue(), date.getYear());
            shiftRosterEntity.ifPresent(entity -> {
                for (int i = 1; i <= date.lengthOfMonth(); i++) {
                    try {
                        Method getterMethod = entity.getClass().getMethod("getDay" + String.format("%02d", i));
                        Integer value = (Integer) getterMethod.invoke(entity);
                        if (value != null) {
                            LocalDate localDate = LocalDate.of(date.getYear(), date.getMonthValue(), i);
                            shiftRosterMap.put(localDate, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}