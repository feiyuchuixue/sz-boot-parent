package com.sz.logger.event;

/**
 * 审计事件扩展出口。
 */
public interface AuditEventSink {

    boolean supports(AuditEvent event);

    void publish(AuditEvent event);

    default boolean async() {
        return true;
    }

    default boolean fallbackOnFailure() {
        return false;
    }

    default boolean diagnosticFallbackOnFailure() {
        return false;
    }
}
