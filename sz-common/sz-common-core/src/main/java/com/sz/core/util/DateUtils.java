package com.sz.core.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author sz
 * @since 2022/5/28 16:44
 */
public class DateUtils {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private DateUtils() {
        throw new IllegalStateException("DateUtils class Illegal");
    }

    /**
     * 获取当前日期和时间，格式化为字符串。
     * <p>
     * 该方法返回当前的日期和时间，格式为 `TIME_PATTERN` 指定的格式。
     * </p>
     *
     * @return 当前日期和时间的字符串表示
     */
    public static String getCurrentDateTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(TIME_PATTERN);
        LocalDateTime now = LocalDateTime.now();
        return df.format(now);
    }

    /**
     * 将字符串类型的日期时间转换为 `LocalDateTime` 对象。
     * <p>
     * 该方法使用指定的日期时间格式（`TIME_PATTERN`）将字符串解析为 `LocalDateTime` 对象。
     * </p>
     *
     * @param dateTime
     *            日期时间字符串，必须符合 `TIME_PATTERN` 格式
     * @return 解析后的 `LocalDateTime` 对象
     */
    public static LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(TIME_PATTERN);
        return LocalDateTime.parse(dateTime, fmt);
    }

    /**
     * 获取当前日期，格式化为字符串。
     * <p>
     * 该方法返回当前日期，格式为 `DATE_PATTERN` 指定的格式。
     * </p>
     *
     * @return 当前日期的字符串表示
     */
    public static String getDefaultDate() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDateTime now = LocalDateTime.now();
        return df.format(now);
    }

    /**
     * 将 `Date` 对象转换为格式化后的字符串。
     * <p>
     * 该方法使用 `SimpleDateFormat` 将 `Date` 对象转换为 `yyyy-MM-dd HH:mm:ss` 格式的字符串。
     * </p>
     *
     * @param datetime
     *            `Date` 对象
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(Date datetime) {
        // 创建 SimpleDateFormat 实例，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
        // 使用 SimpleDateFormat 将 Date 对象转换为字符串
        return sdf.format(datetime);
    }

    /**
     * 将 `LocalDateTime` 对象转换为 `Date` 对象。
     * <p>
     * 该方法将 `LocalDateTime` 转换为 `Instant`，然后转换为 `Date`。
     * </p>
     *
     * @param localDateTime
     *            `LocalDateTime` 对象
     * @return 转换后的 `Date` 对象
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为 Instant
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        // 将 Instant 转换为 Date
        return Date.from(instant);
    }

    /**
     * 将 `Date` 对象转换为 `LocalDateTime` 对象。
     * <p>
     * 该方法将 `Date` 对象转换为 `Instant`，然后转换为 `LocalDateTime`。
     * </p>
     *
     * @param date
     *            `Date` 对象
     * @return 转换后的 `LocalDateTime` 对象
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        // 将 Date 转换为 Instant
        Instant instant = date.toInstant();
        // 将 Instant 转换为 LocalDateTime
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
