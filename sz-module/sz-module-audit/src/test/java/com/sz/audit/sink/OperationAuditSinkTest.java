package com.sz.audit.sink;

import com.sz.audit.service.impl.SysOperationLogServiceImpl;
import com.sz.logger.AuditProperties;
import com.sz.logger.event.AuditEvent;
import com.sz.logger.event.AuditEventType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationAuditSinkTest {

    @Test
    void persistenceSinkSupportsOperationEventsAndUsesConfiguredAsyncMode() {
        FakeService service = new FakeService();
        AuditProperties properties = new AuditProperties();
        properties.setWriteMode(AuditProperties.WriteMode.ASYNC);
        OperationAuditPersistenceSink sink = new OperationAuditPersistenceSink(service, properties);
        AuditEvent event = AuditEvent.builder().eventType(AuditEventType.OPERATION_SUCCESS).build();

        sink.publish(event);

        assertThat(sink.supports(event)).isTrue();
        assertThat(sink.supports(AuditEvent.builder().eventType(AuditEventType.AUDIT_SAVE_FAILED).build())).isFalse();
        assertThat(sink.async()).isTrue();
        assertThat(sink.fallbackOnFailure()).isTrue();
        assertThat(service.saveMainLogCount).isEqualTo(1);
    }

    @Test
    void diagnosticSinkDelegatesDetailSaveAndUsesDiagnosticFallback() {
        FakeService service = new FakeService();
        OperationDiagnosticSink sink = new OperationDiagnosticSink(service, new AuditProperties());
        AuditEvent event = AuditEvent.builder().eventType(AuditEventType.OPERATION_FAIL).build();

        sink.publish(event);

        assertThat(sink.supports(event)).isTrue();
        assertThat(sink.supports(AuditEvent.builder().eventType(AuditEventType.AUDIT_SAVE_FAILED).build())).isFalse();
        assertThat(sink.async()).isFalse();
        assertThat(sink.diagnosticFallbackOnFailure()).isTrue();
        assertThat(service.saveDetailCount).isEqualTo(1);
    }

    private static class FakeService extends SysOperationLogServiceImpl {

        private int saveMainLogCount;

        private int saveDetailCount;

        FakeService() {
            super(null, new AuditProperties());
        }

        @Override
        public Long saveMainLog(AuditEvent event) {
            saveMainLogCount++;
            return 1L;
        }

        @Override
        public void saveDiagnosticDetail(AuditEvent event) {
            saveDetailCount++;
        }
    }
}
