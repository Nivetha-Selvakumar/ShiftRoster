


package com.example.shiftroster.persistance.cron;

import com.example.shiftroster.persistance.Enum.EnumRole;
import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistance.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistance.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistance.util.AppConstant;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
            List<Exception> exceptions = new ArrayList<>();

            List<EmployeeEntity> employeeEntityList = employeeRepo.findAllByRoleAndEmpStatus(EnumRole.EMPLOYEE,EnumStatus.ACTIVE);

            for(EmployeeEntity employeeEntity : employeeEntityList){
                Optional<ShiftRosterEntity> optionalShiftRoster = shiftRosterRepo.findByEmpIdAndMonthAndYear(employeeEntity.getId(), currentMonth, currentYear);
                if (optionalShiftRoster.isPresent()) {
                    Set<Integer> unassignedDays = getUnassignedDays(optionalShiftRoster.get(), daysInMonth);
                    if (!unassignedDays.isEmpty()) {
                        sendEmail(employeeEntity.getAppraiser(), employeeEntity, unassignedDays, exceptions);
                    }else{
                        exceptions.add(new Exception("No Unassigned Shift for the employee"+ employeeEntity.getEmpName()));
                    }
                }else{
                    sendEmailNoShiftAssgin(employeeEntity.getAppraiser(),employeeEntity,exceptions);
                }
            }
            if (!exceptions.isEmpty()) {
                // Throwing a custom aggregated exception with all collected exceptions
                throw new AggregateException(AppConstant.FAILED_TO_SEND_MAIL, exceptions);
            }
        } catch (Exception e) {
            logger.error(AppConstant.ERROR_IN_SENDING_REMINDER + e.getMessage(), e);
            // Send error message to Postman
            sendErrorMessageToLogger(AppConstant.ERROR_IN_SENDING_REMINDER  + e.getMessage());
        }
    }

    private void sendEmailNoShiftAssgin(EmployeeEntity appraiser, EmployeeEntity employeeEntity, List<Exception> exceptions) throws MessagingException {
        String subject = AppConstant.EMAIL_SUBJECT;
        StringBuilder reporteesInfo = new StringBuilder();

        reporteesInfo.append(String.format(
                employeeEntity.getEmpName(),AppConstant.STRING_SPACE,employeeEntity.getEmpCode()
        ));
        try{
            Optional<EmployeeEntity> appraiserEmployee = employeeRepo.findByIdAndEmpStatus(appraiser.getId(),EnumStatus.ACTIVE);
            if(appraiserEmployee.isPresent()){
                if(isValidEmail(appraiserEmployee.get().getEmail())) {
                    String htmlContent =htmlContentForNoShiftAssign(appraiser,reporteesInfo);

                    MimeMessage message = emailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setFrom(fromMail);
                    helper.setTo(employeeEntity.getAppraiser().getEmail());
                    helper.setSubject(subject);
                    helper.setText(htmlContent, true);  // true indicates HTML

                    emailSender.send(message);
                }else{
                    exceptions.add(new Exception("Email not valid"));
                }
            }else{
                exceptions.add(new Exception("Appraiser not found for employee : "+employeeEntity.getEmpName()));
            }
        }catch (Exception e){
            exceptions.add(e);
        }
    }

    private void sendEmail(EmployeeEntity appraiser, EmployeeEntity employeeEntity, Set<Integer> unassignedDays, List<Exception> exceptions) throws MessagingException {
        String subject = AppConstant.EMAIL_SUBJECT;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(AppConstant.EXCEL_DATE_FORMAT);
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        StringBuilder reporteesInfo = new StringBuilder();
        String formattedDates = unassignedDays.stream()
                .map(day -> LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day).format(dateFormatter))
                .collect(Collectors.joining(", "));

        reporteesInfo.append(String.format(
                AppConstant.HTML_DATE_LIST,
                employeeEntity.getEmpName(),
                employeeEntity.getEmpCode(),
                formattedDates
        ));
        try{
            Optional<EmployeeEntity> appraiserEmployee = employeeRepo.findByIdAndEmpStatus(appraiser.getId(),EnumStatus.ACTIVE);
            if(appraiserEmployee.isPresent()){
                if(isValidEmail(appraiserEmployee.get().getEmail())) {
                    String htmlContent = htmlMailSender(appraiser,reporteesInfo);

                    MimeMessage message = emailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setFrom(fromMail);
                    helper.setTo(employeeEntity.getAppraiser().getEmail());
                    helper.setSubject(subject);
                    helper.setText(htmlContent, true);  // true indicates HTML

                    emailSender.send(message);
                }else{
                    exceptions.add(new Exception("Email not valid"));
                }
            }else{
                exceptions.add(new Exception("Appraiser not found for employee : "+employeeEntity.getEmpName()));
            }
        }catch (Exception e){
            exceptions.add(e);
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

    private String htmlContentForNoShiftAssign(EmployeeEntity appraiser, StringBuilder reporteesInfo) {
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
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "<h1>No Shift Assigned Notification</h1>" +
                        "<p>Dear %s,</p>" +
                        "<p>No shifts have been assigned to the employee: %s (%s).</p>" +
                        "<p>Please take the necessary action to assign shifts.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                appraiser.getEmpName(), reporteesInfo.toString()
        );
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
                        "<p>The following reportee have unassigned shifts:</p>" +
                        "<ul>%s</ul>" +
                        "<p>Please take the necessary action to assign the shifts.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                appraiser.getEmpName(),
                reporteesInfo.toString()
        );
    }

    private void sendErrorMessageToLogger(String errorMessage) {
        logger.error(errorMessage);
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
