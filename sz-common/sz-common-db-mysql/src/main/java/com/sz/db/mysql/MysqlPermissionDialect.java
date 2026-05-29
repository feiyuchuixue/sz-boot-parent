package com.sz.db.mysql;

import com.mybatisflex.core.dialect.DbType;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.db.DataScopeProperties;
import com.sz.db.permission.AbstractPermissionDialect;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * MySQL 数据权限方言实现。
 * <p>
 * 使用 MySQL 专属语法：反引号包标识符、JSON_OVERLAPS 函数等（MySQL 8.0.17+）。
 *
 * @since Phase B
 */
@Configuration
public class MysqlPermissionDialect extends AbstractPermissionDialect {

    @Override
    public DbType getDbType() {
        return DbType.MYSQL;
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
        return new StringBuilder().append(" EXISTS ( SELECT 1 FROM `sys_user_dept` ").append("JOIN `sys_dept` ON `sys_user_dept`.`dept_id` = `sys_dept`.`id` ")
                .append("WHERE `sys_user_dept`.`dept_id` IN ").append(sqlParams).append(" AND `sys_dept`.`del_flag` = 'F' ").append("AND `").append(table)
                .append("`.`").append(field).append("` = `sys_user_dept`.`user_id`) ").toString();
    }

    @Override
    protected String buildDeptUnitSql(String table, String field, Collection<Long> deptList) {
        // 使用 JSON_OVERLAPS 替代多个 JSON_CONTAINS OR，可命中多值索引（MySQL 8.0.17+）
        // 索引建法：ALTER TABLE xxx ADD INDEX idx_dept_scope ((CAST(dept_scope AS UNSIGNED
        // ARRAY)));
        String jsonArray = deptList.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
        return " JSON_OVERLAPS(`" + table + "`.`" + field + "`, '" + jsonArray + "')";
    }

    @Override
    protected String buildAllowAdminViewSql(String table) {
        return new StringBuilder().append(" EXISTS (SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = `").append(table).append("`.`").append(FIELD_CREATE_ID)
                .append("` AND `sys_user`.`user_tag_cd` = '1001002' AND `del_flag` = 'F')").toString();
    }

    @Override
    protected String buildExcludeAdminSql(String table) {
        return new StringBuilder().append(" NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = `").append(table).append("`.`").append(FIELD_CREATE_ID)
                .append("` AND `sys_user`.`user_tag_cd` = '1001002' AND `del_flag` = 'F')").toString();
    }

    @Override
    protected String buildUserListSql(String table, String userField, Collection<Long> userList) {
        if (userList.size() == 1) {
            return " `" + table + "`.`" + userField + "` = " + userList.iterator().next();
        }
        return " `" + table + "`.`" + userField + "` IN " + appendCollection(userList);
    }

    private DataScopeProperties getProps() {
        return SpringApplicationContextUtils.getInstance().getBean(DataScopeProperties.class);
    }
}
