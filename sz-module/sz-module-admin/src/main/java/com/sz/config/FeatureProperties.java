package com.sz.config;

import com.sz.core.common.web.ApiPrefixProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * 功能模块可用性判断。
 * <p>
 * 当前主要用于判断代码生成器入口是否可用。generator 依赖存在且
 * {@code sz.api-prefix.modules.generator.enabled} 未显式关闭时，认为功能可用。
 */
@Configuration
@EnableConfigurationProperties(ApiPrefixProperties.class)
public class FeatureProperties {

    private static final String GENERATOR_MODULE = "generator";

    private final ApiPrefixProperties apiPrefixProperties;

    public FeatureProperties(ApiPrefixProperties apiPrefixProperties) {
        this.apiPrefixProperties = apiPrefixProperties;
    }

    /**
     * 返回代码生成器功能是否可用。
     *
     * @return true 表示展示并下发 generator 菜单/权限，false 表示隐藏 generator 入口
     */
    public boolean isGenerator() {
        return isGeneratorOnClasspath() && isGeneratorModuleEnabled();
    }

    private boolean isGeneratorOnClasspath() {
        return ClassUtils.isPresent("com.sz.generator.controller.GeneratorTableController", getClass().getClassLoader());
    }

    private boolean isGeneratorModuleEnabled() {
        if (apiPrefixProperties.getModules() == null) {
            return true;
        }
        ApiPrefixProperties.Module module = apiPrefixProperties.getModules().get(GENERATOR_MODULE);
        return module == null || module.getEnabled() == null || module.getEnabled();
    }
}
