package com.sz.logger;

import com.mybatisflex.core.audit.AuditManager;
import lombok.extern.slf4j.Slf4j;

/**
 * PrintSQL
 * 
 * @author sz
 * @since 2024/5/11 15:03
 */
@Slf4j
public class PrintSQL {

    private PrintSQL() {
        throw new IllegalStateException("Utility class");
    }

    public static void print() {
        // 开启审计功能
        AuditManager.setAuditEnable(true);
        // 自定义sql打印 或 自定义审计功能，也可结合logback将sql日志输出到独立文件中。
        // 详见https://mybatis-flex.com/zh/core/audit.html
        // 设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage -> log.info("{} ---- {}ms, row:{}", formatSQL(auditMessage.getFullSql()), auditMessage.getElapsedTime(),
                auditMessage.getQueryCount()));
    }

    public static String formatSQL(String sql) {
        return sql.replaceAll("\\s+", " ").replace("\\r", " ").replace("\\n", " ");
    }

}
