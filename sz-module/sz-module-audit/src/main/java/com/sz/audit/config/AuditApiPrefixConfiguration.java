package com.sz.audit.config;

import com.sz.core.common.web.ApiPrefixRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 审计 API 前缀声明。
 */
@Configuration
public class AuditApiPrefixConfiguration {

    @Bean
    public ApiPrefixRegister auditApiPrefixRegister() {
        return new ApiPrefixRegister() {

            @Override
            public String module() {
                return "audit";
            }

            @Override
            public String prefix() {
                return "/audit";
            }

            @Override
            public String[] basePackages() {
                return new String[]{"com.sz.audit.controller"};
            }
        };
    }
}
