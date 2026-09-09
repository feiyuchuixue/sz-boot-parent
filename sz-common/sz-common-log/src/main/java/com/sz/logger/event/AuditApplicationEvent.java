package com.sz.logger.event;

import org.springframework.context.ApplicationEvent;

/**
 * Spring 应用内审计事件。
 */
public class AuditApplicationEvent extends ApplicationEvent {

    private final AuditEvent auditEvent;

    public AuditApplicationEvent(Object source, AuditEvent auditEvent) {
        super(source);
        this.auditEvent = auditEvent;
    }

    public AuditEvent getAuditEvent() {
        return auditEvent;
    }
}
