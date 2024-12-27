package com.sz.excel.convert;

import cn.idev.excel.metadata.data.WriteCellData;
import com.sz.core.common.entity.DictVO;
import com.sz.core.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Excel Integer 类型转换器
 */
@Getter
@Setter
@Slf4j
public class CustomIntegerStringConvert extends AbstractExcelDictConvert<Integer> {

    public CustomIntegerStringConvert(Map<String, List<DictVO>> dictmap) {
        super(dictmap);
    }

    @Override
    protected Integer convertToJava(String value) {
        return Utils.getIntVal(value);
    }

    @Override
    protected WriteCellData<Integer> createWriteCellData(Integer object) {
        return new WriteCellData<>(String.valueOf(object));
    }

    @Override
    protected Class<?> getJavaTypeClass() {
        return Integer.class;
    }

}
