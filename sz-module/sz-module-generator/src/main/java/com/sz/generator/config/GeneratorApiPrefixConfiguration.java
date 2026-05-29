package com.sz.generator.config;

import com.sz.core.common.web.ApiPrefixRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码生成器 API 前缀声明。
 */
@Configuration
public class GeneratorApiPrefixConfiguration {

    @Bean
    public ApiPrefixRegister generatorApiPrefixRegister() {
        return new ApiPrefixRegister() {

            @Override
            public String module() {
                return "generator";
            }

            @Override
            public String prefix() {
                return "/generator";
            }

            @Override
            public String[] basePackages() {
                return new String[]{"com.sz.generator.controller"};
            }
        };
    }
}
