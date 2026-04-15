package com.sz.excel.core;

import com.sz.excel.annotation.EnableExcelTemplateScan;
import com.sz.excel.annotation.ExcelTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel 导入模板 DTO 注册中心。
 * <p>
 * 应用启动后扫描 {@link EnableExcelTemplateScan#basePackages()} 指定的包， 发现所有带
 * {@link ExcelTemplate} 注解的类，以 {@link ExcelTemplate#alias()} 为 key、 对应 Class 为
 * value 缓存到内存 Map，供动态模板生成接口按 alias 查找 Class。
 * </p>
 *
 * <h3>注册触发</h3>
 * <p>
 * 本 Bean 不通过 {@code @Component} 自动注册，必须在启动类或配置类上标注
 * {@link EnableExcelTemplateScan}才会被激活。未标注时本 Bean 不存在， 动态模板生成功能静默跳过。
 * </p>
 *
 * <h3>alias 冲突处理</h3>
 * <p>
 * 同一 alias 在多个 DTO 上声明时，保留先扫描到的，并输出 WARN 日志提示冲突位置。
 * </p>
 *
 * @author sz
 * @since 2026/04/09
 */
@Slf4j
public class ExcelTemplateScanRegistry implements InitializingBean {

    /** Bean 名称常量，供 {@link ExcelTemplateScanRegistrar} 注册时使用 */
    public static final String BEAN_NAME = "excelTemplateScanRegistry";

    /** 用户通过 @EnableExcelTemplateScan(basePackages=...) 指定的扫描包 */
    private final String[] basePackages;

    /** alias → ImportDTO Class 映射 */
    private final Map<String, Class<?>> registry = new ConcurrentHashMap<>();

    public ExcelTemplateScanRegistry(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public void afterPropertiesSet() {
        scanAndRegister();
    }

    /**
     * 扫描 basePackages 下所有带 {@link ExcelTemplate} 注解的类并注册。
     * <p>
     * basePackages 中的每一项支持 {@code *} 通配符，例如 {@code "com.sz.sso.*.pojo.dto"}
     * 会被展开为所有匹配的实际包名后再交给 scanner 扫描，从而减少扫描范围、加快启动速度。
     * 不含通配符的普通包名则直接传递，行为与以前一致（自动递归扫描子包）。
     * </p>
     */
    private void scanAndRegister() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ExcelTemplate.class));

        // 将 basePackages 中含通配符的条目展开为实际包名列表
        List<String> resolvedPackages = resolvePackages(basePackages);
        log.debug("[ExcelTemplateScanRegistry] 展开后扫描包列表: {}", resolvedPackages);

        for (String basePackage : resolvedPackages) {
            scanner.findCandidateComponents(basePackage).forEach(bd -> {
                String className = bd.getBeanClassName();
                try {
                    Class<?> clazz = Class.forName(className);
                    ExcelTemplate annotation = clazz.getAnnotation(ExcelTemplate.class);
                    if (annotation == null) {
                        return;
                    }
                    String alias = annotation.alias();
                    Class<?> existing = registry.putIfAbsent(alias, clazz);
                    if (existing != null) {
                        // alias 冲突：保留先注册的，记录 WARN 日志
                        log.warn("[ExcelTemplateScanRegistry] alias 冲突，已忽略后注册类: alias='{}', 已注册='{}', 忽略='{}'", alias, existing.getName(), className);
                    } else {
                        log.debug("[ExcelTemplateScanRegistry] 注册模板 alias='{}' -> {}", alias, className);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("[ExcelTemplateScanRegistry] 无法加载类: {}", className, e);
                }
            });
        }

        log.info("[ExcelTemplateScanRegistry] 原始配置包: {}, 展开后共 {} 个包, 注册 {} 个导入模板 DTO: {}", Arrays.toString(basePackages), resolvedPackages.size(),
                registry.size(), registry.keySet());
    }

    /**
     * 将包名列表中含 {@code *} 通配符的条目展开为实际包名。
     * <p>
     * 展开规则：将包名中的 {@code .} 转换为路径分隔符 {@code /}，并在末尾追加 {@code /**}， 通过
     * {@link PathMatchingResourcePatternResolver} 扫描 classpath 上匹配的目录资源，
     * 再将资源路径还原为包名。不含通配符的条目原样保留。
     * </p>
     *
     * @param packages
     *            原始包名数组，可含 {@code *} 通配符
     * @return 展开后的实际包名列表（去重，保持顺序）
     */
    private List<String> resolvePackages(String[] packages) {
        List<String> result = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (String pkg : packages) {
            if (!pkg.contains("*")) {
                if (!result.contains(pkg)) {
                    result.add(pkg);
                }
                continue;
            }

            // 含通配符：将包名转为 classpath 资源路径模式进行展开
            // 例如 "com.sz.sso.*.pojo.dto" → "classpath*:com/sz/sso/*/pojo/dto"
            String pathPattern = "classpath*:" + pkg.replace('.', '/');
            try {
                Resource[] resources = resolver.getResources(pathPattern);
                for (Resource resource : resources) {
                    String uri = resource.getURI().toString();
                    // 从 URI 中提取包路径，再转回包名
                    // URI 示例: file:/.../.../com/sz/sso/provider/pojo/dto
                    // jar:file:/...!/com/sz/sso/provider/pojo/dto
                    int idx = findPackageStart(uri, pkg);
                    if (idx >= 0) {
                        String resolvedPkg = uri.substring(idx).replace('/', '.').replaceAll("[/\\\\]$", "");
                        if (!result.contains(resolvedPkg)) {
                            result.add(resolvedPkg);
                        }
                    }
                }
            } catch (IOException e) {
                log.warn("[ExcelTemplateScanRegistry] 通配符包名展开失败，将退化为原始包名: '{}', 原因: {}", pkg, e.getMessage());
                // 展开失败时退化：取通配符之前的固定前缀部分作为扫描根包
                String fallback = pkg.contains("*") ? pkg.substring(0, pkg.indexOf('*')).replaceAll("\\.$", "") : pkg;
                if (!result.contains(fallback)) {
                    result.add(fallback);
                }
            }
        }

        return result;
    }

    /**
     * 从 URI 字符串中定位包路径起始位置的通用方法。
     * <p>
     * 取通配符模式中第一段固定前缀（第一个 {@code *} 之前的部分）， 在 URI 中找到对应路径片段的起始索引。
     * </p>
     */
    private int findPackageStart(String uri, String originalPattern) {
        // 取 "com.sz.sso.*.pojo.dto" 中 * 之前的固定部分 "com/sz/sso/"
        String prefix = originalPattern.substring(0, originalPattern.indexOf('*')).replace('.', '/');
        int idx = uri.indexOf(prefix);
        return idx;
    }

    /**
     * 根据 alias 查找对应的 ImportDTO Class。
     *
     * @param alias
     *            {@link ExcelTemplate#alias()} 值
     * @return 对应 Class，未找到时返回 {@code null}
     */
    public Class<?> getByAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }
        return registry.get(alias);
    }

    /**
     * 返回所有已注册的 alias（只读视图）。
     */
    public Set<String> getAllAliases() {
        return Collections.unmodifiableSet(registry.keySet());
    }
}
