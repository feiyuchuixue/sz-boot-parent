package com.sz.excel.convert;

import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.data.WriteCellData;
import com.sz.core.common.entity.DictVO;
import com.sz.core.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Excel Long 类型转换器
 */
@Getter
@Setter
@Slf4j
public class CustomLongStringConvert extends AbstractExcelDictConvert<Long> {

    public CustomLongStringConvert(Map<String, List<DictVO>> dictmap) {
        super(dictmap);
    }

    @Override
    protected Long convertToJava(String value) {
        return Utils.getLongVal(value);
    }

    @Override
    protected WriteCellData<Long> createWriteCellData(Long object) {
        return formatLongNumber(object);
    }

    @Override
    protected Class<?> getJavaTypeClass() {
        return Long.class;
    }

    private static WriteCellData<Long> formatLongNumber(Long object) {
        String str = Utils.getStringVal(object);
        if (str.length() > 15) {
            return new WriteCellData<>(str);
        } else {
            WriteCellData<Long> cellData = new WriteCellData<>(String.valueOf(object));
            cellData.setType(CellDataTypeEnum.NUMBER);
            return cellData;
        }
    }

}
