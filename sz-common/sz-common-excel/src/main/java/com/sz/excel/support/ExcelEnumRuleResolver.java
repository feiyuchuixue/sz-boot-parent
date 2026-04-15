package com.sz.excel.support;

import com.sz.core.common.enums.YesNoEnum;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.annotation.ExcelEnumFormat;

import java.lang.reflect.Field;

/**
 * Excel 枚举映射规则解析器。
 */
public final class ExcelEnumRuleResolver {

    private ExcelEnumRuleResolver() {
    }

    public static EnumRule resolve(Field field) {
        if (field == null || field.isAnnotationPresent(DictFormat.class)) {
            return null;
        }
        if (!field.getType().isEnum()) {
            return null;
        }

        ExcelEnumFormat format = field.getAnnotation(ExcelEnumFormat.class);
        if (format == null) {
            return null;
        }

        return switch (format.preset()) {
            case YES_NO -> buildYesNoRule(field, format);
            case CUSTOM -> buildCustomRule(field, format);
        };
    }

    private static EnumRule buildYesNoRule(Field field, ExcelEnumFormat format) {
        if (!YesNoEnum.class.equals(field.getType())) {
            throw new IllegalStateException("preset=YES_NO 仅允许用于 YesNoEnum, field=" + field.getName());
        }
        if (!format.writeField().isBlank() || !format.readField().isBlank()) {
            throw new IllegalStateException("preset=YES_NO 不需要额外指定 writeField/readField, field=" + field.getName());
        }
        return new EnumRule("desc", "desc", format.ignoreCase(), format.fallbackToName());
    }

    private static EnumRule buildCustomRule(Field field, ExcelEnumFormat format) {
        if (format.writeField().isBlank() || format.readField().isBlank()) {
            throw new IllegalStateException("preset=CUSTOM 必须同时指定 writeField/readField, field=" + field.getName());
        }
        return new EnumRule(format.writeField(), format.readField(), format.ignoreCase(), format.fallbackToName());
    }

    public record EnumRule(String writeField, String readField, boolean ignoreCase, boolean fallbackToName) {
    }
}
