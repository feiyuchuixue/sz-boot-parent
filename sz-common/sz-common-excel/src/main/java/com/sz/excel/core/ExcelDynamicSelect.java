package com.sz.excel.core;

import java.util.List;

/**
 * @author sz
 * @since 2023/12/28 13:22
 */
public interface ExcelDynamicSelect {

    /**
     * 获取动态生成的下拉框可选数据
     *
     * @return 动态生成的下拉框可选数据
     */
    List<String> getSource();
}
