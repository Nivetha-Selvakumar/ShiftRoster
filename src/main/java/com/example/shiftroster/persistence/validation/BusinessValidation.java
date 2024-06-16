package com.example.shiftroster.persistence.validation;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.NotFoundException;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import com.example.shiftroster.persistence.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BusinessValidation {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRepo shiftRepo;

    public EmployeeEntity employeeValidation(String empId) throws CommonException {
        return employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(AppConstant.INVALID_EMPLOYEE));
    }

    public void validateShiftDate(Map<String, Map<LocalDate, String>> employeeShiftData, List<String> errors) {
        for (Map.Entry<String, Map<LocalDate, String>> entry : employeeShiftData.entrySet()) {

            Map<LocalDate, String> shifts = entry.getValue();

            validateWeekOffsAndWorkingDays(shifts, errors);
//            validateConsecutiveWorkingDays(shifts, errors);
//            validateWorkingHours(shifts, errors);
        }

    }

    private void validateWorkingHours(Map<LocalDate, String> shifts, List<String> errors) {
        LocalDate previousDate = null;
        LocalTime previousEndTime = null;

        for (Map.Entry<LocalDate, String> shiftEntry : shifts.entrySet()) {
            LocalDate currentDate = shiftEntry.getKey();
            String shift = shiftEntry.getValue();

            if (!"UA".equalsIgnoreCase(shift) && !"WO".equalsIgnoreCase(shift)) {
                Optional<ShiftEntity> shiftEntity = shiftRepo.findByShiftNameAndStatus(shift,EnumStatus.ACTIVE);
                // Get the shift start and end time for the current date
                LocalTime startTime = DateTimeUtil.convertTimeToLocalTime(shiftEntity.get().getFromTime());
                LocalTime endTime = DateTimeUtil.convertTimeToLocalTime(shiftEntity.get().getToTime());

                if (previousDate != null && previousEndTime != null) {
                    // Calculate the difference between the previous shift's end time and current shift's start time
                    Duration duration = Duration.between(previousEndTime, startTime);
                    long hoursDifference = duration.toHours();

                    if (hoursDifference < 8) {
                        errors.add("Shifts on " + previousDate.toString() + " and " + currentDate.toString() + " do not have an 8-hour difference.");
                        // Skip processing further shifts if the condition fails
                        break;
                    }
                }

                // Update the previous date and end time for the next iteration
                previousDate = currentDate;
                previousEndTime = endTime;
            }
        }
    }

    private void validateConsecutiveWorkingDays(Map<LocalDate, String> shifts, List<String> errors) {
        int consecutiveWorkingDays = 0;

        for (Map.Entry<LocalDate, String> shiftEntry : shifts.entrySet()) {
            String shift = shiftEntry.getValue();

            if (!"UA".equalsIgnoreCase(shift) && !"WO".equalsIgnoreCase(shift)) {
                consecutiveWorkingDays++;
                if (consecutiveWorkingDays > 6) {
                    errors.add("Invalid consecutive working days. Maximum 6 consecutive working days allowed.");
                    break;
                }
            } else {
                consecutiveWorkingDays = 0;
            }
        }
    }

    private void validateWeekOffsAndWorkingDays(Map<LocalDate, String> shifts, List<String> errors) {
        int weekOffsCount = 0;
        int workingDaysCount = 0;

        for (Map.Entry<LocalDate, String> shiftEntry : shifts.entrySet()) {
            String shift = shiftEntry.getValue();

            if ("WO".equalsIgnoreCase(shift)) {
                weekOffsCount++;
            } else if (!"UA".equalsIgnoreCase(shift)) {
                workingDaysCount++;
            }
        }

        if (weekOffsCount < 1 || weekOffsCount > 2) {
            errors.add("Invalid number of week-offs. Minimum 1 and maximum 2 week-offs allowed.");
        }

        if (workingDaysCount < 5 || workingDaysCount > 6) {
            errors.add("Invalid number of working days. Minimum 5 and maximum 6 working days allowed.");
        }
    }
}
