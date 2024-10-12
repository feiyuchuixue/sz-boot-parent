package com.sz.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @ClassName StringUtilsTest
 * @Author sz
 * @Date 2024/6/20 15:28
 * @Version 1.0
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

}