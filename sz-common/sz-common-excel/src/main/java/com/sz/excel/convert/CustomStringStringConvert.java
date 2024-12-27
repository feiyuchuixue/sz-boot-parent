package com.sz.excel.convert;

import cn.idev.excel.metadata.data.WriteCellData;
import com.sz.core.common.entity.DictVO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Excel String 类型转换器
 */
@Getter
@Setter
@Slf4j
public class CustomStringStringConvert extends AbstractExcelDictConvert<String> {

    public CustomStringStringConvert(Map<String, List<DictVO>> dictmap) {
        super(dictmap);
    }

    @Override
    protected String convertToJava(String value) {
        return value;
    }

    @Override
    protected WriteCellData<String> createWriteCellData(String object) {
        return new WriteCellData<>(object);
    }

    @Override
    protected Class<?> getJavaTypeClass() {
        return String.class;
    }
}
