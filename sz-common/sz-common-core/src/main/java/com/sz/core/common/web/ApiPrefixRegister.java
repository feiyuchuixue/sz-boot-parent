package com.sz.core.common.web;

/**
 * API 前缀注册声明。
 * <p>
 * 业务模块通过提供该 Bean 声明自己的 Controller 包和默认挂载前缀，启动服务统一收集并注册。
 * </p>
 */
public interface ApiPrefixRegister {

    /**
     * 模块编码，用于匹配 sz.api-prefix.modules 下的配置项。
     */
    String module();

    /**
     * 默认 API 前缀。
     */
    String prefix();

    /**
     * 需要追加前缀的 Controller 基础包。
     */
    String[] basePackages();

    /**
     * 未配置时是否默认启用。
     */
    default boolean enabled() {
        return true;
    }
}
