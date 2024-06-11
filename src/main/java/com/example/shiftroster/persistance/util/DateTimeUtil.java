package com.example.shiftroster.persistance.util;

import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class DateTimeUtil {
    public static Date convertStringDateFormat(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        Date convertDate = inputFormat.parse(date);
        return convertDate;
    }
}
