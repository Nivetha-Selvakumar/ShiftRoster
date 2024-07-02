package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.NotFoundException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BusinessValidation {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRosterRepo shiftRosterRepo;

    @Autowired
    ShiftRepo shiftRepo;

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
        isValid = isValid && validateShiftHours(employeeId, shifts, errors);
        return isValid;
    }

    private boolean validateShiftHours(String employeeId, Map<LocalDate, String> shifts, List<String> errors) {
        List<Map.Entry<LocalDate, String>> sortedShifts = shifts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        for (int i = 0; i < sortedShifts.size() - 1; i++) {
            LocalDate currentDate = sortedShifts.get(i).getKey();
            LocalDate previousDate = currentDate.minusDays(1);
            String currentShift = sortedShifts.get(i).getValue() ;
            String previousShiftName;
            Integer shiftId = null;
            Optional<ShiftEntity> previousShiftValue;
            Optional<ShiftEntity> currentShiftValue;
            if(Objects.equals(currentShift, AppConstant.WO) || Objects.equals(currentShift, AppConstant.UA)){
                continue;
            }else{
                currentShiftValue = shiftRepo.findByShiftName(currentShift);
            }
            if (shifts.containsKey(previousDate)) {
                previousShiftName = shifts.get(previousDate);
                if(Objects.equals(previousShiftName, AppConstant.WO) || Objects.equals(previousShiftName, AppConstant.UA)){
                    continue;
                }else{
                    previousShiftValue = shiftRepo.findByShiftName(previousShiftName);
                }
            } else{
                Optional<ShiftRosterEntity> previousShiftRoster = shiftRosterRepo.findByEmpIdAndMonthAndYear(Integer.parseInt(employeeId),previousDate.getMonthValue(), previousDate.getYear());
                shiftId = getShiftDay(previousDate,previousShiftRoster);
                if((shiftId == null || shiftId == 0 || !Objects.equals(currentShift, AppConstant.WO) || !Objects.equals(currentShift, AppConstant.UA))){
                    continue;
                }else{
                    previousShiftValue = shiftRepo.findById(shiftId);
                }
            }
            if(!Objects.equals(previousShiftValue.get().getId(), currentShiftValue.get().getId())) {
                LocalTime previousShiftEndTime = LocalTime.parse(previousShiftValue.get().getToTime().toString());
                LocalTime currentShiftStartTime = LocalTime.parse(currentShiftValue.get().getFromTime().toString());
                Duration shiftDifference = Duration.between(previousShiftEndTime, currentShiftStartTime);

                if (shiftDifference.isNegative()) {
                    shiftDifference = shiftDifference.plusHours(24);
                }

                // Check if the gap is less than 8 hours
                if (shiftDifference.toMinutes() < 480) {  // 480 minutes is 8 hours
                    errors.add("Employee " + employeeId + " has less than 8 hours between shifts on " + previousDate + " and " + currentDate);
                    return false;
                }
            }
        }
        return true;
    }

    private Integer getShiftDay(LocalDate previousDate, Optional<ShiftRosterEntity> previousShiftRoster) {
        if (previousShiftRoster.isPresent()) {
            Map<Integer, String> dayGetterMap = createDayGetterMap(previousDate.getDayOfMonth());
            String getterMethodName = dayGetterMap.get(previousDate.getDayOfMonth());
            if (getterMethodName != null) {
                try {
                    // Use reflection to call the getter method for the specific day
                    Method getterMethod = previousShiftRoster.get().getClass().getMethod(getterMethodName);
                    return (Integer) getterMethod.invoke(previousShiftRoster.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Map<Integer, String> createDayGetterMap(int day) {
        Map<Integer, String> dayGetterMap = new HashMap<>();
        // Assume the getter method pattern is "getDayN" where N is the day number
        dayGetterMap.put(day, AppConstant.GET_DAY + String.format(AppConstant.STRING_DAY_FORMAT, day));
        return dayGetterMap;
    }

    private boolean validateWeekOffsAndWorkingDays(String employeeId, Map<LocalDate, String> shifts, List<String> errors, Set<LocalDate> additionalValidationDates) {
        LocalDate minDate = shifts.keySet().stream().min(LocalDate::compareTo).orElse(null);
        LocalDate maxDate = shifts.keySet().stream().max(LocalDate::compareTo).orElse(null);

        if (minDate == null) {
            errors.add(AppConstant.NO_SHIFT + employeeId);
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
                    .filter(entry -> AppConstant.WO.equalsIgnoreCase(entry.getValue()))
                    .count();

            if (weekOffCount < 1 || weekOffCount > 2) {
                errors.add(String.format(AppConstant.INVALID_WEEK_OFFS, employeeId ));
                return false;
            }

            for (Map.Entry<LocalDate, String> entry : currentWeekShifts.entrySet()) {
                LocalDate date = entry.getKey();
                String shift = entry.getValue();
                boolean isWorkingDay = !additionalValidationDates.contains(date) && !AppConstant.WO.equalsIgnoreCase(shift);

                if (isWorkingDay) {
                    consecutiveWorkingDays++;
                    if (consecutiveWorkingDays > 6) {
                        errors.add(String.format(AppConstant.CONSECUTIVE_WORKING_DAYS, employeeId));
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
                shifts.put(date, shiftRosterValue == 0 ? AppConstant.WO : AppConstant.WD);
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