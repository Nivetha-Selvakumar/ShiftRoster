package com.example.shiftroster.persistance.cron;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistance.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistance.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistance.util.AppConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            List<ShiftRosterEntity> shiftRosterEntityList = shiftRosterRepo.findAllByMonthAndYear(currentMonth, currentYear);

            Map<Integer, Set<Integer>> unassignedShiftsMap = shiftRosterEntityList.stream()
                    .map(shiftRoster -> Map.entry(shiftRoster.getEmpId(), getUnassignedDays(shiftRoster, daysInMonth)))
                    .filter(entry -> !entry.getValue().isEmpty())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!shiftRosterEntityList.isEmpty()) {
                List<EmployeeEntity> employees = new ArrayList<>();
                for (ShiftRosterEntity s : shiftRosterEntityList) {
                    Optional<EmployeeEntity> employeeEntity = employeeRepo.findByIdAndEmpStatus(s.getEmpId(), EnumStatus.ACTIVE);
                    employeeEntity.ifPresent(employees::add);
                }

                Map<EmployeeEntity, List<EmployeeEntity>> employeesByAppraiser = employees.stream()
                        .collect(Collectors.groupingBy(EmployeeEntity::getAppraiser));

                List<Exception> exceptions = new ArrayList<>();

                employeesByAppraiser.forEach((appraiser, reportees) -> {
                    String appraiserEmail = appraiser.getEmail();
                    if (appraiserEmail == null || appraiserEmail.isEmpty() || !isValidEmail(appraiserEmail)) {
                        logger.warn(AppConstant.INVALID_MISSING_EMAIL + appraiser.getEmpName());
                        // Send error message to Postman
                        sendErrorMessageToPostman(AppConstant.INVALID_MISSING_EMAIL + appraiser.getEmpName());
                    } else {
                        try {
                            sendEmail(appraiser, reportees, unassignedShiftsMap);
                        } catch (MessagingException e) {
                            logger.error(AppConstant.FAILED_TO_SEND_MAIL + appraiserEmail, e);
                            exceptions.add(e);
                        }
                    }
                });

                if (!exceptions.isEmpty()) {
                    // Throwing a custom aggregated exception with all collected exceptions
                    throw new AggregateException(AppConstant.FAILED_TO_SEND_MAIL, exceptions);
                }
            }
        } catch (Exception e) {
            logger.error(AppConstant.ERROR_IN_SENDING_REMINDER + e.getMessage(), e);
            // Send error message to Postman
            sendErrorMessageToPostman(AppConstant.ERROR_IN_SENDING_REMINDER  + e.getMessage());
        }
    }

    private Set<Integer> getUnassignedDays(ShiftRosterEntity shiftRoster, int daysInMonth) {
        return java.util.stream.IntStream.rangeClosed(1, daysInMonth)
                .filter(day -> getShiftValueForDay(shiftRoster, day) == null)
                .boxed()
                .collect(Collectors.toSet());
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

    private void sendEmail(EmployeeEntity appraiser, List<EmployeeEntity> reportees, Map<Integer, Set<Integer>> unassignedShiftsMap) throws MessagingException {
        String to = appraiser.getEmail();
        String subject = AppConstant.EMAIL_SUBJECT;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(AppConstant.EXCEL_DATE_FORMAT);
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        StringBuilder reporteesInfo = new StringBuilder();
        for (EmployeeEntity reportee : reportees) {
            Set<Integer> unassignedDays = unassignedShiftsMap.get(reportee.getId());
            String formattedDates = unassignedDays.stream()
                    .map(day -> LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day).format(dateFormatter))
                    .collect(Collectors.joining(", "));

            reporteesInfo.append(String.format(
                    AppConstant.HTML_DATE_LIST,
                    reportee.getEmpName(),
                    reportee.getEmpCode(),
                    formattedDates
            ));
        }
        String htmlContent = htmlMailSender(appraiser,reporteesInfo);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromMail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  // true indicates HTML

        emailSender.send(message);
    }

    private String htmlMailSender(EmployeeEntity appraiser, StringBuilder reporteesInfo) {
        return  String.format(
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
                        ".btn {" +
                        "display: inline-block;" +
                        "background-color: #007BFF;" +
                        "color: #fff;" +
                        "padding: 10px 20px;" +
                        "text-decoration: none;" +
                        "border-radius: 4px;" +
                        "margin-top: 20px;" +
                        "}" +
                        ".btn:hover {" +
                        "background-color: #0056b3;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "<h1>Unassigned Shifts Notification</h1>" +
                        "<p>Dear %s,</p>" +
                        "<p>The following reportees have unassigned shifts:</p>" +
                        "<ul>%s</ul>" +
                        "<p>Please take the necessary action to assign the shifts.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                appraiser.getEmpName(),
                reporteesInfo.toString()
        );
    }



    private void sendErrorMessageToPostman(String errorMessage) {
        // Logic to send error message to Postman, you can use ResponseEntity or any other mechanism
        logger.error(errorMessage); // Log the error for debugging purposes
    }
}

class AggregateException extends RuntimeException {
    private final List<Exception> exceptions;

    public AggregateException(String message, List<Exception> exceptions) {
        super(message);
        this.exceptions = exceptions;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }
}
