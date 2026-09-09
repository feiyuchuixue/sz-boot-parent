package com.sz.logger.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccessLogBuilderDefaultTest {

    @Test
    void builderShouldKeepTypeDefaults() {
        assertThat(AccessRequestLog.builder().build().getType()).isEqualTo("request");
        assertThat(AccessResponseLog.builder().build().getType()).isEqualTo("response");
    }
}
