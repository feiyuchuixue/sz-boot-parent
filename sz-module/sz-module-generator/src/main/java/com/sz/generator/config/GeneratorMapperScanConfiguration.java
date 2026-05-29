package com.sz.generator.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * sz-module-generator module mapper scan configuration.
 */
@Configuration
@MapperScan(basePackages = "com.sz.generator.mapper")
public class GeneratorMapperScanConfiguration {
}
