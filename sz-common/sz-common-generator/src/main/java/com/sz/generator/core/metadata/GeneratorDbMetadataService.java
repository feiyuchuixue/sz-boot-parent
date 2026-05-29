package com.sz.generator.core.metadata;

import com.sz.core.common.entity.PageResult;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.generator.pojo.dto.DbTableQueryDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.result.TableColumResult;
import com.sz.generator.pojo.result.TableResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Reads table metadata for the generator without binding mapper XML to one
 * dialect.
 */
@Service
@RequiredArgsConstructor
public class GeneratorDbMetadataService {

    private final DataSource dataSource;

    public List<TableResult> selectDbTableListByNames(List<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            return Collections.emptyList();
        }
        return execute(connection -> {
            DatabaseDialect dialect = DatabaseDialect.from(connection);
            String schema = currentSchema(connection, dialect);
            String placeholders = placeholders(tableNames.size());
            String sql;
            if (dialect == DatabaseDialect.POSTGRESQL) {
                sql = """
                        SELECT c.relname AS table_name,
                               COALESCE(obj_description(c.oid, 'pg_class'), '') AS table_comment,
                               '' AS create_time,
                               '' AS update_time
                        FROM pg_catalog.pg_class c
                        JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
                        WHERE c.relkind = 'r'
                          AND n.nspname = ?
                          AND c.relname IN (%s)
                        ORDER BY c.relname
                        """.formatted(placeholders);
            } else {
                sql = """
                        SELECT table_name, table_comment, create_time, update_time
                        FROM information_schema.tables
                        WHERE table_schema = ?
                          AND table_name IN (%s)
                        ORDER BY create_time DESC
                        """.formatted(placeholders);
            }

            List<Object> params = new ArrayList<>();
            params.add(schema);
            params.addAll(tableNames);
            return queryTables(connection, sql, params);
        });
    }

    public List<TableColumResult> selectDbTableColumnsByName(String tableName) {
        return execute(connection -> {
            DatabaseDialect dialect = DatabaseDialect.from(connection);
            String schema = currentSchema(connection, dialect);
            String sql;
            if (dialect == DatabaseDialect.POSTGRESQL) {
                sql = """
                        SELECT c.column_name,
                               CASE WHEN c.is_nullable = 'NO' AND k.column_name IS NULL THEN '1' ELSE NULL END AS is_required,
                               CASE WHEN k.column_name IS NOT NULL THEN '1' ELSE '0' END AS is_pk,
                               c.ordinal_position AS sort,
                               COALESCE(pgd.description, '') AS column_comment,
                               CASE WHEN c.is_identity = 'YES' OR c.column_default LIKE 'nextval(%%' THEN '1' ELSE '0' END AS is_increment,
                               CASE
                                   WHEN c.data_type = 'character varying' THEN 'varchar(' || c.character_maximum_length || ')'
                                   WHEN c.data_type = 'character' THEN 'char(' || c.character_maximum_length || ')'
                                   WHEN c.data_type = 'numeric' THEN
                                       CASE WHEN c.numeric_precision IS NULL THEN 'decimal'
                                            ELSE 'decimal(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')' END
                                   WHEN c.data_type IN ('timestamp without time zone', 'timestamp with time zone') THEN 'timestamp'
                                   WHEN c.data_type = 'time without time zone' THEN 'time'
                                   WHEN c.data_type = 'integer' THEN 'int'
                                   WHEN c.data_type = 'USER-DEFINED' THEN c.udt_name
                                   ELSE c.data_type
                               END AS column_type
                        FROM information_schema.columns c
                        LEFT JOIN (
                            SELECT ku.table_schema, ku.table_name, ku.column_name
                            FROM information_schema.table_constraints tc
                            JOIN information_schema.key_column_usage ku
                              ON tc.constraint_name = ku.constraint_name
                             AND tc.table_schema = ku.table_schema
                             AND tc.table_name = ku.table_name
                            WHERE tc.constraint_type = 'PRIMARY KEY'
                        ) k ON k.table_schema = c.table_schema
                            AND k.table_name = c.table_name
                            AND k.column_name = c.column_name
                        LEFT JOIN pg_catalog.pg_statio_all_tables st
                          ON st.schemaname = c.table_schema
                         AND st.relname = c.table_name
                        LEFT JOIN pg_catalog.pg_description pgd
                          ON pgd.objoid = st.relid
                         AND pgd.objsubid = c.ordinal_position
                        WHERE c.table_schema = ?
                          AND c.table_name = ?
                        ORDER BY c.ordinal_position
                        """;
            } else {
                sql = """
                        SELECT column_name,
                               CASE WHEN is_nullable = 'NO' AND column_key <> 'PRI' THEN '1' ELSE NULL END AS is_required,
                               CASE WHEN column_key = 'PRI' THEN '1' ELSE '0' END AS is_pk,
                               ordinal_position AS sort,
                               column_comment,
                               CASE WHEN extra = 'auto_increment' THEN '1' ELSE '0' END AS is_increment,
                               column_type
                        FROM information_schema.columns
                        WHERE table_schema = ?
                          AND table_name = ?
                        ORDER BY ordinal_position
                        """;
            }
            return queryColumns(connection, sql, List.of(schema, tableName));
        });
    }

    public PageResult<GeneratorTable> selectDbTableNotInImport(DbTableQueryDTO queryDTO) {
        return execute(connection -> {
            DatabaseDialect dialect = DatabaseDialect.from(connection);
            String schema = currentSchema(connection, dialect);
            List<Object> params = new ArrayList<>();
            params.add(schema);

            StringBuilder sql = new StringBuilder();
            if (dialect == DatabaseDialect.POSTGRESQL) {
                sql.append("""
                        SELECT *
                        FROM (
                            SELECT c.relname AS table_name,
                                   COALESCE(obj_description(c.oid, 'pg_class'), '') AS table_comment,
                                   '' AS create_time,
                                   '' AS update_time
                            FROM pg_catalog.pg_class c
                            JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
                            WHERE c.relkind = 'r'
                              AND n.nspname = ?
                              AND c.relname NOT LIKE 'generator_%'
                              AND NOT EXISTS (SELECT 1 FROM generator_table gt WHERE gt.table_name = c.relname)
                        ) meta
                        WHERE 1 = 1
                        """);
            } else {
                sql.append("""
                        SELECT *
                        FROM (
                            SELECT t.table_name, t.table_comment, t.create_time, t.update_time
                            FROM information_schema.tables t
                            WHERE t.table_schema = ?
                              AND t.table_name NOT LIKE 'generator_%'
                              AND NOT EXISTS (SELECT 1 FROM generator_table gt WHERE gt.table_name = t.table_name)
                        ) meta
                        WHERE 1 = 1
                        """);
            }

            if (queryDTO.isFilterSys()) {
                sql.append(" AND meta.table_name NOT LIKE 'sys_%'");
            }
            if (queryDTO.getTableName() != null && !queryDTO.getTableName().isEmpty()) {
                sql.append(" AND LOWER(meta.table_name) LIKE LOWER(?)");
                params.add("%" + queryDTO.getTableName() + "%");
            }
            if (queryDTO.getTableComment() != null && !queryDTO.getTableComment().isEmpty()) {
                sql.append(" AND LOWER(meta.table_comment) LIKE LOWER(?)");
                params.add("%" + queryDTO.getTableComment() + "%");
            }
            sql.append(dialect == DatabaseDialect.POSTGRESQL ? " ORDER BY meta.table_name" : " ORDER BY meta.create_time DESC");

            List<GeneratorTable> rows = queryGeneratorTables(connection, sql.toString(), params);
            int total = rows.size();
            int limit = queryDTO.getLimit();
            int page = queryDTO.getPage();
            int fromIndex = Math.max((page - 1) * limit, 0);
            if (fromIndex >= total) {
                return PageUtils.getPageResult(page, limit, Collections.<GeneratorTable>emptyList(), total);
            }
            int toIndex = Math.min(fromIndex + limit, total);
            return PageUtils.getPageResult(page, limit, rows.subList(fromIndex, toIndex), total);
        });
    }

    private static List<TableResult> queryTables(Connection connection, String sql, List<Object> params) throws SQLException {
        List<TableResult> list = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection, sql, params); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                TableResult result = new TableResult();
                result.setTableName(rs.getString("table_name"));
                result.setTableComment(rs.getString("table_comment"));
                result.setCreateTime(rs.getString("create_time"));
                result.setUpdateTime(rs.getString("update_time"));
                list.add(result);
            }
        }
        return list;
    }

    private static List<GeneratorTable> queryGeneratorTables(Connection connection, String sql, List<Object> params) throws SQLException {
        List<GeneratorTable> list = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection, sql, params); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                GeneratorTable result = new GeneratorTable();
                result.setTableName(rs.getString("table_name"));
                result.setTableComment(rs.getString("table_comment"));
                result.setCreateTime(null);
                result.setUpdateTime(null);
                list.add(result);
            }
        }
        return list;
    }

    private static List<TableColumResult> queryColumns(Connection connection, String sql, List<Object> params) throws SQLException {
        List<TableColumResult> list = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection, sql, params); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                TableColumResult result = new TableColumResult();
                result.setColumnName(rs.getString("column_name"));
                result.setIsRequired(rs.getString("is_required"));
                result.setIsPk(rs.getString("is_pk"));
                result.setSort(rs.getInt("sort"));
                result.setColumnComment(rs.getString("column_comment"));
                result.setIsIncrement(rs.getString("is_increment"));
                result.setColumnType(rs.getString("column_type"));
                list.add(result);
            }
        }
        return list;
    }

    private static PreparedStatement prepare(Connection connection, String sql, List<Object> params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }
        return statement;
    }

    private static String currentSchema(Connection connection, DatabaseDialect dialect) throws SQLException {
        if (dialect == DatabaseDialect.POSTGRESQL) {
            String schema = connection.getSchema();
            return Utils.isNotNull(schema) ? schema : "public";
        }
        return connection.getCatalog();
    }

    private static String placeholders(int size) {
        return String.join(", ", Collections.nCopies(size, "?"));
    }

    private <T> T execute(SqlFunction<Connection, T> callback) {
        try (Connection connection = dataSource.getConnection()) {
            return callback.apply(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("读取数据库元数据失败", e);
        }
    }

    @FunctionalInterface
    private interface SqlFunction<T, R> {

        R apply(T value) throws SQLException;
    }

    private enum DatabaseDialect {

        MYSQL, POSTGRESQL;

        private static DatabaseDialect from(Connection connection) throws SQLException {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName().toLowerCase(Locale.ROOT);
            if (productName.contains("postgresql")) {
                return POSTGRESQL;
            }
            if (productName.contains("mysql")) {
                return MYSQL;
            }
            throw new IllegalStateException("代码生成器暂不支持当前数据库: " + productName);
        }
    }
}
