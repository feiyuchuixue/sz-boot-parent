package com.sz.audit.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 操作审计诊断明细 VO。
 */
@Data
@Accessors(chain = true)
@Schema(description = "操作审计诊断明细VO")
public class SysOperationLogDetailVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "操作审计主记录ID")
    private Long operationLogId;

    @Schema(description = "审计事件ID")
    private String eventId;

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "明细类型")
    private String detailType;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应内容")
    private String responseBody;

    @Schema(description = "异常堆栈")
    private String exceptionStack;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
