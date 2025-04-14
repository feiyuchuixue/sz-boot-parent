package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.sz.core.common.entity.ControlPermissions;
import com.sz.core.common.entity.LoginUser;
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
        if (isAuthorizedToHandleDataScope(operateType)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        try {

            if (!initializeContext(queryWrapper, operateType)) {
                return;
            }

            ControlPermissions permissions = ControlThreadLocal.get();
            LoginUser loginUser = LoginUtils.getLoginUser();

            assert loginUser != null;
            String[] btnPermissions = permissions.getPermissions();
            Map<String, String> permissionMap = loginUser.getPermissionAndMenuIds();
            Map<String, String> ruleMap = loginUser.getRuleMap();
            String mode = permissions.getMode();

            String rule = determineRuleScope(btnPermissions, permissionMap, ruleMap, mode);
            String table = getTable(SimpleDataScopeHelper.get());

            applyDataScopeRules(queryWrapper, operateType, rule, table, SimpleDataScopeHelper.get());

        } catch (Exception e) {
            log.error("PermissionDialect Exception: {}", e.getMessage());
        } finally {
            super.prepareAuth(queryWrapper, operateType);
        }
    }

    /**
     * 检查是否需要进行数据权限控制
     */
    private boolean isAuthorizedToHandleDataScope(OperateType operateType) {
        return SimpleDataScopeHelper.isDataScope() && StpUtil.isLogin() && operateType == OperateType.SELECT;
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

    private String getTable(Class<?> tableClazz) {
        Table tableClazzAnnotation = tableClazz.getAnnotation(Table.class);
        if (tableClazzAnnotation == null) {
            String simpleName = tableClazz.getSimpleName();
            return StringUtils.toSnakeCase(simpleName);
        } else {
            return tableClazzAnnotation.value();
        }
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
        ControlPermissions permissions = ControlThreadLocal.get();
        Map<String, Set<Long>> userRuleMap = loginUser.getUserRuleMap();
        Map<String, Set<Long>> deptRuleMap = loginUser.getDeptRuleMap();
        Set<Long> customUserIds = determineCustomRuleRelationIds(permissions.getPermissions(), loginUser.getPermissionAndMenuIds(), userRuleMap,
                permissions.getMode());
        Set<Long> customDeptIds = determineCustomRuleRelationIds(permissions.getPermissions(), loginUser.getPermissionAndMenuIds(), deptRuleMap,
                permissions.getMode());

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

        // 添加自定义部门和用户
        deptList.addAll(customDeptIds);
        userList.addAll(customUserIds);

        // 添加当前操作用户
        userList.add(loginUser.getUserInfo().getId());
        buildSql(queryWrapper, table, deptList, userList, tableClazz);

    }

    private String determineRuleScope(String[] permissionKeys, Map<String, String> permissionAccessMap, Map<String, String> ruleScopeMap, String mode) {
        if (ruleScopeMap.isEmpty()) {
            return "";
        }
        Set<String> menuIds = Arrays.stream(permissionKeys).filter(permissionAccessMap::containsKey).map(permissionAccessMap::get).collect(Collectors.toSet());
        if (menuIds.isEmpty()) {
            return "";
        }
        if (menuIds.size() == 1) {
            return ruleScopeMap.getOrDefault(menuIds.iterator().next(), "");
        }
        if (mode.isEmpty()) {
            return "";
        }
        return menuIds.stream().map(menuId -> ruleScopeMap.getOrDefault(menuId, ""))
                .reduce((scope1, scope2) -> mode.equals("or") ? maxRuleScope(scope1, scope2) : minRuleScope(scope1, scope2)).orElse("");
    }

    private Set<Long> determineCustomRuleRelationIds(String[] permissionKeys, Map<String, String> permissionAccessMap, Map<String, Set<Long>> ruleRelation,
            String mode) {
        Set<Long> relationId = new HashSet<>();
        if (ruleRelation.isEmpty()) {
            return relationId;
        }
        Set<String> menuIds = Arrays.stream(permissionKeys).filter(permissionAccessMap::containsKey).map(permissionAccessMap::get).collect(Collectors.toSet());
        if (menuIds.isEmpty()) {
            return relationId;
        }
        if (menuIds.size() == 1) {
            return ruleRelation.getOrDefault(menuIds.iterator().next(), relationId);
        }
        if (mode.isEmpty()) {
            return relationId;
        }
        return menuIds.stream().map(menuId -> ruleRelation.getOrDefault(menuId, relationId))
                .reduce((relation1, relation2) -> mode.equals("or") ? maxRelation(relation1, relation2) : minRelation(relation1, relation2)).orElse(relationId);
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

    private String maxRuleScope(String scope1, String scope2) {
        return Utils.getLongVal(scope1) >= Utils.getLongVal(scope2) ? scope1 : scope2;
    }

    private String minRuleScope(String scope1, String scope2) {
        return Utils.getLongVal(scope1) <= Utils.getLongVal(scope2) ? scope1 : scope2;
    }

    private Set<Long> maxRelation(Set<Long> relation1, Set<Long> relation2) {
        Set<Long> all = new HashSet<>(relation1);
        all.addAll(relation2);
        return all;
    }

    private Set<Long> minRelation(Set<Long> relation1, Set<Long> relation2) {
        Set<Long> intersection = new HashSet<>(relation1);
        intersection.retainAll(relation2);
        return intersection;
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
            sb.append(" EXISTS ( SELECT 1 FROM `sys_user_dept` WHERE `sys_user_dept`.`dept_id` IN ").append(sqlParams).append(" AND `").append(table)
                    .append("`.`").append(field).append("` = `sys_user_dept`.`user_id`)");
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
            queryWrapper.where(sb.toString());
            CPI.putContext(queryWrapper, fieldFlag, true);
        }
    }

    private String appendCollection(Collection<Long> collection) {
        return collection.stream().map(String::valueOf).collect(Collectors.joining(", ", "(", ")"));
    }
}