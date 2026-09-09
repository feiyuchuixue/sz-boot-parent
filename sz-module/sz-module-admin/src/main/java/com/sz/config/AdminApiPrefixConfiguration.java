package com.sz.config;

import com.sz.core.common.web.ApiPrefixRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Admin API 前缀声明。
 */
@Configuration
public class AdminApiPrefixConfiguration {

    @Bean
    public ApiPrefixRegister adminApiPrefixRegister() {
        return new ApiPrefixRegister() {

            @Override
            public String module() {
                return "admin";
            }

            @Override
            public String prefix() {
                return "/admin";
            }

            @Override
            public String[] basePackages() {
                return new String[]{"com.sz.admin", "com.sz.applet", "com.sz.security.controller", "com.sz.www"};
            }
        };
    }
}
