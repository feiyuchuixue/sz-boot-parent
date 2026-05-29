package com.sz.audit.pojo.dto;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作审计日志查询 DTO。
 */
@Data
@Accessors(chain = true)
@Schema(description = "操作审计日志查询DTO")
public class SysOperationLogListDTO extends PageQuery {

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "操作名称")
    private String operationName;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "状态：SUCCESS/FAIL")
    private String status;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URI")
    private String requestUri;

    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "最小耗时，单位毫秒")
    private Long minCostMs;

    @Schema(description = "是否慢操作，T/F")
    private String slowFlag;

    @Schema(description = "操作时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTimeStart;

    @Schema(description = "操作时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTimeEnd;
}
