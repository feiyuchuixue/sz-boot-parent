package com.sz.excel.imports.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对前端返回的导入结果（含失败明细列表）。
 */
@Data
@NoArgsConstructor
@Schema(description = "通用导入结果")
public class ExcelImportResultVO {

    @Schema(description = "导入批次ID")
    private String batchId;

    @Schema(description = "成功条数")
    private int success;

    @Schema(description = "失败条数")
    private int fail;

    @Schema(description = "失败明细列表")
    private List<ExcelImportFailItem> failDetails;

    public ExcelImportResultVO(String batchId, int success, int fail) {
        this.batchId = batchId;
        this.success = success;
        this.fail = fail;
    }

    public ExcelImportResultVO(String batchId, int success, int fail, List<ExcelImportFailItem> failDetails) {
        this.batchId = batchId;
        this.success = success;
        this.fail = fail;
        this.failDetails = failDetails;
    }
}
