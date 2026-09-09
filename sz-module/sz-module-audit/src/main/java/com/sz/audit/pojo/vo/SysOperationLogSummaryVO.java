package com.sz.audit.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 操作审计日志轻量汇总。
 */
@Data
@Accessors(chain = true)
@Schema(description = "操作审计日志轻量汇总")
public class SysOperationLogSummaryVO {

    @Schema(description = "总操作数")
    private Long totalCount;

    @Schema(description = "失败数")
    private Long failCount;

    @Schema(description = "慢操作数")
    private Long slowCount;

    @Schema(description = "异常数")
    private Long exceptionCount;

    @Schema(description = "成功率，百分比")
    private BigDecimal successRate;
}
