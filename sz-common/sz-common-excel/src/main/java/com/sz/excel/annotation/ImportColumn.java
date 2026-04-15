package com.sz.excel.annotation;

import java.lang.annotation.*;

/**
 * 标记 Excel 导入模板中的列属性。
 * <p>
 * 在 {@link ExcelTemplate} 标注的 DTO 字段上使用此注解后：
 * <ul>
 * <li>当 {@code required=true}时，表头显示红色 {@code *} 前缀， 且该列数据区添加 Excel
 * 数据验证，提醒用户此字段不可为空</li>
 * <li>当 {@code required=false} 时，表头保持黑色字体，无数据验证</li>
 * <li>{@code columnWidth} 可强制指定列宽，{@code -1} 表示自动计算</li>
 * </ul>
 * </p>
 *
 * <h3>使用示例</h3>
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;ImportColumn(required = true)
 *     &#64;ExcelProperty("用户名")
 *     private String username;
 *
 *     @ImportColumn(columnWidth = 40)
 *     &#64;ExcelProperty("备注")
 *     private String remark;
 * }
 * </pre>
 *
 * @author sz
 * @since 2026/03/25
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportColumn {

    /**
     * 是否为必填字段。
     * <p>
     * 默认 {@code false}：表头加红色 {@code *} 前缀，并添加非空数据验证。
     * </p>
     */
    boolean required() default false;

    /**
     * 自定义列宽（字符单位，即 Excel setColumnWidth 参数 / 256）。
     * <p>
     * 默认值 {@code -1} 表示不强制指定，由策略类自动计算并兜底最小宽度。
     * </p>
     */
    int columnWidth() default -1;

}
