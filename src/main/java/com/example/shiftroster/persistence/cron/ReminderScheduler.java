package com.example.shiftroster.persistence.cron;

import com.example.shiftroster.persistence.Enum.EnumRole;
import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistence.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistence.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistence.util.AppConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRosterRepo shiftRosterRepo;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    String fromMail;

    @Scheduled(cron = "${spring.mail.cron}")
    public void sendReminderTask() {
        try {
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
            int daysInMonth = yearMonth.lengthOfMonth();
            List<EmployeeEntity> appraiserEntityList = employeeRepo.findAllByRoleAndEmpStatus(EnumRole.APPRAISER, EnumStatus.ACTIVE);

            for (EmployeeEntity appraiser : appraiserEntityList) {
                List<EmployeeEntity> employeeEntityList = employeeRepo.findAllByRoleAndEmpStatusAndAppraiserId(EnumRole.EMPLOYEE, EnumStatus.ACTIVE, appraiser);

                if (employeeEntityList != null && !employeeEntityList.isEmpty()) {
                    List<Integer> employeeIds = employeeEntityList.stream()
                            .map(EmployeeEntity::getId)
                            .collect(Collectors.toList());

                    List<ShiftRosterEntity> shiftRosterEntityList = shiftRosterRepo.findAllByEmpIdInAndMonthAndYear(employeeIds, currentMonth, currentYear);

                    Map<Integer, Set<Integer>> unassignedShiftsMap = new HashMap<>();
                    List<EmployeeEntity> noShiftAssignedEmployees = new ArrayList<>();

                    // Create a set of employee IDs that have shift rosters
                    Set<Integer> employeesWithShifts = shiftRosterEntityList.stream()
                            .map(ShiftRosterEntity::getEmpId)
                            .collect(Collectors.toSet());

                    for (EmployeeEntity employee : employeeEntityList) {
                        int empId = employee.getId();
                        if (employeesWithShifts.contains(empId)) {
                            // Employee has shift roster entries, check for unassigned shifts
                            List<ShiftRosterEntity> employeeShifts = shiftRosterEntityList.stream()
                                    .filter(shift -> shift.getEmpId() == empId)
                                    .collect(Collectors.toList());
                            Set<Integer> unassignedDays = getUnassignedDays(employeeShifts, daysInMonth);
                            if (!unassignedDays.isEmpty()) {
                                unassignedShiftsMap.put(empId, unassignedDays);
                            }
                        } else {
                            // Employee has no shift roster entries
                            noShiftAssignedEmployees.add(employee);
                        }
                    }

                    if (!unassignedShiftsMap.isEmpty() || !noShiftAssignedEmployees.isEmpty()) {
                        sendEmail(appraiser, unassignedShiftsMap, noShiftAssignedEmployees, logger);
                    }

                    if (unassignedShiftsMap.isEmpty() && noShiftAssignedEmployees.isEmpty()) {
                        logger.error(AppConstant.ALL_EMPLOYEES_HAVE_SHIFT);
                    }
                } else {
                    logger.error(AppConstant.NO_ACTIVE_EMPLOYEES_FOUND_FOR_APPRAISER);
                }
            }
        } catch (Exception e) {
            logger.error(AppConstant.ERROR_IN_SENDING_EMAIL, e.getMessage(), e);
        }
    }

    private Set<Integer> getUnassignedDays(List<ShiftRosterEntity> shiftRosterList, int daysInMonth) {
        return java.util.stream.IntStream.rangeClosed(1, daysInMonth)
                .filter(day -> shiftRosterList.stream().noneMatch(shift -> getShiftValueForDay(shift, day) != null))
                .boxed()
                .collect(Collectors.toSet());
    }

    private void sendEmail(EmployeeEntity appraiser, Map<Integer, Set<Integer>> unassignedDays, List<EmployeeEntity> noShiftAssignedEmployees, Logger logger) throws MessagingException {
        String subject = AppConstant.EMAIL_SUBJECT;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(AppConstant.EXCEL_DATE_FORMAT);
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        StringBuilder reporteesWithUnassignedShifts = new StringBuilder();
        StringBuilder reporteesWithNoShifts = new StringBuilder();

        unassignedDays.forEach((empId, days) -> {
            Optional<EmployeeEntity> employeeEntity = employeeRepo.findById(empId);
            String formattedDates = days.stream()
                    .map(day -> LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day).format(dateFormatter))
                    .collect(Collectors.joining(AppConstant.COMMA_SPACE));
            reporteesWithUnassignedShifts.append(String.format(
                    AppConstant.HTML_LIST_DATA,
                    employeeEntity.get().getEmpName(),
                    employeeEntity.get().getEmpCode(),
                    formattedDates
            ));
        });

        noShiftAssignedEmployees.forEach(employee -> {
            reporteesWithNoShifts.append(String.format(
                    AppConstant.HTML_NO_SHIFT_CONTENT,
                    employee.getEmpName(),
                    employee.getEmpCode()
            ));
        });

        try {
            if (isValidEmail(appraiser.getEmail())) {
                String htmlContent = htmlMailSender(appraiser, reporteesWithUnassignedShifts, reporteesWithNoShifts);

                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(fromMail);
                helper.setTo(appraiser.getEmail());
                helper.setSubject(subject);
                helper.setText(htmlContent, true);  // true indicates HTML

                emailSender.send(message);
            } else {
                logger.error(AppConstant.EMAIL_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private Integer getShiftValueForDay(ShiftRosterEntity shiftRoster, int day) {
        try {
            Field field = ShiftRosterEntity.class.getDeclaredField(AppConstant.DAY + String.format(AppConstant.STRING_2D_FORMAT, day));
            field.setAccessible(true);
            return (Integer) field.get(shiftRoster);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(AppConstant.INVALID_DAY + day, e);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = AppConstant.EMAIL_REGEX;
        return email != null && email.matches(emailRegex);
    }

    private String htmlMailSender(EmployeeEntity appraiser, StringBuilder reporteesWithUnassignedShifts, StringBuilder reporteesWithNoShifts) {
        String unassignedShiftsSection = !reporteesWithUnassignedShifts.isEmpty()
                ? String.format(AppConstant.HTML_REPORTEES_UNASSIGNED_SHIFT, reporteesWithUnassignedShifts)
                : AppConstant.EMPTY_STRING;

        String noShiftsSection = !reporteesWithNoShifts.isEmpty()
                ? String.format(AppConstant.HTML_NO_SHIFT, reporteesWithNoShifts)
                : AppConstant.EMPTY_STRING;

        return String.format(
                "<html lang=\"en\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "<style>" +
                        "body {" +
                        "font-family: Arial, sans-serif;" +
                        "background-color: #f4f4f9;" +
                        "color: #333;" +
                        "margin: 0;" +
                        "padding: 0;" +
                        "}" +
                        ".container {" +
                        "max-width: 600px;" +
                        "margin: 30px auto;" +
                        "background-color: #fff;" +
                        "padding: 20px;" +
                        "border-radius: 8px;" +
                        "box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);" +
                        "}" +
                        "h1 {" +
                        "color: #007BFF;" +
                        "font-size: 24px;" +
                        "margin-bottom: 20px;" +
                        "}" +
                        "p {" +
                        "font-size: 16px;" +
                        "line-height: 1.6;" +
                        "}" +
                        "ul {" +
                        "padding-left: 20px;" +
                        "}" +
                        "li {" +
                        "margin-bottom: 10px;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "<h1>Shift Assignment Notification</h1>" +
                        "<p>Hi %s,</p>" +
                        "%s" +
                        "%s" +
                        "<p>Please take the necessary action to assign the shifts.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                appraiser.getEmpName(),
                unassignedShiftsSection,
                noShiftsSection
        );
    }
}
