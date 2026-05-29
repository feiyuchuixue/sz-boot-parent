package com.sz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

@Data
@Configuration
@ConfigurationProperties(prefix = "sz.features")
public class FeatureProperties {

    /**
     * 是否启用代码生成器入口。
     * <p>
     * 未显式配置时根据 generator 模块是否在运行时 classpath 中自动判断，保证本地开发默认可用， {@code prod-lite}
     * 构建排除 generator 后自动隐藏菜单和权限入口。
     */
    private Boolean generator;

    /**
     * 返回代码生成器功能是否可用。
     * <p>
     * yml 中显式配置 {@code sz.features.generator} 时优先使用配置值；未配置时按 classpath 自动探测。
     *
     * @return true 表示展示并下发 generator 菜单/权限，false 表示隐藏
     */
    public boolean isGenerator() {
        if (generator != null) {
            return generator;
        }
        return ClassUtils.isPresent("com.sz.generator.controller.GeneratorTableController", getClass().getClassLoader());
    }
}
