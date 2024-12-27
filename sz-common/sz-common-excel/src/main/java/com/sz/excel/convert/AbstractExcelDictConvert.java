package com.sz.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.sz.core.common.entity.DictVO;
import com.sz.core.util.StreamUtils;
import com.sz.core.util.StringUtils;
import com.sz.core.util.Utils;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.utils.ExcelUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractExcelDictConvert<T> implements Converter<T> {

    protected Map<String, List<DictVO>> dictmap;

    public AbstractExcelDictConvert(Map<String, List<DictVO>> dictmap) {
        this.dictmap = dictmap;
    }

    protected String getLabelFromDict(String dictType, String dictValue, DictFormat anno) {
        String label = "";
        if (StringUtils.isBlank(dictType) && StringUtils.isNoneBlank(anno.readConverterExp())) {
            // Use readConverterExp to build dictionary template
            label = ExcelUtils.convertByExp(dictValue, anno.readConverterExp(), anno.separator());
        } else {
            Map<String, String> map = new HashMap<>();
            List<DictVO> dictLists = dictmap.get(dictType);
            if (anno.useAlias()) {
                map = StreamUtils.toMap(dictLists, vo -> vo.getAlias() == null ? "" : vo.getAlias(), vo -> vo.getCodeName() != null ? vo.getCodeName() : ""); // {"1000003":"禁言","1000002":"禁用","1000001":"正常"}
            } else {
                map = StreamUtils.toMap(dictLists, DictVO::getId, vo -> vo.getCodeName() != null ? vo.getCodeName() : ""); // {"1000003":"禁言","1000002":"禁用","1000001":"正常"}
            }
            label = map.getOrDefault(dictValue, "");
        }
        return label;
    }

    protected T getValueFromExcelData(ReadCellData<?> cellData, DictFormat anno, String dictType) {
        String dictLabel = cellData.getStringValue();
        String value = "";
        if (StringUtils.isBlank(dictType) && StringUtils.isNoneBlank(anno.readConverterExp())) {
            value = ExcelUtils.reverseByExp(dictLabel, anno.readConverterExp(), anno.separator());
        } else {
            List<DictVO> dictLists = dictmap.get(dictType);
            Map<String, String> map = new HashMap<>();
            if (anno.useAlias()) {
                map = StreamUtils.toMap(dictLists, DictVO::getCodeName, vo -> vo.getAlias() == null ? "" : vo.getAlias()); // {"禁言":"1000003","禁用":"1000002","正常":"1000001"}
            } else {
                map = StreamUtils.toMap(dictLists, DictVO::getCodeName, DictVO::getId); // {"禁言":"1000003","禁用":"1000002","正常":"1000001"}
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