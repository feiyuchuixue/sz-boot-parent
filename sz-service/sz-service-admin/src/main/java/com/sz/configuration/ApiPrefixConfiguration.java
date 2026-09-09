package com.sz.configuration;

import com.sz.core.common.web.ApiPrefixProperties;
import com.sz.core.common.web.ApiPrefixRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ApiPrefixProperties.class)
public class ApiPrefixConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ApiPrefixConfiguration.class);

    private final ApiPrefixProperties apiPrefixProperties;

    private final ObjectProvider<ApiPrefixRegister> apiPrefixRegisters;

    public ApiPrefixConfiguration(ApiPrefixProperties apiPrefixProperties, ObjectProvider<ApiPrefixRegister> apiPrefixRegisters) {
        this.apiPrefixProperties = apiPrefixProperties;
        this.apiPrefixRegisters = apiPrefixRegisters;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        Map<String, List<String>> prefixPackages = new LinkedHashMap<>();
        apiPrefixRegisters.orderedStream().forEach(register -> collectModulePrefix(prefixPackages, register));
        prefixPackages
                .forEach((prefix, basePackages) -> configurer.addPathPrefix(prefix, HandlerTypePredicate.forBasePackage(basePackages.toArray(String[]::new))));
    }

    private void collectModulePrefix(Map<String, List<String>> prefixPackages, ApiPrefixRegister register) {
        ApiPrefixProperties.Module moduleProperties = apiPrefixProperties.getModules().get(register.module());
        boolean enabled = moduleProperties == null || moduleProperties.getEnabled() == null ? register.enabled() : moduleProperties.getEnabled();
        if (!enabled) {
            return;
        }
        String prefix = moduleProperties == null || moduleProperties.getPrefix() == null ? register.prefix() : moduleProperties.getPrefix();
        log.info("Register API prefix module={}, prefix={}, basePackages={}", register.module(), normalize(prefix), register.basePackages());
        List<String> basePackages = prefixPackages.computeIfAbsent(normalize(prefix), key -> new ArrayList<>());
        basePackages.addAll(List.of(register.basePackages()));
    }

    private String normalize(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "";
        }
        String normalized = prefix.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        while (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
