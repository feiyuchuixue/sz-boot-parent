package com.sz.generator.core.script;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Resolves the SQL dialect used by generated scripts.
 */
@Component
@RequiredArgsConstructor
public class ScriptDialectResolver {

    private final DataSource dataSource;

    @Value("${DB_TYPE:}")
    private String dbType;

    public ScriptDialect resolveCurrent() {
        try (Connection connection = dataSource.getConnection()) {
            ScriptDialect dialect = ScriptDialect.from(connection.getMetaData().getDatabaseProductName());
            if (dialect != null) {
                return dialect;
            }
        } catch (SQLException ignored) {
            // Fall back to DB_TYPE below.
        }
        ScriptDialect dialect = ScriptDialect.from(dbType);
        return dialect != null ? dialect : ScriptDialect.MYSQL;
    }

    public ScriptDialect resolveSelected(String requestedDialect) {
        ScriptDialect requested = ScriptDialect.from(requestedDialect);
        return requested != null ? requested : resolveCurrent();
    }
}
