package com.sz.excel.support;

/**
 * Excel 导出字段级错误信息。
 */
public record ExcelExportFieldError(Class<?> rowClass, int rowIndex, String fieldName, String headerName, String valueType, String message) {
}
