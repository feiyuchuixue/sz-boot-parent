package com.sz.core.datascope;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataScopeSessionTest {

    @AfterEach
    void tearDown() {
        SimpleDataScopeHelper.clearDataScope();
    }

    @Test
    void dataScopeSessionStartsAndClearsContext() {
        assertThat(SimpleDataScopeHelper.isDataScope()).isFalse();

        try (var ignored = new DataScopeSession(SampleEntity.class)) {
            assertThat(SimpleDataScopeHelper.isDataScope()).isTrue();
            assertThat(SimpleDataScopeHelper.get()).isEqualTo(SampleEntity.class);
        }

        assertThat(SimpleDataScopeHelper.isDataScope()).isFalse();
        assertThat(SimpleDataScopeHelper.get()).isNull();
    }

    @Test
    void simpleHelperCanStartAndClearManually() {
        SimpleDataScopeHelper.start(SampleEntity.class);

        assertThat(SimpleDataScopeHelper.isDataScope()).isTrue();
        assertThat(SimpleDataScopeHelper.get()).isEqualTo(SampleEntity.class);

        SimpleDataScopeHelper.clearDataScope();

        assertThat(SimpleDataScopeHelper.isDataScope()).isFalse();
    }

    private static class SampleEntity {
    }
}
