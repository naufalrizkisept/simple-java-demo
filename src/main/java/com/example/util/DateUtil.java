package com.example.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private DateUtil() {}

    public static String localDateTimeToString(LocalDateTime date, String formatter) {
        return DateTimeFormatter.ofPattern(formatter).format(date);
    }
}
