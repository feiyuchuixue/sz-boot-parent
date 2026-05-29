package com.sz.audit.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.db.id.SzIdGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作审计诊断明细。
 */
@Data
@Table(value = "sys_operation_log_detail")
@Schema(description = "操作审计诊断明细")
public class SysOperationLogDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = SzIdGenerator.NAME)
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
