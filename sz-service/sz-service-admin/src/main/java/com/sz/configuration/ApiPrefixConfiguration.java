package com.sz.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfiguration implements WebMvcConfigurer {

    @Value("${sz.api-prefix.admin:/admin}")
    private String adminPrefix;

    @Value("${sz.api-prefix.generator:/generator}")
    private String generatorPrefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(normalize(adminPrefix), HandlerTypePredicate.forBasePackage(
                "com.sz.admin.system",
                "com.sz.admin.teacher",
                "com.sz.applet",
                "com.sz.security.controller",
                "com.sz.www"
        ));
        configurer.addPathPrefix(normalize(generatorPrefix), HandlerTypePredicate.forBasePackage("com.sz.generator.controller"));
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
