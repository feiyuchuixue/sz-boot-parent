package com.sz.resource.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectKeyUtilsTest {

    @Test
    void toPhysicalKeyRemovesBucketPrefixWhenPresent() {
        assertThat(ObjectKeyUtils.toPhysicalKey("client-logos/20260526/logo.png", "client-logos")).isEqualTo("20260526/logo.png");
        assertThat(ObjectKeyUtils.toPhysicalKey("client-logos/20260526/logo.png", "client-logos/")).isEqualTo("20260526/logo.png");
    }

    @Test
    void toPhysicalKeyKeepsLegacyValuesWhenPrefixMissing() {
        assertThat(ObjectKeyUtils.toPhysicalKey("20260526/logo.png", "client-logos")).isEqualTo("20260526/logo.png");
        assertThat(ObjectKeyUtils.toPhysicalKey(null, "client-logos")).isNull();
        assertThat(ObjectKeyUtils.toPhysicalKey("client-logos/logo.png", null)).isEqualTo("client-logos/logo.png");
        assertThat(ObjectKeyUtils.toPhysicalKey("client-logos/logo.png", " ")).isEqualTo("client-logos/logo.png");
    }

    @Test
    void toPhysicalKeyOnlyRemovesExactBucketSegment() {
        assertThat(ObjectKeyUtils.toPhysicalKey("client-logos-v2/20260526/logo.png", "client-logos")).isEqualTo("client-logos-v2/20260526/logo.png");
        assertThat(ObjectKeyUtils.toPhysicalKey("client-logos", "client-logos")).isEqualTo("client-logos");
    }
}
