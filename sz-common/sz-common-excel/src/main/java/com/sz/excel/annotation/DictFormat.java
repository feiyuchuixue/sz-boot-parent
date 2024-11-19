package com.sz.excel.annotation;

import com.sz.excel.core.ExcelDynamicSelect;

import java.lang.annotation.*;

/**
 * 字典,下拉处理
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DictFormat {

    /**
     * 如果是字典类型，请设置字典的type值 (如: account_status)
     */
    String dictType() default "";

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    String readConverterExp() default "";

    /**
     * 分隔符，读取字符串组内容
     */
    String separator() default ",";

    /**
     * （导出时）是否作为下拉项
     *
     * @return
     */
    boolean isSelected() default false;

    /**
     * 动态下拉内容( 适用于导出模版的场景)
     */
    Class<? extends ExcelDynamicSelect>[] sourceClass() default {};

    /**
     * 字典是否使用别名 （为true时将使用alias而非id做映射）
     *
     * @return
     */
    boolean useAlias() default false;

}
