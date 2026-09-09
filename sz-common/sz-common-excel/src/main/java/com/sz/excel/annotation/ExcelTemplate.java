package com.sz.excel.annotation;

import java.lang.annotation.*;

/**
 * 标记该类为 Excel 导入模板 DTO。
 *
 * <h3>使用示例</h3>
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;ExcelTemplate(alias = "sso用户导入模板.xlsx", validRows = 500)
 *     public class SsoUserImportDTO {
 * 
 *         &#64;ImportColumn
 *         &#64;ExcelProperty("用户名")
 *         private String username;
 *
 *         &#64;ExcelProperty("用户状态")
 *         @DictFormat(dictType = "platform_user_status", isSelected = true)
 *         private String platformUserStatusCd;
 *     }
 * }
 * </pre>
 *
 * @author sz
 * @since 2026/03/25
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelTemplate {

    /**
     * 模板唯一标识，同时作为动态生成时的下载文件名。
     * <p>
     * 建议以 {@code .xlsx} 结尾，例如 {@code "sso用户导入模板.xlsx"}
     * </p>
     */
    String alias();

    /**
     * 数据验证覆盖行数（从第 2 行起）。
     * <p>
     * 对所有列的数据验证（含下拉序列验证和必填非空验证）统一生效。 超大批量导入时可调大此值，但过大会轻微增加文件体积。
     * </p>
     */
    int validRows() default 1000;

}
