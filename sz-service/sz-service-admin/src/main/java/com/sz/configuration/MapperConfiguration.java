package com.sz.configuration;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.core.audit.AuditManager;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: 提取到sz-common-logger模块
 * @author: sz
 * @date: 2022/8/26 15:14
 */
@Slf4j
@Configuration
@MapperScan({"com.sz.admin.*.mapper", "com.sz.generator.mapper", "com.sz.www.*.mapper","com.sz.applet.*.mapper"})
public class MapperConfiguration {
    public MapperConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);
        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                log.info("{} ---- {}ms, row:{}",
                        formatSQL(auditMessage.getFullSql()),
                        auditMessage.getElapsedTime(),
                        auditMessage.getQueryCount())
        );
    }

    /**
     * 分页插件
     * @return
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    private String formatSQL(String sql) {
        return sql.replaceAll("\\s+", " ").replace("\\r", " ").replace("\\n", " ");
    }

}
