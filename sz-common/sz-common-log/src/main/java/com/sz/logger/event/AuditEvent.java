package com.sz.logger.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计事件。
 */
@Data
@Builder(toBuilder = true)
public class AuditEvent {

    private String eventId;

    private AuditEventType eventType;

    private String traceId;

    private Long operationLogId;

    private String moduleName;

    private String operationName;

    private String operationType;

    private String status;

    private String userId;

    private String userName;

    private String permissionCode;

    private String requestMethod;

    private String requestUri;

    private String businessId;

    private String ipAddress;

    private Long costMs;

    private Boolean slow;

    private String responseCode;

    private String responseMessage;

    private String errorType;

    private String errorMessage;

    private String requestParams;

    private String responseBody;

    private String exceptionStack;

    private LocalDateTime occurredAt;
}
