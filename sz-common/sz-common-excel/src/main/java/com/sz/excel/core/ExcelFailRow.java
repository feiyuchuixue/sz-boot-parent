package com.sz.excel.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Excel 结构化失败行信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFailRow<T> {

    /**
     * Excel 行号（1-based）
     */
    private Integer rowNo;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 当前失败行原始数据快照
     */
    private T rowData;
}
