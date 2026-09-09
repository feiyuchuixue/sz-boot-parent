package com.sz.excel.imports.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 解析结果。
 * <p>
 * 成功行交由业务继续处理；失败项直接并入最终失败记录。
 */
@Data
@AllArgsConstructor
public class ExcelImportParseResult<T> {

    private List<T> successRows;

    private List<ExcelImportFailItem> failItems;

    public int totalCount() {
        int s = successRows == null ? 0 : successRows.size();
        int f = failItems == null ? 0 : failItems.size();
        return s + f;
    }

    public boolean isEmpty() {
        return totalCount() == 0;
    }

    public List<T> safeSuccessRows() {
        return successRows == null ? new ArrayList<>() : successRows;
    }

    public List<ExcelImportFailItem> safeFailItems() {
        return failItems == null ? new ArrayList<>() : failItems;
    }
}
