package com.sz.excel.support;

import com.sz.core.util.Utils;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.annotation.ExcelEnumFormat;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * Excel 安全导出值解析器。
 * <p>
 * 单字段导出失败时返回安全值（通常为 null），并输出字段级别详细日志。
 * </p>
 */
@Slf4j
public final class ExcelExportSafeValueResolver {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ExcelExportSafeValueResolver() {
    }

    public static ResolvedValue resolve(Object rowObject, Field field, int rowIndex) {
        try {
            Object value = Utils.invokeGetter(rowObject, field.getName());
            return new ResolvedValue(resolveValue(field, value), false);
        } catch (Exception ex) {
            logWarn(rowObject == null ? null : rowObject.getClass(), rowIndex, field, null, ex);
            return new ResolvedValue(null, true);
        }
    }

    public record ResolvedValue(Object value, boolean failed) {
    }

    private static Object resolveValue(Field field, Object value) {
        if (value == null) {
            return null;
        }
        if (field.isAnnotationPresent(DictFormat.class)) {
            return value;
        }
        if (field.isAnnotationPresent(ExcelEnumFormat.class) && value instanceof Enum<?> enumValue) {
            return value;
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean || value instanceof Enum<?>) {
            return value;
        }
        if (value instanceof LocalDateTime || value instanceof LocalDate || value instanceof LocalTime) {
            return value;
        }
        if (value instanceof Map<?, ?> || value instanceof Collection<?>) {
            throw new IllegalStateException("复杂类型未配置导出转换: " + value.getClass().getSimpleName());
        }
        return value;
    }

    private static void logWarn(Class<?> rowClass, int rowIndex, Field field, Object value, Exception ex) {
        ExcelExportFieldError error = new ExcelExportFieldError(rowClass, rowIndex, field.getName(), resolveHeaderName(field),
                value == null ? "unknown" : value.getClass().getSimpleName(), ex.getMessage());
        log.warn("[Excel export warn] class={}, row={}, field={}, header={}, valueType={}, msg={}，已按空值输出",
                error.rowClass() == null ? "unknown" : error.rowClass().getSimpleName(), error.rowIndex(), error.fieldName(), error.headerName(),
                error.valueType(), error.message(), ex);
    }

    private static String resolveHeaderName(Field field) {
        cn.idev.excel.annotation.ExcelProperty excelProperty = field.getAnnotation(cn.idev.excel.annotation.ExcelProperty.class);
        if (excelProperty == null || excelProperty.value().length == 0) {
            return field.getName();
        }
        return excelProperty.value()[0];
    }
}
