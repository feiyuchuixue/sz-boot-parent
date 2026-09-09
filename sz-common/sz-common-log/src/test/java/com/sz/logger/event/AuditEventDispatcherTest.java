package com.sz.logger.event;

import com.sz.logger.AuditProperties;
import com.sz.logger.desensitize.LogDesensitizer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class AuditEventDispatcherTest {

    @Test
    void dispatchPublishesOnlySupportedSyncSinks() {
        RecordingSink supported = new RecordingSink(true, false);
        RecordingSink unsupported = new RecordingSink(false, false);
        AuditEventDispatcher dispatcher = new AuditEventDispatcher(List.of(supported, unsupported), new AuditProperties(), new LogDesensitizer());
        AuditEvent event = event(AuditEventType.OPERATION_SUCCESS);

        dispatcher.dispatch(event);

        assertThat(supported.publishCount).hasValue(1);
        assertThat(unsupported.publishCount).hasValue(0);
    }

    @Test
    void syncSinkFailureDispatchesAuditSaveFailedFallback() {
        RecordingSink failing = new RecordingSink(true, false);
        failing.failOnPublish = true;
        failing.fallbackOnFailure = true;
        failing.supportedType = AuditEventType.OPERATION_FAIL;
        RecordingSink fallback = new RecordingSink(false, false);
        fallback.supportedType = AuditEventType.AUDIT_SAVE_FAILED;
        AuditEventDispatcher dispatcher = new AuditEventDispatcher(List.of(failing, fallback), new AuditProperties(), new LogDesensitizer());

        dispatcher.dispatch(event(AuditEventType.OPERATION_FAIL));

        assertThat(failing.publishCount).hasValue(1);
        assertThat(fallback.publishCount).hasValue(1);
        assertThat(fallback.lastEvent.getEventType()).isEqualTo(AuditEventType.AUDIT_SAVE_FAILED);
        assertThat(fallback.lastEvent.getErrorType()).isEqualTo(IllegalStateException.class.getName());
    }

    @Test
    void nullEventIsIgnored() {
        RecordingSink sink = new RecordingSink(true, false);
        AuditEventDispatcher dispatcher = new AuditEventDispatcher(List.of(sink), new AuditProperties(), new LogDesensitizer());

        dispatcher.dispatch(null);

        assertThat(sink.publishCount).hasValue(0);
    }

    private static AuditEvent event(AuditEventType eventType) {
        return AuditEvent.builder().eventId("event-1").eventType(eventType).status("FAIL").slow(true).build();
    }

    private static class RecordingSink implements AuditEventSink {

        private final boolean supports;

        private final boolean async;

        private final AtomicInteger publishCount = new AtomicInteger();

        private AuditEventType supportedType;

        private AuditEvent lastEvent;

        private boolean failOnPublish;

        private boolean fallbackOnFailure;

        RecordingSink(boolean supports, boolean async) {
            this.supports = supports;
            this.async = async;
        }

        @Override
        public boolean supports(AuditEvent event) {
            if (supportedType != null) {
                return event.getEventType() == supportedType;
            }
            return supports;
        }

        @Override
        public void publish(AuditEvent event) {
            publishCount.incrementAndGet();
            lastEvent = event;
            if (failOnPublish) {
                throw new IllegalStateException("save failed");
            }
        }

        @Override
        public boolean async() {
            return async;
        }

        @Override
        public boolean fallbackOnFailure() {
            return fallbackOnFailure;
        }
    }
}
