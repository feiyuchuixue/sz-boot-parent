package com.sz.excel.annotation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelTemplateAnnotationTest {

    @Test
    void excelTemplateMetadataIsAvailableAtRuntime() throws NoSuchFieldException {
        ExcelTemplate template = TemplateImportDTO.class.getAnnotation(ExcelTemplate.class);
        Field requiredField = TemplateImportDTO.class.getDeclaredField("username");
        Field optionalField = TemplateImportDTO.class.getDeclaredField("remark");

        assertThat(template.alias()).isEqualTo("用户导入模板.xlsx");
        assertThat(template.validRows()).isEqualTo(500);
        assertThat(requiredField.getAnnotation(ImportColumn.class).required()).isTrue();
        assertThat(requiredField.getAnnotation(ImportColumn.class).columnWidth()).isEqualTo(40);
        assertThat(optionalField.getAnnotation(ImportColumn.class).required()).isFalse();
        assertThat(optionalField.getAnnotation(ImportColumn.class).columnWidth()).isEqualTo(-1);
    }

    @ExcelTemplate(alias = "用户导入模板.xlsx", validRows = 500)
    private static class TemplateImportDTO {

        @ImportColumn(required = true, columnWidth = 40)
        private String username;

        @ImportColumn
        private String remark;
    }
}
