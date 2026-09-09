package com.sz.generator.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仅在代码生成器模块启用时装配相关 Bean。
 * <p>
 * 是否启用统一由 {@code sz.api-prefix.modules.generator.enabled} 控制，未配置时默认启用。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(prefix = "sz.api-prefix.modules.generator", name = "enabled", havingValue = "true", matchIfMissing = true)
public @interface ConditionalOnGeneratorEnabled {
}
