package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.constant.SqlConsts;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 自定义方言 -- 数据权限
 */
@Slf4j
public class SimplePermissionDialect extends CommonsDialectImpl {

    private static final String FIELD_CREATE_ID = "create_id";

    private static final String FIELD_DEPT_SCOPE = "dept_scope";

    // 定义一个分隔符常量
    private static final String SEPARATOR_STR = "$";

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        if (!SimpleDataScopeHelper.isDataScope() || !StpUtil.isLogin()) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        try {
            if (operateType != OperateType.SELECT) {
                super.prepareAuth(queryWrapper, operateType);
                return;
            }

            if (!initializeContext(queryWrapper, operateType)) {
                return;
            }

            ControlPermissions permissions = ControlThreadLocal.get();
            LoginUser loginUser = LoginUtils.getLoginUser();

            String rule = determineRuleScope(permissions, loginUser);
            String alias = getAlias(SimpleDataScopeHelper.get());

            handleRule(queryWrapper, operateType, rule, alias, SimpleDataScopeHelper.get());

            handleCustomLogic(queryWrapper, loginUser, permissions, alias, SimpleDataScopeHelper.get());

        } catch (Exception e) {
            log.error("PermissionDialect Exception: {}", e.getMessage());
        } finally {
            super.prepareAuth(queryWrapper, operateType);
        }
    }

    private boolean initializeContext(QueryWrapper queryWrapper, OperateType operateType) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);

        if (queryTables == null || queryTables.isEmpty()) {
            return false;
        }

        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null || !ControlThreadLocal.hasLocal()) {
            super.prepareAuth(queryWrapper, operateType);
            return false;
        }

        if (LoginUtils.isSuperAdmin()) {
            super.prepareAuth(queryWrapper, operateType);
            return false;
        }

        processSubQueries(queryTables, operateType);

        boolean isJoin = CPI.getJoins(queryWrapper) != null && !CPI.getJoins(queryWrapper).isEmpty();
        Map<String, QueryTable> tableMap = buildTableMap(queryTables, isJoin, joinTables);
        return tableMap != null && !tableMap.isEmpty();
    }

    private String determineRuleScope(ControlPermissions permissions, LoginUser loginUser) {
        String[] btnPermissions = permissions.getPermissions();
        Map<String, String> permissionMap = loginUser.getPermissionAndMenuIds();
        Map<String, String> ruleMap = loginUser.getRuleMap();
        String mode = permissions.getMode();

        return determineRuleScope(btnPermissions, permissionMap, ruleMap, mode);
    }

    private String getAlias(Class<?> tableClazz) {
        Table tableClazzAnnotation = tableClazz.getAnnotation(Table.class);
        if (tableClazzAnnotation == null) {
            String simpleName = tableClazz.getSimpleName();
            return StringUtils.toSnakeCase(simpleName);
        } else {
            return tableClazzAnnotation.value();
        }
    }

    private void handleRule(QueryWrapper queryWrapper, OperateType operateType, String rule, String alias, Class<?> tableClazz) {
        LoginUser loginUser = LoginUtils.getLoginUser();
        String logicMinUnit = SpringApplicationContextUtils.getInstance().getBean(DataScopeProperties.class).getLogicMinUnit();
        switch (rule) {
            case "1006001" :
                super.prepareAuth(queryWrapper, operateType);
                return;
            case "1006002" :
                handleDeptScope(queryWrapper, loginUser.getDeptAndChildren(), logicMinUnit, alias, tableClazz);
                break;
            case "1006003" :
                handleDeptScope(queryWrapper, loginUser.getDepts(), logicMinUnit, alias, tableClazz);
                break;
            case "1006004" :
            default :
                handlePersonalScope(queryWrapper, loginUser, alias, tableClazz);
                break;
        }
    }

    private void handleCustomLogic(QueryWrapper queryWrapper, LoginUser loginUser, ControlPermissions permissions, String alias, Class<?> tableClazz) {
        String key = "customRuleContext";
        Object context = CPI.getContext(queryWrapper, key);

        if (!Boolean.TRUE.equals(context)) {
            Map<String, Set<Long>> userRuleMap = loginUser.getUserRuleMap();
            Map<String, Set<Long>> deptRuleMap = loginUser.getDeptRuleMap();
            Set<Long> customUserIds = determineCustomRuleRelationIds(permissions.getPermissions(), loginUser.getPermissionAndMenuIds(), userRuleMap,
                    permissions.getMode());
            Set<Long> customDeptIds = determineCustomRuleRelationIds(permissions.getPermissions(), loginUser.getPermissionAndMenuIds(), deptRuleMap,
                    permissions.getMode());

            Consumer<QueryWrapper> queryHandler = getQueryHandler(userRuleMap, deptRuleMap, customUserIds, customDeptIds, alias, tableClazz);
            if (queryHandler != null) {
                queryWrapper.and(queryHandler);
                CPI.putContext(queryWrapper, key, true);
            }
        }
    }

    private Consumer<QueryWrapper> getQueryHandler(Map<String, Set<Long>> userRuleMap, Map<String, Set<Long>> deptRuleMap, Set<Long> customUserIds,
            Set<Long> customDeptIds, String alias, Class<?> tableClazz) {
        if (!userRuleMap.isEmpty() && !deptRuleMap.isEmpty()) {
            return wrapper -> wrapper.and(handleCustomUserRelation(customUserIds)).or(handleCustomDeptRelation(customDeptIds, alias, tableClazz));
        } else if (!userRuleMap.isEmpty()) {
            return wrapper -> wrapper.and(handleCustomUserRelation(customUserIds));
        } else if (!deptRuleMap.isEmpty()) {
            return wrapper -> wrapper.and(handleCustomDeptRelation(customDeptIds, alias, tableClazz));
        }
        return null;
    }

    private void processSubQueries(List<QueryTable> queryTables, OperateType operateType) {
        for (QueryTable queryTable : queryTables) {
            if (queryTable instanceof SelectQueryTable) {
                prepareAuth(((SelectQueryTable) queryTable).getQueryWrapper(), operateType);
            }
        }
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

    private void handleDeptScope(QueryWrapper queryWrapper, Collection<Long> depts, String logicMinUnit, String alias, Class<?> tableClazz) {
        if (depts.isEmpty()) {
            return;
        }
        if ("user".equals(logicMinUnit)) {
            handleUserDeptScope(queryWrapper, depts, alias, tableClazz);
        } else {
            handleDeptScope(queryWrapper, depts, alias);
        }
    }

    private void handleUserDeptScope(QueryWrapper queryWrapper, Collection<Long> depts, String alias, Class<?> tableClazz) {
        String field = FIELD_CREATE_ID;
        if (!isFieldExists(tableClazz, StringUtils.toCamelCase(field))) {
            return;
        }

        String sqlParams = depts.stream().map(String::valueOf).collect(Collectors.joining(", ", "(", ")"));
        String sql = " EXISTS ( SELECT 1 FROM `sys_user_dept` WHERE `sys_user_dept`.`dept_id` IN " + sqlParams + " AND `" + alias + "`.`" + field
                + "` = `sys_user_dept`.`user_id` )";
        String sqlSuper = " OR  EXISTS ( SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = `" + alias + "`.`" + field
                + "` AND  `sys_user`.`user_tag_cd` = '1001002' AND `del_flag` = 'F' )";
        if (!Boolean.TRUE.equals(CPI.getContext(queryWrapper, field))) {
            queryWrapper.and(sql + sqlSuper);
            CPI.putContext(queryWrapper, field, true);
        }
    }

    private void handleDeptScope(QueryWrapper queryWrapper, Collection<Long> depts, String alias) {
        String field = FIELD_DEPT_SCOPE;
        if (!Boolean.TRUE.equals(CPI.getContext(queryWrapper, field))) {
            StringBuilder append = new StringBuilder();
            boolean isFirst = true;
            for (Long dept : depts) {
                if (!isFirst) {
                    append.append(" OR JSON_CONTAINS(").append(alias).append(".").append(field).append(", '").append(dept).append("', '").append(SEPARATOR_STR)
                            .append("')");
                } else {
                    isFirst = false;
                    append.append("JSON_CONTAINS(").append(alias).append(".").append(field).append(", '").append(dept).append("', '").append(SEPARATOR_STR)
                            .append("')");
                }
            }
            queryWrapper.and(append.toString());
            CPI.putContext(queryWrapper, field, true);
        }
    }

    private void handlePersonalScope(QueryWrapper queryWrapper, LoginUser loginUser, String alias, Class<?> tableClazz) {
        String field = FIELD_CREATE_ID;
        if (!isFieldExists(tableClazz, StringUtils.toCamelCase(field)) || !loginUser.getUserRuleMap().isEmpty()) {
            return;
        }
        QueryCondition queryCondition = QueryCondition.create(new QueryColumn(null, null, field, alias), SqlConsts.EQUALS, loginUser.getUserInfo().getId());
        if (!Boolean.TRUE.equals(CPI.getContext(queryWrapper, field))) {
            queryWrapper.where(queryCondition);
            CPI.putContext(queryWrapper, field, true);
        }
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

    private QueryCondition handleCustomUserRelation(Collection<Long> customUserIds) {
        if (customUserIds.isEmpty()) {
            return null;
        }
        return QueryCondition.create(new QueryColumn(null, null, FIELD_CREATE_ID, null), SqlConsts.IN, customUserIds);
    }

    private String handleCustomDeptRelation(Collection<Long> depts, String alias, Class<?> tableClazz) {
        if (depts.isEmpty()) {
            return "";
        }
        String field = FIELD_DEPT_SCOPE;
        if (!isFieldExists(tableClazz, StringUtils.toCamelCase(field))) {
            return "";
        }
        StringBuilder append = new StringBuilder();
        boolean isFirst = true;
        for (Long dept : depts) {
            if (!isFirst) {
                append.append(" OR JSON_CONTAINS(").append(alias).append(".").append(field).append(", '").append(dept).append("', '$')");
            } else {
                isFirst = false;
                append.append("JSON_CONTAINS(").append(alias).append(".").append(field).append(", '").append(dept).append("', '$')");
            }
        }
        return append.toString();
    }
}