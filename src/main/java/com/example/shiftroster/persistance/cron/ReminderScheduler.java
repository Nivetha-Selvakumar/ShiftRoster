package com.example.shiftroster.persistance.cron;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistance.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistance.secondary.repository.ShiftRosterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReminderScheduler {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRosterRepo shiftRosterRepo;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    String fromMail;

    @Scheduled(cron= "${spring.mail.cron}")
    public void sendReminderTask() {

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        int daysInMonth = yearMonth.lengthOfMonth();


        List<ShiftRosterEntity> shiftRosterEntityList = shiftRosterRepo.findAllByMonthAndYear(currentMonth,currentYear);

        Map<Integer, Set<Integer>> unassignedShiftsMap = shiftRosterEntityList.stream()
                .map(shiftRoster -> Map.entry(shiftRoster.getEmpId(), getUnassignedDays(shiftRoster, daysInMonth)))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        if (!shiftRosterEntityList.isEmpty()) {
            // Query to get employees with unassigned shifts and their appraisers
            List<EmployeeEntity> employees = new ArrayList<>();
            for(ShiftRosterEntity s : shiftRosterEntityList){
                Optional<EmployeeEntity> employeeEntity = employeeRepo.findByIdAndEmpStatus(s.getEmpId(),EnumStatus.ACTIVE);
                employees.add(employeeEntity.get());
            }

            // Group employees by appraiser
            Map<EmployeeEntity, List<EmployeeEntity>> employeesByAppraiser = employees.stream()
                    .collect(Collectors.groupingBy(EmployeeEntity::getAppraiser));

            // Send notification emails to appraisers
            employeesByAppraiser.forEach((appraiser, reportees) -> sendEmail(appraiser, reportees,unassignedShiftsMap));
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
            Field field = ShiftRosterEntity.class.getDeclaredField("day" + String.format("%02d", day));
            field.setAccessible(true);
            return (Integer) field.get(shiftRoster);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid day: " + day, e);
        }
    }

    private void sendEmail(EmployeeEntity appraiser, List<EmployeeEntity> reportees, Map<Integer, Set<Integer>> unassignedShiftsMap) {
        String to = appraiser.getEmail();
        String subject = "Unassigned Shifts Notification";
        StringBuilder text = new StringBuilder("The following reportees have unassigned shifts:\n\n");

        for (EmployeeEntity reportee : reportees) {
            text.append(reportee.getEmpName()).append(" (").append(reportee.getEmpCode()).append(") - Unassigned Dates of this month are: ");
            Set<Integer> unassignedDays = unassignedShiftsMap.get(reportee.getId());
            text.append(unassignedDays.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            text.append("\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text.toString());

        emailSender.send(message);
    }

}