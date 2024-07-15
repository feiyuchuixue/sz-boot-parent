package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ControlPermissions;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.datascope.ControlThreadLocal;
import com.sz.core.datascope.SimpleDataScopeHelper;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import com.sz.core.util.Utils;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义方言 -- 数据权限
 *
 * @ClassName PermissionDialect
 * @Author sz
 * @Date 2024/6/17 10:36
 * @Version 1.0
 */
@Slf4j
public class SimplePermissionDialect extends CommonsDialectImpl {

    private final static String FIELD_CREATE_ID = "create_id";

    private final static String FIELD_DEPT_SCOPE = "dept_scope";

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        if (!SimpleDataScopeHelper.isDataScope() || !StpUtil.isLogin()) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        try {
            if (operateType != OperateType.SELECT) {
                // TODO check dataScope  ?
                super.prepareAuth(queryWrapper, operateType);
                return;
            }

            Class<?> tableClazz = SimpleDataScopeHelper.get();
            List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
            List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
            if (queryTables == null || queryTables.isEmpty()) {
                return;
            }
            LoginUser loginUser = LoginUtils.getLoginUser();
            if (loginUser == null || !ControlThreadLocal.hasLocal()) {
                super.prepareAuth(queryWrapper, operateType);
                return;
            }

            if (loginUser.getRoles().contains(GlobalConstant.SUPER_ROLE)) { // 管理员，可查看全部
                super.prepareAuth(queryWrapper, operateType);
                return;
            }

            // 对子查询语句进行处理 例如： select count(1) from (select * from table where ...)
            for (QueryTable queryTable : queryTables) {
                if (queryTable instanceof SelectQueryTable) {
                    prepareAuth(((SelectQueryTable) queryTable).getQueryWrapper(), operateType);
                }
            }
            // 判断是否是isJoin
            boolean isJoin = CPI.getJoins(queryWrapper) != null && !CPI.getJoins(queryWrapper).isEmpty();
            Map<String, QueryTable> tableMap = buildTableMap(queryTables, isJoin, joinTables);
            if (tableMap == null) return;

            ControlPermissions permissions = ControlThreadLocal.get();
            String[] btnPermissions = permissions.getPermissions();
            Map<String, String> pamMap = loginUser.getPermissionAndMenuIds();
            Map<String, String> ruleMap = loginUser.getRuleMap();

            String simpleName = tableClazz.getSimpleName(); // eg: TeacherStatics
            String tableName = StringUtils.toSnakeCase(simpleName); // eg: teacher_statics
            QueryTable table = tableMap.get(tableName);
            if (table == null) return;

            // 根据权限规则处理查询
            String rule = determineRule(btnPermissions, pamMap, ruleMap);
            String alias = Utils.isNotNull(table.getAlias()) ? table.getAlias() : table.getName();

            String logicMinUnit = SpringApplicationContextUtils.getBean(DataScopeProperties.class).getLogicMinUnit();
            switch (rule) {
                case "1006001": // 全部 - 放行
                    super.prepareAuth(queryWrapper, operateType);
                    return;
                case "1006002": // 本部门及以下
                    handleDeptScope(queryWrapper, loginUser.getDeptAndChildren(), logicMinUnit, alias, tableClazz);
                    break;
                case "1006003": // 仅本部门
                    handleDeptScope(queryWrapper, loginUser.getDepts(), logicMinUnit, alias, tableClazz);
                    break;
                case "1006004": // 仅本人
                    handlePersonalScope(queryWrapper, loginUser, table, tableClazz);
                    break;
                case "1006005": // 自定义
                    handleCustomScope(queryWrapper, loginUser, logicMinUnit, alias, tableClazz, table);
                    break;
                default: // 未知权限，只能查看自己的数据
                    handlePersonalScope(queryWrapper, loginUser, table, tableClazz);
                    log.warn(" Unknown Rule : {}, Use Personal Scope , permission : {} .", rule, JsonUtils.toJsonString(btnPermissions));
                    break;
            }
        } catch (Exception e) {
            log.error(" PermissionDialect Exception :" + e.getMessage());
            e.printStackTrace();
        } finally {
            super.prepareAuth(queryWrapper, operateType);
        }
    }

    private static Map<String, QueryTable> buildTableMap(List<QueryTable> queryTables, boolean isJoin, List<QueryTable> joinTables) {
        Map<String, QueryTable> tableMap = new HashMap<>();
        for (QueryTable queryTable : queryTables) {
            if (queryTable.getName() == null || ("").equals(queryTable.getName().trim())) {  // TODO 临时方案：如果name为空或空字符串直接return；等待官方修复。忽略非正常结构 queryTables ==[SELECT * FROM TABLE]
                return null;
            }
            tableMap.put(queryTable.getName(), queryTable);
        }
        if (isJoin) {
            for (QueryTable joinTable : joinTables) {
                if (joinTable.getName() != null && !("").equals(joinTable.getName().trim())) {
                    tableMap.put(joinTable.getName(), joinTable);
                }
            }
        }
        return tableMap;
    }

    private String determineRule(String[] btnPermissions, Map<String, String> pamMap, Map<String, String> ruleMap) {
        if (ruleMap.isEmpty()) { // 如果ruleMap为空返回空
            return "";
        }
        if (btnPermissions.length == 1) {
            String btnPermission = btnPermissions[0];
            if (pamMap.containsKey(btnPermission)) {
                String menuId = pamMap.get(btnPermission);
                return ruleMap.get(menuId);
            }
        } else {
            Set<String> mergeMenuIds = new HashSet<>();
            for (String btnPermission : btnPermissions) {
                if (pamMap.containsKey(btnPermission)) {
                    String menuId = pamMap.get(btnPermission);
                    mergeMenuIds.add(menuId);
                }
            }
            if (mergeMenuIds.size() == 1) {
                String menuId = mergeMenuIds.stream().findFirst().orElse(null);
                return ruleMap.get(menuId);
            } else {
                // TODO 针对多个 权限的处理，OR 和 AND 规则。 暂时取第一个
                String menuId = mergeMenuIds.stream().findFirst().orElse(null);
                return ruleMap.get(menuId);
            }
        }
        return "";
    }

    /**
     * 字段有效性校验
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    private boolean isFieldExists(Class<?> clazz, String fieldName) {
        try {
            // 尝试获取类中的字段
            Field field = clazz.getDeclaredField(fieldName);
            // 检查字段是否为null
            if (field != null) {
                return true;
            }
        } catch (NoSuchFieldException e) {
            log.error(" [DataScope]: Entity `{}` Filed `{}` not found.", clazz.getSimpleName(), fieldName);
        }
        return false;
    }

    private void handleDeptScope(QueryWrapper queryWrapper, List<Long> depts, String logicMinUnit, String alias, Class<?> tableClazz) {
        String field;
        Object context;
        if (depts.isEmpty()) {
            return;
        }
        if ("user".equals(logicMinUnit)) {
            field = FIELD_CREATE_ID;
            boolean exists = isFieldExists(tableClazz, StringUtils.toCamelCase(field));
            if (!exists) {
                return;
            }

            String sqlParams = depts.stream().map(String::valueOf).collect(Collectors.joining(", ", "(", ")"));
            String sql = " EXISTS ( SELECT 1 FROM `sys_user_dept` WHERE `sys_user_dept`.`dept_id` IN " + sqlParams + " AND `" + alias + "`.`" + field + "` = `sys_user_dept`.`user_id` )";
            String sqlSuper = " EXISTS ( SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = `" + alias + "`.`" + field + "` AND  `sys_user`.`user_tag_cd` = '1001002' AND `del_flag` = 'F' )";// 管理员Id查询
            context = CPI.getContext(queryWrapper, field);
            if (!Boolean.TRUE.equals(context)) {
                queryWrapper.where(sql);
                queryWrapper.or(sqlSuper);
                CPI.putContext(queryWrapper, field, true);
            }
        } else {
            field = FIELD_DEPT_SCOPE;
            context = CPI.getContext(queryWrapper, field);
            if (!Boolean.TRUE.equals(context)) {
                depts.forEach(dept -> {
                    String sql = "JSON_CONTAINS(" + alias + "." + field + ", '" + dept + "', '$')";
                    queryWrapper.or(sql);
                });
                CPI.putContext(queryWrapper, field, true);
            }
        }
    }

    private void handlePersonalScope(QueryWrapper queryWrapper, LoginUser loginUser, QueryTable table, Class<?> tableClazz) {
        String field = FIELD_CREATE_ID;
        boolean exists = isFieldExists(tableClazz, StringUtils.toCamelCase(field));
        if (!exists) {
            return;
        }
        QueryCondition queryCondition = QueryCondition.create(
                new QueryColumn(table.getSchema(), table.getName(), field, table.getAlias()),
                SqlConsts.EQUALS,
                loginUser.getUserInfo().getId()
        );
        Object context = CPI.getContext(queryWrapper, field);
        if (!Boolean.TRUE.equals(context)) {
            queryWrapper.where(queryCondition);
            CPI.putContext(queryWrapper, field, true);
        }
    }

    private void handleCustomScope(QueryWrapper queryWrapper, LoginUser loginUser, String logicMinUnit, String alias, Class<?> tableClazz, QueryTable table) {
        String field;
        List<Long> customDeptIds = loginUser.getCustomDeptIds();
        List<Long> customUserIds = loginUser.getCustomUserIds();

        // 部门维度查询
        if (!customDeptIds.isEmpty()) {
            handleDeptScope(queryWrapper, customDeptIds, logicMinUnit, alias, tableClazz);
        }

        // 用户维度查询
        if (!customUserIds.isEmpty()) {
            field = FIELD_CREATE_ID;
            QueryCondition queryCondition = QueryCondition.create(
                    new QueryColumn(table.getSchema(), table.getName(), field, table.getAlias()),
                    SqlConsts.IN,
                    customUserIds);
            Object contextUser = CPI.getContext(queryWrapper, field + "_custom");
            if (!Boolean.TRUE.equals(contextUser)) {
                queryWrapper.or(queryCondition);
                CPI.putContext(queryWrapper, field + "_custom", true);
            }
        }

    }

}
