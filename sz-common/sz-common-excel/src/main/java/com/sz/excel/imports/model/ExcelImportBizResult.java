package com.sz.excel.imports.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务侧导入结果（单次 doImport 调用或多次分片聚合）。
 */
@Data
@AllArgsConstructor
public class ExcelImportBizResult {

    private int successCount;

    private List<ExcelImportFailItem> failItems;

    public static ExcelImportBizResult empty() {
        return new ExcelImportBizResult(0, new ArrayList<>());
    }

    public static ExcelImportBizResult of(int successCount, List<ExcelImportFailItem> failItems) {
        return new ExcelImportBizResult(successCount, failItems == null ? new ArrayList<>() : failItems);
    }

    public List<ExcelImportFailItem> safeFailItems() {
        return failItems == null ? new ArrayList<>() : failItems;
    }
}
