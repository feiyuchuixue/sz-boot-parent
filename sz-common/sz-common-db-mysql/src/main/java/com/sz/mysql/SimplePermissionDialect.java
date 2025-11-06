package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.sz.core.common.entity.ControlPermissions;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.RoleMenuScopeVO;
import com.sz.core.datascope.ControlThreadLocal;
import com.sz.core.datascope.SimpleDataScopeHelper;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import com.sz.core.util.Utils;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义方言 -- 数据权限
 */
@Slf4j
public class SimplePermissionDialect extends CommonsDialectImpl {

    private static final String FIELD_CREATE_ID = "create_id";

    private static final String FIELD_DEPT_SCOPE = "dept_scope";

    private static final String SEPARATOR_STR = "$"; // 分隔符常量

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        // 需要跳过的情况
        if (isSkipDataScope(operateType)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        // 防止误触 && MybatisFlex Relation 多对多映射问题
        String tableName = getTableName(SimpleDataScopeHelper.get());
        if (!isTargetTable(queryWrapper, tableName)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }

        try {
            if (!initializeContext(queryWrapper, operateType)) {
                return;
            }

            ControlPermissions permissions = ControlThreadLocal.get();
            LoginUser loginUser = LoginUtils.getLoginUser();
            if (permissions == null || loginUser == null) {
                log.error("PermissionDialect prepareAuth error: permissions or loginUser is null.");
                return;
            }
            String[] btnPermissions = permissions.getPermissions();
            Map<String, String> permissionMap = loginUser.getPermissionAndMenuIds();
            Map<String, RoleMenuScopeVO> scopeMap = loginUser.getDataScope();
            String firstPermission = (btnPermissions != null && btnPermissions.length > 0) ? btnPermissions[0] : "";
            String menuId = permissionMap.get(firstPermission); // 根据 权限标识获取菜单ID
            RoleMenuScopeVO scope = menuId == null ? null : scopeMap.get(menuId);
            // ！如果没有配置数据权限，默认只看自己的数据
            if (scope == null || permissionMap.isEmpty()) {
                Set<Long> userIds = new HashSet<>();
                Set<Long> deptIds = new HashSet<>();
                // 添加当前操作用户
                userIds.add(loginUser.getUserInfo().getId());
                buildSql(queryWrapper, tableName, deptIds, userIds, SimpleDataScopeHelper.get());
                return;
            }

            String dataScopeCd = scope.getDataScopeCd();
            switch (dataScopeCd) {
                case "1006005" -> { // 自定义
                    RoleMenuScopeVO.CustomScope customScope = scope.getCustomScope();
                    Set<Long> deptIds = new HashSet<>();
                    Set<Long> userIds = new HashSet<>();
                    if (customScope != null) {
                        if (Utils.isNotNull(customScope.getDeptIds())) {
                            deptIds.addAll(customScope.getDeptIds());
                        }
                        if (Utils.isNotNull(customScope.getUserIds())) {
                            userIds.addAll(customScope.getUserIds());
                        }
                    }
                    // 添加当前操作用户
                    userIds.add(loginUser.getUserInfo().getId());
                    buildSql(queryWrapper, tableName, deptIds, userIds, SimpleDataScopeHelper.get());
                }
                case "1006001", "1006002", "1006003", "1006004" -> {
                    applyDataScopeRules(queryWrapper, operateType, dataScopeCd, tableName, SimpleDataScopeHelper.get());
                }
            }

        } catch (Exception e) {
            log.error("PermissionDialect Exception: {}", e.getMessage());
        } finally {
            super.prepareAuth(queryWrapper, operateType);
        }
    }

    /**
     * 检查是否跳过数据权限控制
     */
    private boolean isSkipDataScope(OperateType operateType) {
        return !SimpleDataScopeHelper.isDataScope() || !StpUtil.isLogin() || operateType != OperateType.SELECT;
    }

    /**
     * 验证当前查询是否是目标表
     *
     * @param queryWrapper
     *            wrapper
     * @param table
     *            表名称
     * @return boolean
     */
    private boolean isTargetTable(QueryWrapper queryWrapper, String table) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return false;
        }
        for (QueryTable queryTable : queryTables) {
            if (table.equals(queryTable.getName())) {
                return true;
            }
            if (queryTable instanceof SelectQueryTable selectQueryTable) {
                if (isTargetTable(selectQueryTable.getQueryWrapper(), table)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 初始化上下文
     *
     * @param queryWrapper
     *            wrapper
     * @param operateType
     *            操作类型
     * @return 是否初始化成功
     */
    private boolean initializeContext(QueryWrapper queryWrapper, OperateType operateType) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return false;
        }
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null || !ControlThreadLocal.hasLocal() || LoginUtils.isSuperAdmin()) {
            super.prepareAuth(queryWrapper, operateType);
            return false;
        }
        for (QueryTable queryTable : queryTables) {
            if (queryTable instanceof SelectQueryTable) {
                prepareAuth(((SelectQueryTable) queryTable).getQueryWrapper(), operateType);
            }
        }

        boolean isJoin = CPI.getJoins(queryWrapper) != null && !CPI.getJoins(queryWrapper).isEmpty();
        Map<String, QueryTable> tableMap = buildTableMap(queryTables, isJoin, joinTables);

        return !tableMap.isEmpty();
    }

    private static Map<String, QueryTable> buildTableMap(List<QueryTable> queryTables, boolean isJoin, List<QueryTable> joinTables) {
        Map<String, QueryTable> tableMap = new HashMap<>();
        for (QueryTable queryTable : queryTables) {
            if (queryTable.getName() == null || queryTable.getName().trim().isEmpty()) {
                return Collections.emptyMap();
            }
            tableMap.put(queryTable.getName(), queryTable);
        }
        if (isJoin) {
            for (QueryTable joinTable : joinTables) {
                if (joinTable.getName() != null && !joinTable.getName().trim().isEmpty()) {
                    tableMap.put(joinTable.getName(), joinTable);
                }
            }
        }
        return tableMap;
    }

    private String getTableName(Class<?> clazz) {
        Table anno = clazz.getAnnotation(Table.class);
        return (anno == null) ? StringUtils.toSnakeCase(clazz.getSimpleName()) : anno.value();
    }

    /**
     * 应用数据权限规则
     *
     * @param queryWrapper
     *            wrapper
     * @param operateType
     *            类型
     * @param rule
     *            规则
     * @param table
     *            table表
     * @param tableClazz
     *            class
     */
    private void applyDataScopeRules(QueryWrapper queryWrapper, OperateType operateType, String rule, String table, Class<?> tableClazz) {
        LoginUser loginUser = LoginUtils.getLoginUser();
        assert loginUser != null;
        // 如果有全部数据的查询权限，直接返回
        if ("1006001".equals(rule)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        // 初始化部门和用户集合
        Set<Long> deptList = new HashSet<>();
        Set<Long> userList = new HashSet<>();
        // 根据规则添加部门
        switch (rule) {
            case "1006002" -> deptList.addAll(loginUser.getDeptAndChildren()); // 本部门及以下
            case "1006003" -> deptList.addAll(loginUser.getDepts()); // 仅本部门
        }
        // 添加当前操作用户
        userList.add(loginUser.getUserInfo().getId());
        buildSql(queryWrapper, table, deptList, userList, tableClazz);
    }

    private boolean isFieldExists(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            log.error(" [DataScope]: Entity `{}` Filed `{}` not found.", clazz.getSimpleName(), fieldName);
        }
        return false;
    }

    /**
     * 根据条件拼装sql
     *
     * @param queryWrapper
     *            wrapper
     * @param table
     *            表名
     * @param deptList
     *            部门集合
     * @param userList
     *            用户集合
     * @param tableClazz
     *            class
     */
    private void buildSql(QueryWrapper queryWrapper, String table, Collection<Long> deptList, Collection<Long> userList, Class<?> tableClazz) {
        DataScopeProperties properties = SpringApplicationContextUtils.getInstance().getBean(DataScopeProperties.class);
        String unit = properties.getLogicMinUnit();
        boolean allowAdminView = properties.isAllowAdminView();

        String field = "user".equals(unit) ? FIELD_CREATE_ID : FIELD_DEPT_SCOPE;
        if (!isFieldExists(tableClazz, StringUtils.toCamelCase(field))) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirstAppend = true;

        // 构建用户或部门为单元的SQL
        if ("user".equals(unit) && !deptList.isEmpty()) { // 以用户为最小单元
            String sqlParams = appendCollection(deptList);
            sb.append(" EXISTS ( SELECT 1 FROM `sys_user_dept` ").append("JOIN `sys_dept` ON `sys_user_dept`.`dept_id` = `sys_dept`.`id` ")
                    .append("WHERE `sys_user_dept`.`dept_id` IN ").append(sqlParams).append(" AND `sys_dept`.`del_flag` = 'F' ").append("AND `").append(table)
                    .append("`.`").append(field).append("` = `sys_user_dept`.`user_id`) ");
            isFirstAppend = false;
        } else { // 以部门为最小单元
            if (!deptList.isEmpty()) {
                for (Long dept : deptList) {
                    if (!isFirstAppend) {
                        sb.append(" OR ");
                    }
                    sb.append(" JSON_CONTAINS(").append("`").append(table).append("`").append(".").append("`").append(field).append("`").append(", '")
                            .append(dept).append("', '").append(SEPARATOR_STR).append("')");
                    isFirstAppend = false;
                }
            }
        }

        // 允许其他用户查看超管产生的数据
        if (allowAdminView) {
            if (!isFirstAppend) {
                sb.append(" OR ");
            }
            sb.append(" EXISTS (SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = `").append(table).append("`.`").append(FIELD_CREATE_ID)
                    .append("` AND `sys_user`.`user_tag_cd` = '1001002' AND `del_flag` = 'F')");
            isFirstAppend = false;

        }

        // 自定义用户条件
        if (!userList.isEmpty()) {
            if (!isFirstAppend)
                sb.append(" OR ");
            if (userList.size() == 1) {
                sb.append(" `").append(table).append("`.`").append(FIELD_CREATE_ID).append("` = ").append(userList.iterator().next());
            } else {
                sb.append(" `").append(table).append("`.`").append(FIELD_CREATE_ID).append("` IN ").append(appendCollection(userList));
            }
        }

        // 避免重复拼装
        String fieldFlag = "customScopeContext";
        Object context = CPI.getContext(queryWrapper, fieldFlag);
        if (context == null || Boolean.FALSE.equals(context)) {
            queryWrapper.where("(" + sb + ")");
            CPI.putContext(queryWrapper, fieldFlag, true);
        }
    }

    private String appendCollection(Collection<Long> collection) {
        return collection.stream().map(String::valueOf).collect(Collectors.joining(", ", "(", ")"));
    }
}