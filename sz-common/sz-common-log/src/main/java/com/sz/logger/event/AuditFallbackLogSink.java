package com.sz.logger.event;

import com.sz.core.util.JsonUtils;
import com.sz.logger.desensitize.LogDesensitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 操作审计主记录保存失败时的文件日志兜底。
 */
@Slf4j(topic = "audit-operation-fallback-log")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class AuditFallbackLogSink implements AuditEventSink {

    private final LogDesensitizer logDesensitizer;

    @Override
    public boolean supports(AuditEvent event) {
        return event.getEventType() == AuditEventType.AUDIT_SAVE_FAILED;
    }

    @Override
    public void publish(AuditEvent event) {
        log.error("audit.operation fallback {}", logDesensitizer.desensitize(JsonUtils.toJsonString(event)));
    }

    @Override
    public boolean async() {
        return false;
    }
}
