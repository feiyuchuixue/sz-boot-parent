package com.sz.admin.system.script;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Resolves the SQL dialect used by admin exported scripts.
 */
@Component
@RequiredArgsConstructor
public class AdminScriptDialectResolver {

    private final DataSource dataSource;

    @Value("${DB_TYPE:}")
    private String dbType;

    public AdminScriptDialect resolveCurrent() {
        try (Connection connection = dataSource.getConnection()) {
            AdminScriptDialect dialect = AdminScriptDialect.from(connection.getMetaData().getDatabaseProductName());
            if (dialect != null) {
                return dialect;
            }
        } catch (SQLException ignored) {
            // Fall back to DB_TYPE below.
        }
        AdminScriptDialect dialect = AdminScriptDialect.from(dbType);
        return dialect != null ? dialect : AdminScriptDialect.MYSQL;
    }

    public AdminScriptDialect resolveSelected(String requestedDialect) {
        AdminScriptDialect requested = AdminScriptDialect.from(requestedDialect);
        return requested != null ? requested : resolveCurrent();
    }
}
