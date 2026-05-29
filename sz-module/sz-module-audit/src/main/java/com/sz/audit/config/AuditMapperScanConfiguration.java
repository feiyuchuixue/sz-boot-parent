package com.sz.audit.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * sz-module-audit 模块自治 MapperScan。
 */
@Configuration
@MapperScan(basePackages = "com.sz.audit.mapper")
public class AuditMapperScanConfiguration {
}
