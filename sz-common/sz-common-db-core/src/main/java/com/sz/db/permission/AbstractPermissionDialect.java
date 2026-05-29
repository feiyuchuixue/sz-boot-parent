package com.sz.db.permission;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.sz.core.common.entity.ControlPermissions;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.RoleMenuScopeVO;
import com.sz.core.datascope.ControlThreadLocal;
import com.sz.core.datascope.SimpleDataScopeHelper;
import com.sz.core.util.StringUtils;
import com.sz.core.util.Utils;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限方言抽象基类。
 * <p>
 * 通用流程（开关、登录态、目标表识别、scope 路由）写死在基类；
 * 与方言相关的 SQL 片段构造，由具体子类（如 MysqlPermissionDialect）实现。
 *
 * @author sz
 */
@Slf4j
public abstract class AbstractPermissionDialect extends CommonsDialectImpl {

    protected static final String FIELD_CREATE_ID = "create_id";
    protected static final String FIELD_DEPT_SCOPE = "dept_scope";
    protected static final String SEPARATOR_STR = "$";

    /** 由子类返回当前方言对应的 DbType，供 MybatisFlexConfiguration 注册时使用。 */
    public abstract DbType getDbType();

    /** 由子类决定是否启用数据权限拦截。 */
    protected abstract boolean isDataScopeEnabled();

    /** 由子类返回 logicMinUnit 配置（"user" / "dept"）。 */
    protected abstract String getLogicMinUnit();

    /** 由子类返回是否允许查看管理员数据。 */
    protected abstract boolean isAllowAdminView();

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
            String menuId = permissionMap.get(firstPermission); // 根据权限标识获取菜单ID
            RoleMenuScopeVO scope = menuId == null ? null : scopeMap.get(menuId);
            // 如果没有配置数据权限，默认只看自己的数据
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

    // ============== 由子类实现的"方言相关"方法 ==============

    /**
     * 以"用户"为最小单位时，构造按部门集合过滤的 SQL 片段（通常 EXISTS 子查询）。
     */
    protected abstract String buildUserUnitDeptSql(String table, String field, Collection<Long> deptList);

    /**
     * 以"部门"为最小单位时，构造按部门集合过滤的 SQL 片段（通常 JSON 包含判断）。
     */
    protected abstract String buildDeptUnitSql(String table, String field, Collection<Long> deptList);

    /**
     * 构造允许查看管理员数据的 SQL 片段（通常 EXISTS 子查询）。
     */
    protected abstract String buildAllowAdminViewSql(String table);

    /**
     * 构造按用户集合过滤的 SQL 片段（= 或 IN）。
     */
    protected abstract String buildUserListSql(String table, String userField, Collection<Long> userList);

    // ============== 通用工具（与方言无关，保留在基类） ==============

    private boolean isSkipDataScope(OperateType operateType) {
        return !SimpleDataScopeHelper.isDataScope() || !StpUtil.isLogin() || operateType != OperateType.SELECT;
    }

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
     * 通用编排：调用 4 个子类抽象方法构造完整 SQL，并 where 注入。
     */
    private void buildSql(QueryWrapper queryWrapper, String table, Collection<Long> deptList, Collection<Long> userList, Class<?> tableClazz) {
        String unit = getLogicMinUnit();
        boolean allowAdminView = isAllowAdminView();

        String field = "user".equals(unit) ? FIELD_CREATE_ID : FIELD_DEPT_SCOPE;
        if (!isFieldExists(tableClazz, StringUtils.toCamelCase(field))) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirstAppend = true;

        // 构建用户或部门为单元的 SQL
        if ("user".equals(unit) && !deptList.isEmpty()) { // 以用户为最小单元
            sb.append(buildUserUnitDeptSql(table, field, deptList));
            isFirstAppend = false;
        } else { // 以部门为最小单元
            if (!deptList.isEmpty()) {
                sb.append(buildDeptUnitSql(table, field, deptList));
                isFirstAppend = false;
            }
        }

        // 允许其他用户查看超管产生的数据
        if (allowAdminView) {
            if (!isFirstAppend) {
                sb.append(" OR ");
            }
            sb.append(buildAllowAdminViewSql(table));
            isFirstAppend = false;
        }

        // 自定义用户条件
        if (!userList.isEmpty()) {
            if (!isFirstAppend) {
                sb.append(" OR ");
            }
            sb.append(buildUserListSql(table, FIELD_CREATE_ID, userList));
        }

        // 避免重复拼装
        String fieldFlag = "customScopeContext";
        Object context = CPI.getContext(queryWrapper, fieldFlag);
        if (context == null || Boolean.FALSE.equals(context)) {
            queryWrapper.where("(" + sb + ")");
            CPI.putContext(queryWrapper, fieldFlag, true);
        }
    }

    protected String appendCollection(Collection<Long> collection) {
        return collection.stream().map(String::valueOf).collect(Collectors.joining(", ", "(", ")"));
    }
}
