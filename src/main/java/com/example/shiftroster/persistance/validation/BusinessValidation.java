package com.example.shiftroster.persistance.validation;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.Exception.NotFoundException;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistance.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistance.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistance.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistance.util.AppConstant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class BusinessValidation {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRepo shiftRepo;


    public EmployeeEntity employeeValidation(String empId) throws CommonException {
        return employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException(AppConstant.EMPLOYEE_NOT_FOUND));
    }

    public ShiftEntity shiftValidation(String shift) throws CommonException {
        return  shiftRepo.findByShiftNameAndStatus(shift, EnumStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException(AppConstant.SHIFT_NOT_FOUND));
    }

    public void bulkUploadValidationForShiftAssign(String currentDate, List<String> shifts) throws CommonException {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            List<Date> shiftDates = shifts.stream()
                    .map(shift -> {
                        try {
                            return sdf.parse(currentDate + " " + shift);
                        } catch (ParseException e) {
                            throw new RuntimeException("Error parsing date.");
                        }
                    })
                    .sorted()
                    .collect(Collectors.toList());

            if (!isValidShiftDates(shiftDates)) {
                throw new CommonException("Invalid shift dates.");
            }
            assignShiftsToEmployees(shiftDates);
        } catch (RuntimeException | CommonException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private boolean isValidShiftDates(List<Date> shiftDates) {
        int weekOffCount = (int) shiftDates.stream()
                .map(date -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    return cal.get(Calendar.DAY_OF_WEEK);
                })
                .filter(dayOfWeek -> dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
                .count();

        long maxContinuousDays = shiftDates.stream()
                .collect(Collectors.groupingByConcurrent(date -> date.getTime() / (24 * 60 * 60 * 1000)))
                .values()
                .stream()
                .mapToLong(List::size)
                .max()
                .orElse(0);

        boolean validShiftDifference = shiftDates.stream()
                .mapToLong(Date::getTime)
                .reduce((prev, curr) -> {
                    long diffMillis = curr - prev;
                    return diffMillis < AppConstant.MIN_SHIFT_DIFFERENCE * 60 * 60 * 1000? -1 : curr;
                })
                .getAsLong() != -1;

        return (weekOffCount >= 1 && weekOffCount <= 2) &&
                (shiftDates.size() >= 5 && shiftDates.size() <= 6) &&
                (maxContinuousDays <= 6) &&
                validShiftDifference;
    }

    private void assignShiftsToEmployees(List<Date> shiftDates) {

    }


//    public void validate(Row row) throws CommonException {
//        validateShiftDifference(row);
//        validateWeekOffs(row);
//        validateShiftDays(row);
//        validateContinuity(row);
//    }
//    private static void validateShiftDifference(Row row) throws CommonException {
//        // Check if there's at least 8 hours difference between shifts
//        for (int i = 1; i <= 7; i++) {
//            Cell currentCell = row.getCell(i);
//            if (currentCell == null || currentCell.getStringCellValue().equalsIgnoreCase("off")) {
//                continue;
//            }
//            if (i < 7) {
//                Cell nextCell = row.getCell(i + 1);
//                if (nextCell != null && !nextCell.getStringCellValue().equalsIgnoreCase("off")) {
//                    LocalTime currentTime = LocalTime.parse(currentCell.getStringCellValue().split("-")[1]);
//                    LocalTime nextTime = LocalTime.parse(nextCell.getStringCellValue().split("-")[0]);
//                    if (!nextTime.isAfter(currentTime.plusHours(8))) {
//                        throw new CommonException("Shifts should have at least 8 hours difference between shifts.");
//                    }
//                }
//            }
//        }
//    }
//
//    private static void validateWeekOffs(Row row) throws CommonException {
//        // Count the number of week offs in the row
//        long weekOffs = countWeekOffs(row);
//        if (weekOffs < 1 || weekOffs > 2) {
//            throw new CommonException("Minimum 1 and maximum 2 weeks off must be availed for a week.");
//        }
//    }
//
//    private static void validateShiftDays(Row row) throws CommonException {
//        // Count the number of shift days in the row
//        long shiftDays = countShiftDays(row);
//        if (shiftDays < 5 || shiftDays > 6) {
//            throw new CommonException("Minimum 5 and maximum 6 days of shift must be assigned.");
//        }
//    }
//
//    private static void validateContinuity(Row row) throws CommonException {
//        // Check for continuity in shifts
//        List<String> shifts = getShiftsFromRow(row);
//        int continuousDays = 0;
//        for (String shift : shifts) {
//            if (!shift.equalsIgnoreCase("off")) {
//                continuousDays++;
//                if (continuousDays > 6) {
//                    throw new CommonException("Shift should not be assigned more than 6 days continuously.");
//                }
//            } else {
//                continuousDays = 0;
//            }
//        }
//    }
//
//    private static long countWeekOffs(Row row) {
//        long weekOffs = 0;
//        for (int i = 1; i <= 7; i++) {
//            Cell cell = row.getCell(i);
//            if (cell != null && cell.getStringCellValue().equalsIgnoreCase("off")) {
//                weekOffs++;
//            }
//        }
//        return weekOffs;
//    }
//
//    private static long countShiftDays(Row row) {
//        long shiftDays = 0;
//        for (int i = 1; i <= 7; i++) {
//            Cell cell = row.getCell(i);
//            if (cell != null && !cell.getStringCellValue().equalsIgnoreCase("off")) {
//                shiftDays++;
//            }
//        }
//        return shiftDays;
//    }
//
//    private static List<String> getShiftsFromRow(Row row) {
//        // Extract shifts from the row
//        // Example: If shifts are in columns B to H
//        // Adjust the cell range based on your Excel sheet structure
//        List<String> shifts = new ArrayList<>();
//        for (int i = 1; i <= 7; i++) {
//            Cell cell = row.getCell(i);
//            if (cell != null) {
//                shifts.add(cell.getStringCellValue());
//            }
//        }
//        return shifts;
//    }


}
