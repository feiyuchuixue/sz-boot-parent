package com.sz.audit.sink;

import com.sz.audit.service.SysOperationLogService;
import com.sz.logger.event.AuditEvent;
import com.sz.logger.event.AuditEventSink;
import com.sz.logger.event.AuditEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 操作审计诊断明细异步入库 Sink。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@RequiredArgsConstructor
public class OperationDiagnosticSink implements AuditEventSink {

    private final SysOperationLogService sysOperationLogService;

    @Override
    public boolean supports(AuditEvent event) {
        return event.getEventType() == AuditEventType.OPERATION_SUCCESS || event.getEventType() == AuditEventType.OPERATION_FAIL;
    }

    @Override
    public void publish(AuditEvent event) {
        sysOperationLogService.saveDiagnosticDetail(event);
    }

    @Override
    public boolean diagnosticFallbackOnFailure() {
        return true;
    }
}
