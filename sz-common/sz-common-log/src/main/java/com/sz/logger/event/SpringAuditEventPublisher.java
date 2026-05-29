package com.sz.logger.event;

import com.sz.logger.AuditProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 将审计事件转为 Spring 应用事件，方便业务侧按需监听扩展。
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class SpringAuditEventPublisher implements AuditEventSink {

    private final ApplicationEventPublisher publisher;

    private final AuditProperties auditProperties;

    @Override
    public boolean supports(AuditEvent event) {
        AuditProperties.Event eventProperties = resolveEventProperties();
        return eventProperties.isEnabled() && event.getEventType() != AuditEventType.AUDIT_SAVE_FAILED;
    }

    @Override
    public void publish(AuditEvent event) {
        AuditProperties.Event eventProperties = resolveEventProperties();
        AuditEvent publishEvent = eventProperties.isIncludeDetail() ? event : event.toBuilder().requestParams("").responseBody("").exceptionStack("").build();
        publisher.publishEvent(new AuditApplicationEvent(this, publishEvent));
    }

    @Override
    public boolean async() {
        return resolveEventProperties().getPublishMode() == AuditProperties.WriteMode.ASYNC;
    }

    private AuditProperties.Event resolveEventProperties() {
        return auditProperties.getEvent() == null ? new AuditProperties.Event() : auditProperties.getEvent();
    }
}
