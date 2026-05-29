package com.sz.core.common.valid.validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonValidatorTest {

    private final PhoneValidator phoneValidator = new PhoneValidator();

    private final IdCardValidator idCardValidator = new IdCardValidator();

    private final NotZeroValidator notZeroValidator = new NotZeroValidator();

    @Test
    void phoneAllowsBlankButRejectsInvalidFormat() {
        assertThat(phoneValidator.isValid(null, null)).isTrue();
        assertThat(phoneValidator.isValid("", null)).isTrue();
        assertThat(phoneValidator.isValid("13812345678", null)).isTrue();
        assertThat(phoneValidator.isValid("12812345678", null)).isFalse();
        assertThat(phoneValidator.isValid("1381234567", null)).isFalse();
    }

    @Test
    void idCardValidatesBirthDateAndCheckCode() {
        assertThat(idCardValidator.isValid(null, null)).isTrue();
        assertThat(idCardValidator.isValid("", null)).isTrue();
        assertThat(idCardValidator.isValid("11010519491231002X", null)).isTrue();
        assertThat(idCardValidator.isValid("110105194912310021", null)).isFalse();
        assertThat(idCardValidator.isValid("11010518991231002X", null)).isFalse();
    }

    @Test
    void notZeroRejectsNullAndIntegerZeroValues() {
        assertThat(notZeroValidator.isValid(null, null)).isFalse();
        assertThat(notZeroValidator.isValid(0, null)).isFalse();
        assertThat(notZeroValidator.isValid(0L, null)).isFalse();
        assertThat(notZeroValidator.isValid(1, null)).isTrue();
        assertThat(notZeroValidator.isValid(1L, null)).isTrue();
    }
}
