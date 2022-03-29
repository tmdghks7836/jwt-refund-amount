package com.jwt.szs.utils;

import com.jwt.szs.utils.type.DateFormatType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeUtils {


    public static LocalDate parseLocalDate(String dateString, DateFormatType dateFormatType) {

        String separator = dateFormatType.getSeparator();
        String format = String.format("yyyy%sMM%sdd", separator, separator);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return LocalDate.parse(dateString, formatter);
    }
}
