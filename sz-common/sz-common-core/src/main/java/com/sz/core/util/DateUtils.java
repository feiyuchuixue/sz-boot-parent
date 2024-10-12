package com.sz.core.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: sz
 * @date: 2022/5/28 16:44
 * @description: 时间工具类
 */
public class DateUtils {

    private static String DATE_PATTERN = "yyyy-MM-dd";

    private static String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private DateUtils() {
        throw new IllegalStateException("DateUtils class Illegal");
    }

    public static String getCurrentDateTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(TIME_PATTERN);
        LocalDateTime now = LocalDateTime.now();
        return df.format(now);
    }

    public static LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(TIME_PATTERN);
        return LocalDateTime.parse(dateTime, fmt);
    }

    public static String getDefaultDate() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime now = LocalDateTime.now();
        return df.format(now);
    }

    public static String formatDateTime(Date datetime) {
        // 创建 SimpleDateFormat 实例，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用 SimpleDateFormat 将 Date 对象转换为字符串
        String dateString = sdf.format(datetime);
        return dateString;
    }

    // 将 LocalDateTime 转换为 Date
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为 Instant
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        // 将 Instant 转换为 Date
        return Date.from(instant);
    }

    // 将 Date 转换为 LocalDateTime
    public static LocalDateTime dateToLocalDateTime(Date date) {
        // 将 Date 转换为 Instant
        Instant instant = date.toInstant();
        // 将 Instant 转换为 LocalDateTime
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
