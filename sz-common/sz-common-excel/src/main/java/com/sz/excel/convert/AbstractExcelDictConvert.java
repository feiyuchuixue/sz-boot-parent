package com.sz.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.sz.core.common.entity.DictVO;
import com.sz.core.util.StreamUtils;
import com.sz.core.util.Utils;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.utils.ExcelUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public abstract class AbstractExcelDictConvert<T> implements Converter<T> {

    protected Map<String, List<DictVO>> dictmap;

    protected AbstractExcelDictConvert(Map<String, List<DictVO>> dictmap) {
        this.dictmap = dictmap;
    }

    protected String getLabelFromDict(String dictType, String dictValue, DictFormat anno) {
        // 处理特殊情况，dictType为空且readConverterExp非空时直接返回转换结果
        if (isBlank(dictType) && isNoneBlank(anno.readConverterExp())) {
            return ExcelUtils.convertByExp(dictValue, anno.readConverterExp(), anno.separator());
        }

        // 获取字典列表
        List<DictVO> dictLists = dictmap.get(dictType);
        if (dictLists == null || dictLists.isEmpty()) {
            return "";
        }

        // 根据是否使用Alias构造字典映射
        Map<String, String> map = StreamUtils.toMap(dictLists, vo -> anno.useAlias() && vo.getAlias() != null ? vo.getAlias() : vo.getId(),
                vo -> vo.getCodeName() != null ? vo.getCodeName() : "");

        // 返回对应的标签，找不到时返回空字符串
        return map.getOrDefault(dictValue, "");
    }

    protected T getValueFromExcelData(ReadCellData<?> cellData, DictFormat anno, String dictType) {
        String dictLabel = cellData.getStringValue();
        String value;
        if (isBlank(dictType) && isNoneBlank(anno.readConverterExp())) {
            value = ExcelUtils.reverseByExp(dictLabel, anno.readConverterExp(), anno.separator());
        } else {
            List<DictVO> dictLists = dictmap.get(dictType);
            Map<String, String> map;
            if (anno.useAlias()) {
                map = StreamUtils.toMap(dictLists, DictVO::getCodeName, vo -> vo.getAlias() == null ? "" : vo.getAlias());
            } else {
                map = StreamUtils.toMap(dictLists, DictVO::getCodeName, DictVO::getId);
            }
            value = map.getOrDefault(dictLabel, "");
        }
        return convertToJava(value);
    }

    protected abstract T convertToJava(String value);

    protected abstract WriteCellData<T> createWriteCellData(T object);

    @Override
    public Class<?> supportJavaTypeKey() {
        return getJavaTypeClass();
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public T convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null) {
            return convertToJava(cellData.getStringValue());
        }
        Field field = contentProperty.getField();
        DictFormat anno = field.getAnnotation(DictFormat.class);
        if (anno == null) {
            return convertToJava(cellData.getStringValue());
        }
        String dictType = anno.dictType();
        return getValueFromExcelData(cellData, anno, dictType);
    }

    @Override
    public WriteCellData<T> convertToExcelData(T object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null) {
            return createWriteCellData(object);
        }
        Field field = contentProperty.getField();
        DictFormat anno = field.getAnnotation(DictFormat.class);
        if (anno == null) {
            return createWriteCellData(object);
        }
        String dictType = anno.dictType();
        String dictValue = Utils.getStringVal(object);
        String label = getLabelFromDict(dictType, dictValue, anno);
        return new WriteCellData<>(label);
    }

    protected abstract Class<?> getJavaTypeClass();
}