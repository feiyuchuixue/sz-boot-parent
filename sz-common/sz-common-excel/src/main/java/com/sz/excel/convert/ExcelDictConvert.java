package com.sz.excel.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.sz.core.common.entity.UserOptionVO;
import com.sz.core.common.service.DictService;
import com.sz.core.common.service.UserOptionService;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import com.sz.core.util.Utils;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典格式化转换处理
 */
@Slf4j
public class ExcelDictConvert implements Converter<Object> {

    private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<>();

    static {
        DEFAULT_VALUES.put(Integer.class, 0);
        DEFAULT_VALUES.put(Long.class, 0L);
        DEFAULT_VALUES.put(Double.class, 0.0d);
        DEFAULT_VALUES.put(Float.class, 0.0f);
    }

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    /**
     * 从excel导入，字典类型处理
     *
     * @param cellData            Excel cell data.NotNull.
     * @param contentProperty     Content property.Nullable.
     * @param globalConfiguration Global configuration.NotNull.
     * @return
     */
    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        Field field = contentProperty.getField();
        DictFormat anno = field.getAnnotation(DictFormat.class);
        String dictType = anno.dictType();
        String dictLabel = cellData.getStringValue();
        String value = "";
        if (StringUtils.isBlank(dictType) && StringUtils.isNoneBlank(anno.readConverterExp())) { // 使用readConverterExp来构建字典模板
            value = ExcelUtils.reverseByExp(dictLabel, anno.readConverterExp(), anno.separator());
        } else { // 使用dictType 获取字典类型
            DictService dictService = SpringApplicationContextUtils.getBean(DictService.class);
            value = dictService.getDictValue(dictType, dictLabel, anno.separator());
        }
        return convert(field, value);
    }

    /**
     * 导出到excel，字典类型处理
     *
     * @param object              Java Data.NotNull.
     * @param contentProperty     Content property.Nullable.
     * @param globalConfiguration Global configuration.NotNull.
     * @return
     */
    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        Field field = contentProperty.getField();
        DictFormat anno = field.getAnnotation(DictFormat.class);
        String dictType = anno.dictType();
        String dictValue = Utils.getStringVal(object);
        String label = "";
        if (StringUtils.isBlank(dictType) && StringUtils.isNoneBlank(anno.readConverterExp())) { // 使用readConverterExp来构建字典模板
            label = ExcelUtils.convertByExp(dictValue, anno.readConverterExp(), anno.separator());
        } else if (anno.isUser()) {
            UserOptionService optionService = SpringApplicationContextUtils.getBean(UserOptionService.class);
            List<UserOptionVO> userOptions = optionService.getUserOptions();
            Map<Long, UserOptionVO> optionsMap = userOptions.stream().collect(Collectors.toMap(UserOptionVO::getId, o -> o));
            if (optionsMap.containsKey(Utils.getLongVal(dictValue))) {
                label = optionsMap.get(Utils.getLongVal(dictValue)).getNickname();
            }
        } else { // 使用dictType 获取字典类型
            DictService dictService = SpringApplicationContextUtils.getBean(DictService.class);
            label = dictService.getDictLabel(dictType, dictValue, anno.separator());
        }
        return new WriteCellData<>(label);
    }

    public static boolean hasAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

    public static Object convert(Field field, String value) {
        Class<?> fieldType = field.getType();

        if (String.class.equals(fieldType)) {
            return value;
        } else {
            if (StringUtils.isBlank(value)) {
                return DEFAULT_VALUES.get(fieldType);
            } else {
                return convertToType(value, fieldType);
            }
        }
    }

    private static Object convertToType(String value, Class<?> targetType) {
        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            return Integer.parseInt(value);
        } else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            return Long.parseLong(value);
        } else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
            return Double.parseDouble(value);
        } else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
            return Float.parseFloat(value);
        } // Add more cases as needed...

        throw new IllegalArgumentException("Unsupported field type: " + targetType);
    }


}
