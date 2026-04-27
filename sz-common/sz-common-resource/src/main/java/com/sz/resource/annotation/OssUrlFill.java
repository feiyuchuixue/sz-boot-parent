package com.sz.resource.annotation;

import com.sz.resource.spi.OssUrlResolver;

import java.lang.annotation.*;

/**
 * 标注在 VO 的 String 类型 objectKey 字段上，框架在返回前自动将其原地替换为完整可访问 URL。
 *
 * <pre>
 * 
 * {
 *     &#64;code
 *     // 基本用法：默认 ResourceService 解析
 *     &#64;OssUrlFill(sceneCode = "sso.user.avatar")
 *     private String avatars;
 *
 *     // 自定义解析器（推荐）：直接传 .class，类型安全
 *     @OssUrlFill(sceneCode = "sso.user.avatar", resolverClass = SsoAvatarUrlResolver.class)
 *     private String avatars;
 * }
 * </pre>
 *
 * <p>
 * 解析器优先级：{@link #resolverClass} &gt; {@link #customResolver} &gt; 默认
 * ResourceService
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OssUrlFill {

    /** 资源场景码，对应配置项 {@code sz.resource.scenes.[code]}。 */
    String sceneCode();

    /**
     * 自定义解析器类型（推荐）。框架按类型从容器获取 Bean，与 {@link #customResolver} 同时存在时优先生效。
     *
     * @see OssUrlResolver
     */
    Class<? extends OssUrlResolver<?>> resolverClass() default OssUrlResolver.None.class;

    /**
     * 自定义解析器 Bean 名称（兼容方式）。{@link #resolverClass} 已设置时本属性被忽略。
     *
     * @see OssUrlResolver
     */
    String customResolver() default "";
}
