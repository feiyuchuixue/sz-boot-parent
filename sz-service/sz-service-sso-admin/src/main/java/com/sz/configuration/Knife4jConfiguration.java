package com.sz.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("sz-admin后台管理系统API").version("1.0").description("Sz-Admin RESTful APIs")
                .termsOfService("http://127.0.0.1:9991").license(new License().name("Apache 2.0").url("http://127.0.0.1:9991")));
    }

}