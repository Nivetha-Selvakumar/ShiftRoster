package com.example.shiftroster.persistence.util;

import org.springframework.context.annotation.Configuration;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

@Configuration
public class DateTimeUtil {
    public static Date convertStringDateFormat(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat(AppConstant.STRING_DATE);
        return  inputFormat.parse(date);
    }
}
