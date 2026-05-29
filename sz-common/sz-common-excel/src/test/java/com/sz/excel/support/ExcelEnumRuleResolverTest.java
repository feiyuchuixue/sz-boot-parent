package com.sz.excel.support;

import com.sz.core.common.enums.YesNoEnum;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.annotation.ExcelEnumFormat;
import com.sz.excel.enums.ExcelEnumPreset;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcelEnumRuleResolverTest {

    @Test
    void resolveReturnsYesNoRuleForYesNoPreset() throws NoSuchFieldException {
        ExcelEnumRuleResolver.EnumRule rule = ExcelEnumRuleResolver.resolve(field("enabled"));

        assertThat(rule).isNotNull();
        assertThat(rule.writeField()).isEqualTo("desc");
        assertThat(rule.readField()).isEqualTo("desc");
        assertThat(rule.ignoreCase()).isTrue();
        assertThat(rule.fallbackToName()).isTrue();
    }

    @Test
    void resolveReturnsCustomRuleWhenFieldsAreExplicit() throws NoSuchFieldException {
        ExcelEnumRuleResolver.EnumRule rule = ExcelEnumRuleResolver.resolve(field("status"));

        assertThat(rule).isNotNull();
        assertThat(rule.writeField()).isEqualTo("label");
        assertThat(rule.readField()).isEqualTo("label");
        assertThat(rule.ignoreCase()).isFalse();
        assertThat(rule.fallbackToName()).isFalse();
    }

    @Test
    void resolveIgnoresDictFormatAndNonEnumFields() throws NoSuchFieldException {
        assertThat(ExcelEnumRuleResolver.resolve(field("dictStatus"))).isNull();
        assertThat(ExcelEnumRuleResolver.resolve(field("name"))).isNull();
        assertThat(ExcelEnumRuleResolver.resolve(null)).isNull();
    }

    @Test
    void resolveRejectsInvalidAnnotationCombinations() throws NoSuchFieldException {
        assertThatThrownBy(() -> ExcelEnumRuleResolver.resolve(field("invalidYesNo")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("仅允许用于 YesNoEnum");
        assertThatThrownBy(() -> ExcelEnumRuleResolver.resolve(field("invalidCustom")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("必须同时指定");
    }

    private static Field field(String name) throws NoSuchFieldException {
        return SampleRow.class.getDeclaredField(name);
    }

    private enum Status {
        ENABLED,
        DISABLED
    }

    private static class SampleRow {

        @ExcelEnumFormat(preset = ExcelEnumPreset.YES_NO)
        private YesNoEnum enabled;

        @ExcelEnumFormat(preset = ExcelEnumPreset.CUSTOM, writeField = "label", readField = "label", ignoreCase = false, fallbackToName = false)
        private Status status;

        @DictFormat(dictType = "status")
        @ExcelEnumFormat(preset = ExcelEnumPreset.CUSTOM, writeField = "label", readField = "label")
        private Status dictStatus;

        private String name;

        @ExcelEnumFormat(preset = ExcelEnumPreset.YES_NO)
        private Status invalidYesNo;

        @ExcelEnumFormat(preset = ExcelEnumPreset.CUSTOM, writeField = "label")
        private Status invalidCustom;
    }
}
