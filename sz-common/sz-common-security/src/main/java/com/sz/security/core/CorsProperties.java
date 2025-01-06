package com.sz.security.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName CorsProperties
 * @Author sz
 * @Date 2025/1/6 10:02
 * @Version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.cors")
public class CorsProperties {

    private CopyOnWriteArrayList<String> allowedOrigins;

}
