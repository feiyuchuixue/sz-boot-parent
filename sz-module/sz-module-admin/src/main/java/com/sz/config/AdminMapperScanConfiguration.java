package com.sz.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * sz-module-admin 模块自治 MapperScan 启动壳 sz-service-admin 不再感知本模块的 mapper 路径
 */
@Configuration
@MapperScan(basePackages = {"com.sz.admin.*.mapper", "com.sz.applet.*.mapper", "com.sz.generator.mapper"})
public class AdminMapperScanConfiguration {
}