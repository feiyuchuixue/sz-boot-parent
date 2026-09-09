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

    private Map<String, Module> modules = new LinkedHashMap<>();

    @Setter
    @Getter
    public static class Module {

        private Boolean enabled;

        private String prefix;

    }
}
