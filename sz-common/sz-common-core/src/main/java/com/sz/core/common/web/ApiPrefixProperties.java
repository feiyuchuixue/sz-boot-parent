package com.sz.core.common.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API 前缀配置。
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "sz.api-prefix")
public class ApiPrefixProperties {

    /**
     * 模块 API 前缀配置。Map 键与运行时 ApiPrefixRegister.module() 返回值对应，属于开放集合；当前内置
     * admin、audit、generator， 新模块可注册自己的键。未配置某个模块时，该模块完全使用注册器声明的 enabled 和 prefix；本
     * Map 应保持非 null。
     */
    private Map<String, Module> modules = new LinkedHashMap<>();

    @Setter
    @Getter
    public static class Module {

        /**
         * 是否注册该模块的 Controller 路径前缀。Java 属性为 null 时继承 ApiPrefixRegister.enabled()；YAML
         * 省略、显式 null 或 空标量均保持该属性为 null。false 跳过该模块的路径前缀注册，但不会卸载模块 Bean、数据库变更或其他功能。
         */
        private Boolean enabled;

        /**
         * 追加到模块 Controller 路径之前的业务前缀。只有 Java 属性为 null 时才继承
         * ApiPrefixRegister.prefix()；YAML 应省略本项 以继承，显式 null
         * 或空标量会绑定为空串并清除模块前缀，纯空白也会清除。非空值会去除首尾空白、自动补齐开头斜杠，
         * 并去除根路径之外的末尾斜杠。server.servlet.context-path 仍在本前缀之外生效。
         */
        private String prefix;

    }
}
