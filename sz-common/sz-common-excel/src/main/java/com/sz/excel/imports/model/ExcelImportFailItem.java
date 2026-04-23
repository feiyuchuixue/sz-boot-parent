package com.sz.excel.imports.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 统一的导入失败项（值对象）。
 * <p>
 * 同时用于 Excel 解析失败 和 业务校验失败 两类来源。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用导入失败项")
public class ExcelImportFailItem {

    @Schema(description = "失败行号")
    private Integer rowNo;

    @Schema(description = "业务主识别值")
    private String bizKey;

    @Schema(description = "业务主识别值标签")
    private String bizKeyLabel;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "当前失败行原始快照")
    private Map<String, Object> rowData;
}
