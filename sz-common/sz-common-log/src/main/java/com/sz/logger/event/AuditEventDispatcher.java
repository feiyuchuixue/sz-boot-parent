package com.sz.logger.event;

import com.sz.core.util.JsonUtils;
import com.sz.logger.AuditProperties;
import com.sz.logger.desensitize.LogDesensitizer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 审计事件分发器。
 */
@Slf4j(topic = "diagnostic-exception-log")
@Component
@RequiredArgsConstructor
public class AuditEventDispatcher {

    private static final Logger PERFORMANCE_LOG = LoggerFactory.getLogger("diagnostic-performance-log");

    private final List<AuditEventSink> sinks;

    private final AuditProperties auditProperties;

    private final LogDesensitizer logDesensitizer;

    private ExecutorService executor;

    @PostConstruct
    public void init() {
        AuditProperties.Diagnostic diagnostic = auditProperties.getDiagnostic() == null ? new AuditProperties.Diagnostic() : auditProperties.getDiagnostic();
        int coreSize = Math.max(1, diagnostic.getAsyncCoreSize());
        int maxSize = Math.max(coreSize, diagnostic.getAsyncMaxSize());
        int queueCapacity = Math.max(1, diagnostic.getAsyncQueueCapacity());
        executor = new ThreadPoolExecutor(coreSize, maxSize, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueCapacity), new AuditThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void dispatch(AuditEvent event) {
        if (event == null) {
            return;
        }
        sinks.stream().filter(sink -> !sink.async()).forEach(sink -> publishSafely(sink, event));
        publishAsync(event);
    }

    private void publishAsync(AuditEvent event) {
        List<AuditEventSink> asyncSinks = sinks.stream().filter(AuditEventSink::async).filter(sink -> sink.supports(event)).toList();
        if (asyncSinks.isEmpty()) {
            return;
        }
        try {
            executor.execute(() -> asyncSinks.forEach(sink -> publishSafely(sink, event)));
        } catch (RejectedExecutionException e) {
            log.warn("audit.event async queue full eventId={} type={} sinks={}", event.getEventId(), event.getEventType(),
                    asyncSinks.stream().map(sink -> sink.getClass().getName()).toList());
            asyncSinks.forEach(sink -> handlePublishRejected(sink, event, e));
        }
    }

    private void publishSafely(AuditEventSink sink, AuditEvent event) {
        if (!sink.supports(event)) {
            return;
        }
        try {
            sink.publish(event);
        } catch (Exception e) {
            log.error("audit.event sink failed sink={} eventId={} type={} error={}", sink.getClass().getName(), event.getEventId(), event.getEventType(),
                    e.getMessage(), e);
            if (sink.diagnosticFallbackOnFailure()) {
                writeDiagnosticFallback(event, e);
            }
            if (sink.fallbackOnFailure() && event.getEventType() != AuditEventType.AUDIT_SAVE_FAILED) {
                dispatchFallback(event, e);
            }
        }
    }

    private void handlePublishRejected(AuditEventSink sink, AuditEvent event, RejectedExecutionException cause) {
        if (sink.diagnosticFallbackOnFailure()) {
            writeDiagnosticFallback(event, cause);
        }
        if (sink.fallbackOnFailure() && event.getEventType() != AuditEventType.AUDIT_SAVE_FAILED) {
            dispatchFallback(event, cause);
        }
    }

    private void writeDiagnosticFallback(AuditEvent event, Exception cause) {
        AuditProperties.Diagnostic diagnostic = auditProperties.getDiagnostic() == null ? new AuditProperties.Diagnostic() : auditProperties.getDiagnostic();
        AuditEvent fallbackEvent = event.toBuilder().errorType(cause.getClass().getName()).errorMessage(cause.getMessage()).build();
        String payload = logDesensitizer.desensitize(JsonUtils.toJsonString(fallbackEvent));
        if (Boolean.TRUE.equals(event.getSlow()) && diagnostic.isPerformanceEnabled()) {
            PERFORMANCE_LOG.warn("diagnostic.performance fallback {}", payload);
        }
        if ("FAIL".equals(event.getStatus()) && diagnostic.isExceptionEnabled()) {
            log.error("diagnostic.exception fallback {}", payload);
        }
    }

    private void dispatchFallback(AuditEvent source, Exception cause) {
        AuditEvent fallback = source.toBuilder().eventType(AuditEventType.AUDIT_SAVE_FAILED).errorType(cause.getClass().getName())
                .errorMessage(cause.getMessage()).build();
        sinks.stream().filter(sink -> !sink.async()).filter(sink -> sink.supports(fallback)).forEach(sink -> {
            try {
                sink.publish(fallback);
            } catch (Exception e) {
                log.error("audit.event fallback failed sink={} eventId={} error={}", sink.getClass().getName(), source.getEventId(), e.getMessage(), e);
            }
        });
    }

    private static class AuditThreadFactory implements ThreadFactory {

        private final AtomicInteger index = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "audit-event-" + index.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
