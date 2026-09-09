package com.sz.logger;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuditPropertiesTest {

    @Test
    void offModeDisablesSqlAccessOperationAndDiagnostic() {
        AuditProperties properties = new AuditProperties();
        properties.setMode(AuditProperties.AuditMode.OFF);

        assertThat(properties.resolveSql().getMode()).isEqualTo(AuditProperties.SqlMode.OFF);
        assertThat(properties.resolveAccess().getMode()).isEqualTo(AuditProperties.AccessMode.OFF);
        assertThat(properties.resolveOperation().isEnabled()).isFalse();
        assertThat(properties.resolveDiagnostic().isEnabled()).isFalse();
    }

    @Test
    void standardModeUsesSlowAccessAndCopiesTopLevelOperationSwitches() {
        AuditProperties properties = new AuditProperties();
        properties.setWriteMode(AuditProperties.WriteMode.ASYNC);
        properties.setMethods(new LinkedHashSet<>(Set.of("GET")));
        properties.setSlowThresholdMs(1500L);
        properties.setRecordParams(false);
        properties.setRecordResponseBody(true);

        AuditProperties.Access access = properties.resolveAccess();
        AuditProperties.Operation operation = properties.resolveOperation();

        assertThat(access.getMode()).isEqualTo(AuditProperties.AccessMode.SLOW);
        assertThat(operation.getWriteMode()).isEqualTo(AuditProperties.WriteMode.ASYNC);
        assertThat(operation.containsMethod("get")).isTrue();
        assertThat(operation.getSlowThresholdMs()).isEqualTo(1500L);
        assertThat(operation.isParamsEnabled()).isFalse();
        assertThat(operation.isResponseBodyEnabled()).isTrue();
    }

    @Test
    void resolveMethodsDoesNotFallbackWhenTopLevelMethodsAreEmpty() {
        AuditProperties properties = new AuditProperties();
        properties.setMethods(new LinkedHashSet<>());

        AuditProperties.Operation operation = properties.resolveOperation();

        assertThat(operation.containsMethod("POST")).isFalse();
        assertThat(operation.containsMethod("PUT")).isFalse();
        assertThat(operation.containsMethod("DELETE")).isFalse();
    }

    @Test
    void resolveTraceAndEventUseTopLevelCompatibilitySwitches() {
        AuditProperties properties = new AuditProperties();
        properties.setTraceEnabled(false);
        properties.setTraceHeaderName("X-Request-Id");
        properties.setWriteMode(AuditProperties.WriteMode.SYNC);

        AuditProperties.Trace trace = properties.resolveTrace();
        AuditProperties.Event event = properties.resolveEvent();

        assertThat(trace.isEnabled()).isFalse();
        assertThat(trace.getHeaderName()).isEqualTo("X-Request-Id");
        assertThat(event.getPublishMode()).isEqualTo(AuditProperties.WriteMode.SYNC);
    }
}
