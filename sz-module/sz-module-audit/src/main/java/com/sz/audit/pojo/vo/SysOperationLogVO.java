package com.sz.audit.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作审计日志 VO。
 */
@Data
@Accessors(chain = true)
@Schema(description = "操作审计日志VO")
public class SysOperationLogVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "审计事件ID")
    private String eventId;

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

    @Schema(description = "权限码")
    private String permissionCode;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URI")
    private String requestUri;

    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "耗时，单位毫秒")
    private Long costMs;

    @Schema(description = "是否慢操作，T/F")
    private String slowFlag;

    @Schema(description = "状态：SUCCESS/FAIL")
    private String status;

    @Schema(description = "响应码")
    private String responseCode;

    @Schema(description = "响应消息")
    private String responseMessage;

    @Schema(description = "异常类型")
    private String errorType;

    @Schema(description = "异常消息")
    private String errorMessage;

    @Schema(description = "诊断明细")
    private List<SysOperationLogDetailVO> details = new ArrayList<>();
}
