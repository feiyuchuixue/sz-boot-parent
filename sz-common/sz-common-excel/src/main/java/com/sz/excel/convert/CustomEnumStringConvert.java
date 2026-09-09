package com.sz.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.sz.core.util.Utils;
import com.sz.excel.support.ExcelEnumRuleResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 通用枚举 Excel String 转换器。
 * <p>
 * 该转换器不依赖枚举属性顺序，而是完全基于字段上的 {@code @ExcelEnumFormat} 所声明的规则进行导入导出转换。
 * </p>
 */
public class CustomEnumStringConvert implements Converter<Object> {

    private final Class<? extends Enum<?>> enumClass;

    public CustomEnumStringConvert(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return enumClass;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getField() == null) {
            return null;
        }
        Field field = contentProperty.getField();
        ExcelEnumRuleResolver.EnumRule rule = ExcelEnumRuleResolver.resolve(field);
        if (rule == null) {
            return null;
        }
        String cellValue = cellData == null ? null : cellData.getStringValue();
        return resolveEnum(cellValue, rule);
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (object == null) {
            return new WriteCellData<>("");
        }
        if (contentProperty == null || contentProperty.getField() == null) {
            return new WriteCellData<>(object.toString());
        }
        Field field = contentProperty.getField();
        ExcelEnumRuleResolver.EnumRule rule = ExcelEnumRuleResolver.resolve(field);
        if (rule == null) {
            return new WriteCellData<>(object.toString());
        }
        Object value = getFieldValue(object, rule.writeField());
        return new WriteCellData<>(value == null ? "" : String.valueOf(value));
    }

    private Object resolveEnum(String cellValue, ExcelEnumRuleResolver.EnumRule rule) {
        String target = Utils.getStringVal(cellValue);
        if (target == null || target.isBlank()) {
            return null;
        }
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            Object fieldValue = getFieldValue(enumConstant, rule.readField());
            if (match(fieldValue, target, rule.ignoreCase())) {
                return enumConstant;
            }
        }
        if (rule.fallbackToName()) {
            for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
                if (match(enumConstant.name(), target, rule.ignoreCase())) {
                    return enumConstant;
                }
            }
        }
        throw new IllegalArgumentException("不支持的枚举值: " + cellValue + ", enum=" + enumClass.getSimpleName());
    }

    private boolean match(Object fieldValue, String target, boolean ignoreCase) {
        if (fieldValue == null) {
            return false;
        }
        String source = String.valueOf(fieldValue);
        return ignoreCase ? source.equalsIgnoreCase(target) : source.equals(target);
    }

    private Object getFieldValue(Object enumObject, String fieldName) {
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method getter = enumObject.getClass().getMethod(getterName);
            return getter.invoke(enumObject);
        } catch (Exception ignored) {
        }
        try {
            Field declaredField = Arrays.stream(enumObject.getClass().getDeclaredFields()).filter(field -> field.getName().equals(fieldName)).findFirst()
                    .orElseThrow();
            declaredField.setAccessible(true);
            return declaredField.get(enumObject);
        } catch (Exception ex) {
            throw new IllegalStateException("无法读取枚举属性: " + enumObject.getClass().getSimpleName() + "." + fieldName, ex);
        }
    }
}
