package com.sz.admin.system.script;

import java.lang.reflect.Method;
import java.time.temporal.TemporalAccessor;

/**
 * Formats Java values for generated SQL scripts.
 */
public class AdminSqlValueFormatter {

    public String value(Object value) {
        if (value == null) {
            return "NULL";
        }
        Object normalized = normalize(value);
        if (normalized instanceof Number) {
            return normalized.toString();
        }
        return "'" + escape(normalized.toString()) + "'";
    }

    public String number(Object value) {
        if (value == null) {
            return "NULL";
        }
        return normalize(value).toString();
    }

    private static Object normalize(Object value) {
        if (value instanceof TemporalAccessor) {
            return value.toString();
        }
        Object enumCode = enumCode(value);
        return enumCode != null ? enumCode : value;
    }

    private static Object enumCode(Object value) {
        try {
            Method method = value.getClass().getMethod("getCode");
            return method.invoke(value);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static String escape(String value) {
        return value.replace("'", "''");
    }
}
