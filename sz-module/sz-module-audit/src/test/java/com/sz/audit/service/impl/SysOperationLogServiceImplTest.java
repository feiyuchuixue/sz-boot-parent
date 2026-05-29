package com.sz.audit.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.sz.audit.mapper.SysOperationLogDetailMapper;
import com.sz.audit.mapper.SysOperationLogMapper;
import com.sz.audit.pojo.dto.SysOperationLogListDTO;
import com.sz.audit.pojo.po.SysOperationLog;
import com.sz.audit.pojo.po.SysOperationLogDetail;
import com.sz.logger.AuditProperties;
import com.sz.logger.event.AuditEvent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class SysOperationLogServiceImplTest {

    @Test
    void saveMainLogMapsAuditEventToMainLogAndBackfillsOperationLogId() {
        CapturingDetailMapper detailMapper = new CapturingDetailMapper();
        TestableService service = new TestableService(detailMapper.proxy(), new AuditProperties());
        LocalDateTime occurredAt = LocalDateTime.of(2026, 5, 28, 10, 0);
        AuditEvent event = AuditEvent.builder().eventId("event-1").traceId("trace-1").userId("1001").userName("alice").moduleName("系统管理")
                .operationName("新增用户").operationType("CREATE").permissionCode("sys.user.create_btn").requestMethod("POST").requestUri("/sys-user")
                .businessId("7").ipAddress("127.0.0.1").occurredAt(occurredAt).costMs(120L).slow(false).status("SUCCESS").responseCode("0000")
                .responseMessage("SUCCESS").errorType("").errorMessage("").build();

        Long id = service.saveMainLog(event);

        assertThat(id).isEqualTo(99L);
        assertThat(event.getOperationLogId()).isEqualTo(99L);
        assertThat(service.savedLog.getEventId()).isEqualTo("event-1");
        assertThat(service.savedLog.getUserId()).isEqualTo(1001L);
        assertThat(service.savedLog.getSlowFlag()).isEqualTo("F");
        assertThat(service.savedLog.getOperationTime()).isEqualTo(occurredAt);
        assertThat(service.savedLog.getRequestUri()).isEqualTo("/sys-user");
    }

    @Test
    void saveMainLogIgnoresNonNumericUserIdAndMarksSlowFlag() {
        TestableService service = new TestableService(new CapturingDetailMapper().proxy(), new AuditProperties());
        AuditEvent event = AuditEvent.builder().eventId("event-2").userId("anonymous").slow(true).status("FAIL").occurredAt(LocalDateTime.now()).build();

        service.saveMainLog(event);

        assertThat(service.savedLog.getUserId()).isNull();
        assertThat(service.savedLog.getSlowFlag()).isEqualTo("T");
    }

    @Test
    void saveDiagnosticDetailSkipsWhenMainLogIdIsMissingOrDiagnosticDisabled() {
        CapturingDetailMapper detailMapper = new CapturingDetailMapper();
        TestableService service = new TestableService(detailMapper.proxy(), new AuditProperties());

        service.saveDiagnosticDetail(AuditEvent.builder().eventId("event-1").status("FAIL").build());

        AuditProperties disabled = new AuditProperties();
        disabled.setEnabled(false);
        new TestableService(detailMapper.proxy(), disabled).saveDiagnosticDetail(AuditEvent.builder().operationLogId(1L).status("FAIL").build());

        assertThat(detailMapper.inserted.get()).isNull();
    }

    @Test
    void saveDiagnosticDetailUsesExceptionPerformanceAndDetailTypes() {
        CapturingDetailMapper detailMapper = new CapturingDetailMapper();
        TestableService service = new TestableService(detailMapper.proxy(), new AuditProperties());

        service.saveDiagnosticDetail(AuditEvent.builder().operationLogId(1L).eventId("fail").traceId("trace").status("FAIL").requestParams("{}").build());
        assertThat(detailMapper.inserted.get().getDetailType()).isEqualTo("EXCEPTION");

        service.saveDiagnosticDetail(AuditEvent.builder().operationLogId(1L).eventId("slow").status("SUCCESS").slow(true).responseBody("{}").build());
        assertThat(detailMapper.inserted.get().getDetailType()).isEqualTo("PERFORMANCE");

        service.saveDiagnosticDetail(AuditEvent.builder().operationLogId(1L).eventId("detail").status("SUCCESS").requestParams("{\"id\":1}").build());
        assertThat(detailMapper.inserted.get().getDetailType()).isEqualTo("DETAIL");
        assertThat(detailMapper.inserted.get().getOperationLogId()).isEqualTo(1L);
        assertThat(detailMapper.inserted.get().getCreateTime()).isNotNull();
    }

    @Test
    void summaryCountWrappersDoNotCarryOrderByForPostgresqlCompatibility() {
        TestableService service = new TestableService(new CapturingDetailMapper().proxy(), new AuditProperties());
        SysOperationLogListDTO dto = new SysOperationLogListDTO();
        dto.setOperationTimeStart(LocalDateTime.of(2026, 5, 29, 0, 0));
        dto.setOperationTimeEnd(LocalDateTime.of(2026, 5, 29, 23, 59, 59));

        service.summary(dto);

        assertThat(service.countWrappers).hasSize(3);
        assertThat(service.countWrappers).allSatisfy(wrapper -> {
            String sql = wrapper.toSQL();
            assertThat(sql).containsIgnoringCase("operation_time").containsIgnoringCase("between");
            assertThat(sql).doesNotContainIgnoringCase("order by");
        });
    }

    private static class TestableService extends SysOperationLogServiceImpl {

        private SysOperationLog savedLog;

        private final List<QueryWrapper> countWrappers = new ArrayList<>();

        TestableService(SysOperationLogDetailMapper detailMapper, AuditProperties auditProperties) {
            super(detailMapper, auditProperties);
        }

        @Override
        public boolean save(SysOperationLog entity) {
            entity.setId(99L);
            savedLog = entity;
            return true;
        }

        @Override
        public long count(QueryWrapper queryWrapper) {
            countWrappers.add(queryWrapper);
            return 0;
        }
    }

    private static class CapturingDetailMapper {

        private final AtomicReference<SysOperationLogDetail> inserted = new AtomicReference<>();

        SysOperationLogDetailMapper proxy() {
            return (SysOperationLogDetailMapper) Proxy.newProxyInstance(SysOperationLogServiceImplTest.class.getClassLoader(),
                    new Class<?>[]{SysOperationLogDetailMapper.class}, (proxy, method, args) -> {
                        if ("insert".equals(method.getName())) {
                            inserted.set((SysOperationLogDetail) args[0]);
                            return 1;
                        }
                        return defaultValue(method.getReturnType());
                    });
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (boolean.class.equals(type)) {
            return false;
        }
        if (char.class.equals(type)) {
            return '\0';
        }
        return 0;
    }
}
