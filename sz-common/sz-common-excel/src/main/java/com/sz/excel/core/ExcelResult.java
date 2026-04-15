package com.sz.excel.core;

import java.util.List;

/**
 * excel返回对象
 *
 */
public interface ExcelResult<T> {

    /**
     * 对象列表
     */
    List<T> getList();

    /**
     * 错误列表
     */
    List<String> getErrorList();

    /**
     * 结构化失败行列表
     */
    List<ExcelFailRow<T>> getFailRowList();

    /**
     * 导入回执
     */
    String getAnalysis();
}
