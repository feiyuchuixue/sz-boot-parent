package com.sz.excel.core;

import java.util.List;

/**
 * @ClassName ExcelDynamicSelect
 * @Author sz
 * @Date 2023/12/28 13:22
 * @Version 1.0
 */
public interface ExcelDynamicSelect {

    /**
     * 获取动态生成的下拉框可选数据
     *
     * @return 动态生成的下拉框可选数据
     */
    List<String> getSource();
}
