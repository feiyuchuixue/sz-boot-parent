package com.sz.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * StringUtilsTest
 * 
 * @author sz
 * @since 2024/6/20 15:28
 * @version 1.0
 */
class StringUtilsTest {

    @Test
    void toSnakeCase() {
        String str = StringUtils.toSnakeCase("TeacherStatics");
        assertEquals("teacher_statics", str);
    }

    @Test
    void toCamelCase() {
        assertEquals("teacherStatics", StringUtils.toCamelCase("teacher_statics"));
        assertEquals("teacherstatics", StringUtils.toCamelCase("teacherstatics"));
        assertEquals("teacherStatics", StringUtils.toCamelCase("teacher__Statics"));
        assertEquals("teacherStatics", StringUtils.toCamelCase("teacher_Statics"));
        assertEquals("teacherStatics", StringUtils.toCamelCase("TEACHER_STATICS"));
    }

    @Test
    void containsAnyIgnoreCaseMatchesRegularCharSequenceWithoutClassCast() {
        assertThat(StringUtils.containsAnyIgnoreCase("sys.user.create_btn", "USER", "role")).isTrue();
        assertThat(StringUtils.containsAnyIgnoreCase("sys.user.create_btn", "dept", "role")).isFalse();
        assertThat(StringUtils.containsAnyIgnoreCase("", "sys")).isFalse();
        assertThat(StringUtils.containsAnyIgnoreCase(null, "sys")).isFalse();
    }

}
