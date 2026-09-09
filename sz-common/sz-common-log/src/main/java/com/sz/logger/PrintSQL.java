package com.sz.logger;

import com.mybatisflex.core.audit.AuditManager;
import com.sz.logger.AuditProperties.Sql;
import com.sz.logger.AuditProperties.SqlMode;
import lombok.extern.slf4j.Slf4j;

/**
 * PrintSQL
 *
 * @author sz
 * @since 2024/5/11 15:03
 */
@Slf4j(topic = "audit-sql-log")
public class PrintSQL {

    private PrintSQL() {
        throw new IllegalStateException("Utility class");
    }

    public static void print(Sql properties) {
        Sql sqlProperties = properties == null ? new Sql() : properties;
        if (sqlProperties.getMode() == SqlMode.OFF) {
            AuditManager.setAuditEnable(false);
            return;
        }

        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage -> {
            Long elapsedTime = auditMessage.getElapsedTime();
            long elapsed = elapsedTime == null ? 0L : elapsedTime;
            if (sqlProperties.getMode() == SqlMode.SLOW && elapsed < sqlProperties.getSlowThresholdMs()) {
                return;
            }
            if (sqlProperties.isFullSqlEnabled()) {
                log.info("audit.sql fullSql=\"{}\" elapsedMs={} rows={}", formatSQL(auditMessage.getFullSql()), elapsed, auditMessage.getQueryCount());
                return;
            }
            log.info("audit.sql summary elapsedMs={} rows={}", elapsed, auditMessage.getQueryCount());
        });
    }

    public static String formatSQL(String sql) {
        if (sql == null) {
            return "";
        }
        return sql.replaceAll("\\s+", " ").replace("\\r", " ").replace("\\n", " ");
    }

}
