package com.sz.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


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
        Assertions.assertEquals("teacher_statics", str);
    }

}