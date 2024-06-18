package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.NotFoundException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
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
    ShiftRosterRepo shiftRosterRepo;

    public EmployeeEntity employeeValidation(String empId) throws CommonException {
        return employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(AppConstant.INVALID_EMPLOYEE));
    }

    public boolean validateShiftDate(Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors) {
        Map.Entry<String, Map<LocalDate, String>> entry = employeeShiftData.entrySet().iterator().next();
        String employeeId = entry.getKey();
        Map<LocalDate, String> shifts = entry.getValue();
        Set<LocalDate> additionalValidationDates = new HashSet<>();

        boolean isValid = validateWeekOffsAndWorkingDays(employeeId, shifts, errors, additionalValidationDates);

        // Remove additional validation data after validation is complete
        additionalValidationDates.forEach(shifts::remove);

        return isValid;
    }

    private boolean validateWeekOffsAndWorkingDays(String employeeId, Map<LocalDate, String> shifts, List<String> errors, Set<LocalDate> additionalValidationDates) {
        LocalDate minDate = shifts.keySet().stream().min(LocalDate::compareTo).orElse(null);
        LocalDate maxDate = shifts.keySet().stream().max(LocalDate::compareTo).orElse(null);

        if (minDate == null) {
            errors.add("No shifts available for validation for employee id " + employeeId);
            return false;
        }

        Map<LocalDate, Integer> shiftRosterMap = new HashMap<>();
        Set<LocalDate> datesToFetch = new HashSet<>(shifts.keySet());

        if (minDate.getDayOfMonth() == 1 && minDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            LocalDate previousMonth = minDate.minusMonths(1);
            Set<LocalDate> prevMonthDates = generateMonthDates(previousMonth);
            datesToFetch.addAll(prevMonthDates);
            additionalValidationDates.addAll(prevMonthDates);
        }

        if (maxDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
            LocalDate nextMonth = maxDate.plusMonths(1);
            Set<LocalDate> nextMonthDates = generateMonthDates(nextMonth);
            datesToFetch.addAll(nextMonthDates);
            additionalValidationDates.addAll(nextMonthDates);
        }

        fetchShiftData(employeeId, shiftRosterMap, datesToFetch);
        fillMissingShiftData(shifts, shiftRosterMap, datesToFetch);

        LocalDate currentStartDate = minDate;
        int consecutiveWorkingDays = 0;
        while (!currentStartDate.isAfter(maxDate)) {
            LocalDate weekStartDate = getWeekStartDate(currentStartDate);
            LocalDate weekEndDate = weekStartDate.plusDays(6);

            Map<LocalDate, String> currentWeekShifts = shifts.entrySet().stream()
                    .filter(entry -> !entry.getKey().isBefore(weekStartDate) && !entry.getKey().isAfter(weekEndDate))
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            long weekOffCount = currentWeekShifts.entrySet().stream()
                    .filter(entry -> {
                        LocalDate date = entry.getKey();
                        String shift = entry.getValue();
                        int shiftRosterValue = shiftRosterMap.getOrDefault(date, 1);
                        return "WO".equalsIgnoreCase(shift) || shiftRosterValue == 0;
                    })
                    .count();

            if (weekOffCount < 1 || weekOffCount > 2) {
                errors.add("Employee ID " + employeeId + " has an invalid number of week-offs.");
                return false;
            }

            for (Map.Entry<LocalDate, String> entry : currentWeekShifts.entrySet()) {
                LocalDate date = entry.getKey();
                String shift = entry.getValue();
                boolean isWorkingDay = !"WO".equalsIgnoreCase(shift) && shiftRosterMap.getOrDefault(date, 1) != 0;

                if (isWorkingDay) {
                    consecutiveWorkingDays++;
                    if (consecutiveWorkingDays > 6) {
                        errors.add("Employee ID " + employeeId + " has more than 6 continuous working days starting from " + date.minusDays(consecutiveWorkingDays - 1) + ".");
                        return false;
                    }
                } else {
                    consecutiveWorkingDays = 0;
                }
            }
            currentStartDate = weekEndDate.plusDays(1);
        }
        return true;
    }

    private void fetchShiftData(String employeeId, Map<LocalDate, Integer> shiftRosterMap, Set<LocalDate> dates) {
        Map<Integer, List<LocalDate>> datesByMonth = dates.stream().collect(Collectors.groupingBy(LocalDate::getMonthValue));
        datesByMonth.forEach((month, monthDates) -> {
            Optional<ShiftRosterEntity> shiftRosterEntity = shiftRosterRepo.findByEmpIdAndMonthAndYear(Integer.parseInt(employeeId), month, monthDates.get(0).getYear());
            shiftRosterEntity.ifPresent(entity -> {
                monthDates.forEach(date -> {
                    try {
                        Method getterMethod = entity.getClass().getMethod(AppConstant.GET_DAY + String.format(AppConstant.STRING_DAY_FORMAT, date.getDayOfMonth()));
                        Integer value = (Integer) getterMethod.invoke(entity);
                        if (value != null) {
                            shiftRosterMap.put(date, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private void fillMissingShiftData(Map<LocalDate, String> shifts, Map<LocalDate, Integer> shiftRosterMap, Set<LocalDate> dates) {
        dates.forEach(date -> {
            if (!shifts.containsKey(date)) {
                int shiftRosterValue = shiftRosterMap.getOrDefault(date, 1);
                shifts.put(date, shiftRosterValue == 0 ? "WO" : "WD");
            }
        });
    }

    private Set<LocalDate> generateMonthDates(LocalDate month) {
        return month.withDayOfMonth(1).datesUntil(month.plusMonths(1).withDayOfMonth(1)).collect(Collectors.toSet());
    }

    private LocalDate getWeekStartDate(LocalDate date) {
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }
}