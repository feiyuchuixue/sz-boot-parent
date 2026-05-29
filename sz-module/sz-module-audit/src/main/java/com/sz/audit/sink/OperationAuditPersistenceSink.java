package com.sz.audit.sink;

import com.sz.audit.service.SysOperationLogService;
import com.sz.logger.AuditProperties;
import com.sz.logger.event.AuditEvent;
import com.sz.logger.event.AuditEventSink;
import com.sz.logger.event.AuditEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 操作审计主记录入库 Sink。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class OperationAuditPersistenceSink implements AuditEventSink {

    private final SysOperationLogService sysOperationLogService;

    private final AuditProperties auditProperties;

    @Override
    public boolean supports(AuditEvent event) {
        return event.getEventType() == AuditEventType.OPERATION_SUCCESS || event.getEventType() == AuditEventType.OPERATION_FAIL;
    }

    @Override
    public void publish(AuditEvent event) {
        sysOperationLogService.saveMainLog(event);
    }

    @Override
    public boolean async() {
        AuditProperties.Operation operation = auditProperties.getOperation() == null ? new AuditProperties.Operation() : auditProperties.getOperation();
        return operation.getWriteMode() == AuditProperties.WriteMode.ASYNC;
    }

    @Override
    public boolean fallbackOnFailure() {
        return true;
    }
}
