package com.sz.excel.annotation;

import com.sz.excel.core.ExcelTemplateScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Excel 导入模板 DTO 的 classpath 扫描。
 * <p>
 * 标注此注解后，框架将在应用启动时扫描 {@link #basePackages()} 指定的包， 自动发现所有带
 * {@link ExcelTemplate} 注解的类，并以 {@link ExcelTemplate#alias()} 为 key 注册到
 * {@link com.sz.excel.core.ExcelTemplateScanRegistry}， 供动态模板生成接口按 alias 查找对应
 * Class。
 * </p>
 *
 * <h3>使用示例</h3>
 * 
 * <pre>
 * {@code
 * &#64;EnableExcelTemplateScan(basePackages = {"com.sz.sso", "com.sz.admin"})
 * &#64;SpringBootApplication
 * public class PlatformAdminApplication { ... }
 * }
 * </pre>
 *
 * <h3>注意</h3>
 * <ul>
 * <li>不标注此注解时，{@link com.sz.excel.core.ExcelTemplateScanRegistry} Bean 不会被注册，
 * 动态模板生成功能静默跳过，下载接口返回 404。</li>
 * <li>{@link #basePackages()} 应尽量精确，避免指定过大的包范围影响启动速度。</li>
 * <li>同一 alias 在多个 DTO 上重复声明时，保留先扫描到的，并输出 WARN 日志提示冲突。</li>
 * </ul>
 *
 * @author sz
 * @since 2026/04/09
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ExcelTemplateScanRegistrar.class)
public @interface EnableExcelTemplateScan {

    /**
     * 要扫描的包名，支持多个。
     * <p>
     * 建议精确到业务模块包，例如 {@code "com.sz.sso"}、{@code "com.sz.admin"}。
     * </p>
     */
    String[] basePackages();
}
