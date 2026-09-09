package com.sz.db.postgresql;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.db.DataScopeProperties;
import com.sz.db.permission.AbstractPermissionDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * PostgreSQL 数据权限方言实现。
 * <p>
 * 使用 PostgreSQL 专属语法：双引号包裹标识符、BIGINT[] && 操作符等（PostgreSQL 9.4+）。
 * <p>
 * dept_scope 字段在 PG 中存储为 BIGINT[]，使用 && 操作符判断数组交集（等价于 MySQL JSON_OVERLAPS）。
 * 索引建法：CREATE INDEX idx_dept_scope ON xxx USING GIN (dept_scope);
 *
 * @since Phase D
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "org.postgresql.Driver")
public class PostgresqlPermissionDialect extends AbstractPermissionDialect {

    /**
     * 显式声明 PG 方言所需的标识符包装方式（双引号）和 LIMIT/OFFSET 处理器， 与 MybatisFlex 内置 PG
     * 方言保持一致，避免外层包装 SQL（如分页 COUNT）误用 MySQL 反引号语法。
     */
    public PostgresqlPermissionDialect() {
        super(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.POSTGRESQL);
    }

    @Override
    public DbType getDbType() {
        return DbType.POSTGRE_SQL;
    }

    @Override
    protected boolean isDataScopeEnabled() {
        return getProps().isEnabled();
    }

    @Override
    protected String getLogicMinUnit() {
        return getProps().getLogicMinUnit();
    }

    @Override
    protected boolean isAllowAdminView() {
        return getProps().isAllowAdminView();
    }

    @Override
    protected String buildUserUnitDeptSql(String table, String field, Collection<Long> deptList) {
        String sqlParams = appendCollection(deptList);
        return new StringBuilder().append(" EXISTS ( SELECT 1 FROM \"sys_user_dept\" ")
                .append("JOIN \"sys_dept\" ON \"sys_user_dept\".\"dept_id\" = \"sys_dept\".\"id\" ").append("WHERE \"sys_user_dept\".\"dept_id\" IN ")
                .append(sqlParams).append(" AND \"sys_dept\".\"del_flag\" = 'F' ").append("AND \"").append(table).append("\".\"").append(field)
                .append("\" = \"sys_user_dept\".\"user_id\") ").toString();
    }

    @Override
    protected String buildDeptUnitSql(String table, String field, Collection<Long> deptList) {
        // 使用 BIGINT[] && 操作符，判断 dept_scope 数组与目标部门集合是否有交集
        // 索引建法：CREATE INDEX idx_dept_scope ON xxx USING GIN (dept_scope)
        String pgArray = deptList.stream().map(String::valueOf).collect(Collectors.joining(",", "ARRAY[", "]::BIGINT[]"));
        return " \"" + table + "\".\"" + field + "\" && " + pgArray;
    }

    @Override
    protected String buildAllowAdminViewSql(String table) {
        return new StringBuilder().append(" EXISTS (SELECT 1 FROM \"sys_user\" WHERE \"sys_user\".\"id\" = \"").append(table).append("\".\"")
                .append(FIELD_CREATE_ID).append("\" AND \"sys_user\".\"user_tag_cd\" = '1001002' AND \"del_flag\" = 'F')").toString();
    }

    @Override
    protected String buildExcludeAdminSql(String table) {
        return new StringBuilder().append(" NOT EXISTS (SELECT 1 FROM \"sys_user\" WHERE \"sys_user\".\"id\" = \"").append(table).append("\".\"")
                .append(FIELD_CREATE_ID).append("\" AND \"sys_user\".\"user_tag_cd\" = '1001002' AND \"del_flag\" = 'F')").toString();
    }

    @Override
    protected String buildUserListSql(String table, String userField, Collection<Long> userList) {
        if (userList.size() == 1) {
            return " \"" + table + "\".\"" + userField + "\" = " + userList.iterator().next();
        }
        return " \"" + table + "\".\"" + userField + "\" IN " + appendCollection(userList);
    }

    private DataScopeProperties getProps() {
        return SpringApplicationContextUtils.getInstance().getBean(DataScopeProperties.class);
    }
}
