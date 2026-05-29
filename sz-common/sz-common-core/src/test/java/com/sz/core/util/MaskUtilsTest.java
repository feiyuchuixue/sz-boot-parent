package com.sz.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskUtilsTest {

    @Test
    void maskUsernameKeepsUsefulEdges() {
        assertThat(MaskUtils.maskUsername(null)).isNull();
        assertThat(MaskUtils.maskUsername("a")).isEqualTo("a");
        assertThat(MaskUtils.maskUsername("ab")).isEqualTo("a*");
        assertThat(MaskUtils.maskUsername("zhangsan")).isEqualTo("z******n");
    }

    @Test
    void maskEmailHandlesLocalPartByLength() {
        assertThat(MaskUtils.maskEmail(null)).isNull();
        assertThat(MaskUtils.maskEmail("invalid")).isEqualTo("invalid");
        assertThat(MaskUtils.maskEmail("a@example.com")).isEqualTo("a*@example.com");
        assertThat(MaskUtils.maskEmail("test@example.com")).isEqualTo("t**t@example.com");
        assertThat(MaskUtils.maskEmail("zhangsan@example.com")).isEqualTo("zha***an@example.com");
        assertThat(MaskUtils.maskEmail("feiabcdfxue@example.com")).isEqualTo("fei*****xue@example.com");
    }

    @Test
    void maskSensitiveNumbersUseStableBoundaries() {
        assertThat(MaskUtils.maskPhone("13812345678")).isEqualTo("138****5678");
        assertThat(MaskUtils.maskIdCard("110101199001011234")).isEqualTo("110101********1234");
        assertThat(MaskUtils.maskBankCard("6222021234567890123")).isEqualTo("6222 **** **** 0123");
    }
}
