package com.sz.db.permission;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
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
 * 通用流程（开关、登录态、目标表识别、scope 路由）写死在基类； 与方言相关的 SQL 片段构造，由具体子类（如
 * MysqlPermissionDialect）实现。
 *
 * @author sz
 */
@Slf4j
public abstract class AbstractPermissionDialect extends CommonsDialectImpl {

    protected static final String FIELD_CREATE_ID = "create_id";

    protected static final String FIELD_DEPT_SCOPE = "dept_scope";

    /**
     * 默认构造器：使用 MybatisFlex CommonsDialectImpl 默认配置（反引号 + MySQL LIMIT）， 适用于 MySQL
     * 方言子类。
     */
    protected AbstractPermissionDialect() {
        super();
    }

    /**
     * 带参构造器：允许子类指定标识符包装方式与 LIMIT/OFFSET 处理器， 用于非 MySQL 数据库（如 PG 使用双引号 + POSTGRESQL
     * LIMIT）。
     */
    protected AbstractPermissionDialect(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        super(keywordWrap, limitOffsetProcessor);
    }

    /** 由子类返回当前方言对应的 DbType，供 MybatisFlexConfiguration 注册时使用。 */
    public abstract DbType getDbType();

    /** 由子类决定是否启用数据权限拦截（对应 sz.data-scope.enabled）。 */
    protected abstract boolean isDataScopeEnabled();

    /** 由子类返回 logicMinUnit 配置（"user" / "dept"）。 */
    protected abstract String getLogicMinUnit();

    /** 由子类返回是否允许查看管理员数据。 */
    protected abstract boolean isAllowAdminView();

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        // 前置：全局开关、登录态、ThreadLocal 标记、操作类型
        if (isSkipDataScope(operateType)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        // 前置：目标表校验，防止误触 MybatisFlex Relation 多对多映射
        String tableName = getTableName(SimpleDataScopeHelper.get());
        if (!isTargetTable(queryWrapper, tableName)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        // 前置：超管跳过、子查询递归、表名合法性校验
        if (!initializeContext(queryWrapper, operateType)) {
            return;
        }

        // 核心：解析 scope 并拼接 SQL
        resolveAndApplyScope(queryWrapper, operateType, tableName);
        super.prepareAuth(queryWrapper, operateType);
    }

    /** 以"用户"为最小单位时，构造按部门集合过滤的 SQL 片段（通常 EXISTS 子查询）。 */
    protected abstract String buildUserUnitDeptSql(String table, String field, Collection<Long> deptList);

    /** 以"部门"为最小单位时，构造按部门集合过滤的 SQL 片段（通常 JSON 包含判断）。 */
    protected abstract String buildDeptUnitSql(String table, String field, Collection<Long> deptList);

    /** 构造允许查看管理员数据的 SQL 片段（通常 EXISTS 子查询）。 */
    protected abstract String buildAllowAdminViewSql(String table);

    /**
     * 构造排除超管创建数据的 SQL 片段（通常 NOT EXISTS 子查询）。
     * <p>
     * dept 模式下，dept_scope 存储创建人归属部门快照；超管归属全部门，导致任意部门用户均能通过 JSON_OVERLAPS
     * 命中超管数据。需在部门过滤条件上附加本片段（AND），将超管数据排除在外。 allowAdminView=true 时不追加本片段，超管数据仍可见。
     */
    protected abstract String buildExcludeAdminSql(String table);

    /** 构造按用户集合过滤的 SQL 片段（= 或 IN）。 */
    protected abstract String buildUserListSql(String table, String userField, Collection<Long> userList);

    /**
     * 解析当前请求的 scope 并将对应的 WHERE 条件注入 QueryWrapper。
     */
    private void resolveAndApplyScope(QueryWrapper queryWrapper, OperateType operateType, String tableName) {
        try {
            ControlPermissions controlPermissions = ControlThreadLocal.get();
            LoginUser loginUser = LoginUtils.getLoginUser();
            if (controlPermissions == null || loginUser == null) {
                log.error("PermissionDialect prepareAuth error: permissions or loginUser is null.");
                return;
            }

            String[] btnPermissions = controlPermissions.getPermissions();
            Map<String, Long> permissionMap = loginUser.getPermissionAndMenuIds();
            Map<Long, RoleMenuScopeVO> scopeMap = loginUser.getDataScope();

            // 情况A：所有权限标识均未命中 permissionAndMenuIds
            // → 对应菜单未开启 use_data_scope，不施加任何数据限制
            if (!hasAnyMenuId(btnPermissions, permissionMap)) {
                return;
            }

            // 情况B：菜单已开启 use_data_scope，但角色未配置数据权限
            // → 默认只看自己创建的数据
            RoleMenuScopeVO scope = resolveWidestScope(btnPermissions, permissionMap, scopeMap);
            if (scope == null) {
                buildSql(queryWrapper, tableName, Collections.emptySet(), Set.of(loginUser.getUserInfo().getId()), SimpleDataScopeHelper.get());
                return;
            }

            // 情况C：按配置的 dataScopeCd 施加过滤
            applyScope(queryWrapper, operateType, scope, tableName, loginUser);

        } catch (Exception e) {
            log.error("PermissionDialect Exception: {}", e.getMessage());
        }
    }

    /**
     * 判断权限标识列表中是否至少有一个命中了 permissionAndMenuIds。 命中即说明该菜单开启了
     * use_data_scope，需要进行数据权限控制。
     */
    private boolean hasAnyMenuId(String[] btnPermissions, Map<String, Long> permissionMap) {
        if (btnPermissions == null || btnPermissions.length == 0)
            return false;
        for (String permission : btnPermissions) {
            if (permissionMap.containsKey(permission))
                return true;
        }
        return false;
    }

    /**
     * 遍历所有权限标识，收集命中的 scope，返回最宽松的那个。
     * <p>
     * AND 模式：多个权限来自同一父菜单，scope 相同，取任一结果一致。<br>
     * OR 模式：多个权限来自不同父菜单，scope 可能不同，取最宽松（dataScopeCd 最小值）。
     * <p>
     * dataScopeCd 字典值大小与宽松程度的对应关系： 1006001（全部）< 1006002（本部门及以下）< 1006003（仅本部门）<
     * 1006004（仅本人）
     */
    private RoleMenuScopeVO resolveWidestScope(String[] btnPermissions, Map<String, Long> permissionMap, Map<Long, RoleMenuScopeVO> scopeMap) {
        if (scopeMap == null)
            return null;
        RoleMenuScopeVO widest = null;
        for (String permission : btnPermissions) {
            Long menuId = permissionMap.get(permission);
            if (menuId == null)
                continue;
            RoleMenuScopeVO candidate = scopeMap.get(menuId);
            if (candidate == null)
                continue;
            if (widest == null || candidate.getDataScopeCd().compareTo(widest.getDataScopeCd()) < 0) {
                widest = candidate;
            }
        }
        return widest;
    }

    /**
     * 按 scope 的 dataScopeCd 分发，拼接对应的 SQL WHERE 条件。
     * <p>
     * 当 dataScopeCd 为 1006002~1006004 且 extraCustomScope 非空时， 将自定义范围的
     * deptIds/userIds 合并进标准规则的集合，一次性构造 SQL，实现 OR 并集语义。
     */
    private void applyScope(QueryWrapper queryWrapper, OperateType operateType, RoleMenuScopeVO scope, String tableName, LoginUser loginUser) {
        String dataScopeCd = scope.getDataScopeCd();
        switch (dataScopeCd) {
            case DataScopeConstant.ALL, DataScopeConstant.DEPT_AND_BELOW, DataScopeConstant.DEPT_ONLY, DataScopeConstant.SELF_ONLY ->
                applyDataScopeRules(queryWrapper, operateType, dataScopeCd, tableName, SimpleDataScopeHelper.get(), scope.getExtraCustomScope(), loginUser);
            case DataScopeConstant.CUSTOM -> { // 纯自定义
                RoleMenuScopeVO.CustomScope customScope = scope.getCustomScope();
                Set<Long> deptIds = new HashSet<>();
                Set<Long> userIds = new HashSet<>();
                if (customScope != null) {
                    if (Utils.isNotNull(customScope.getDeptIds()))
                        deptIds.addAll(customScope.getDeptIds());
                    if (Utils.isNotNull(customScope.getUserIds()))
                        userIds.addAll(customScope.getUserIds());
                }
                // 始终包含当前用户自己的数据
                userIds.add(loginUser.getUserInfo().getId());
                buildSql(queryWrapper, tableName, deptIds, userIds, SimpleDataScopeHelper.get());
            }
        }
    }

    private boolean isSkipDataScope(OperateType operateType) {
        return !isDataScopeEnabled() || !SimpleDataScopeHelper.isDataScope() || !StpUtil.isLogin() || operateType != OperateType.SELECT;
    }

    private boolean isTargetTable(QueryWrapper queryWrapper, String table) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty())
            return false;
        for (QueryTable queryTable : queryTables) {
            if (table.equals(queryTable.getName()))
                return true;
            if (queryTable instanceof SelectQueryTable selectQueryTable) {
                if (isTargetTable(selectQueryTable.getQueryWrapper(), table))
                    return true;
            }
        }
        return false;
    }

    private boolean initializeContext(QueryWrapper queryWrapper, OperateType operateType) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty())
            return false;
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null || !ControlThreadLocal.hasLocal() || LoginUtils.isSuperAdmin()) {
            super.prepareAuth(queryWrapper, operateType);
            return false;
        }
        // 对子查询递归处理
        for (QueryTable queryTable : queryTables) {
            if (queryTable instanceof SelectQueryTable) {
                prepareAuth(((SelectQueryTable) queryTable).getQueryWrapper(), operateType);
            }
        }
        boolean isJoin = CPI.getJoins(queryWrapper) != null && !CPI.getJoins(queryWrapper).isEmpty();
        boolean valid = !buildTableMap(queryTables, isJoin, joinTables).isEmpty();
        if (!valid) {
            super.prepareAuth(queryWrapper, operateType);
        }
        return valid;
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

    private void applyDataScopeRules(QueryWrapper queryWrapper, OperateType operateType, String rule, String table, Class<?> tableClazz,
            RoleMenuScopeVO.CustomScope extraCustomScope, LoginUser loginUser) {
        assert loginUser != null;
        // 全部数据，直接放行（extraCustomScope 是子集，无需 OR）
        if (DataScopeConstant.ALL.equals(rule)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        Set<Long> deptList = new HashSet<>();
        Set<Long> userList = new HashSet<>();
        switch (rule) {
            case DataScopeConstant.DEPT_AND_BELOW -> deptList.addAll(loginUser.getDeptAndChildren()); // 本部门及以下
            case DataScopeConstant.DEPT_ONLY -> deptList.addAll(loginUser.getDepts()); // 仅本部门
            // DataScopeConstant.SELF_ONLY（仅本人）：不加部门，只加当前用户
        }
        userList.add(loginUser.getUserInfo().getId());
        // 若存在附加自定义范围（extraCustomScope），将其 deptIds/userIds 并入集合，实现 OR 并集语义
        if (extraCustomScope != null) {
            if (Utils.isNotNull(extraCustomScope.getDeptIds()))
                deptList.addAll(extraCustomScope.getDeptIds());
            if (Utils.isNotNull(extraCustomScope.getUserIds()))
                userList.addAll(extraCustomScope.getUserIds());
        }
        buildSql(queryWrapper, table, deptList, userList, tableClazz);
    }

    /**
     * 通用编排：调用子类抽象方法构造完整 SQL 并注入 WHERE 条件。
     * <p>
     * 字段存在性说明： - user 模式：部门过滤和用户过滤都使用 create_id，检查一次即可。 - dept 模式：部门过滤使用
     * dept_scope，用户过滤（兜底自己）使用 create_id，需分别检查。 dept_scope 不存在不影响 create_id
     * 的用户过滤分支，不能一刀切 return。
     * <p>
     * allowAdminView 说明： - 仅对"部门过滤"有意义：deptList 非空时，true=追加 OR EXISTS 超管条件，false=追加
     * AND NOT EXISTS 排除超管。 - deptList 为空（如"仅本人"规则）时，allowAdminView 不生效，只拼 create_id
     * = 自己，语义纯粹。
     */
    private void buildSql(QueryWrapper queryWrapper, String table, Collection<Long> deptList, Collection<Long> userList, Class<?> tableClazz) {
        String unit = getLogicMinUnit();
        boolean allowAdminView = isAllowAdminView();
        boolean isDeptUnit = !"user".equals(unit);

        // user 模式：dept_scope 字段不参与，只需检查 create_id
        // dept 模式：dept_scope 和 create_id 各自独立检查，见下方各分支
        if (!isDeptUnit && !isFieldExists(tableClazz, StringUtils.toCamelCase(FIELD_CREATE_ID)))
            return;

        StringBuilder sb = new StringBuilder();
        boolean isFirstAppend = true;

        if (!deptList.isEmpty()) {
            if (!isDeptUnit) {
                // user 模式：通过 sys_user_dept 关联表过滤
                if (allowAdminView) {
                    sb.append(buildUserUnitDeptSql(table, FIELD_CREATE_ID, deptList));
                } else {
                    sb.append("(").append(buildUserUnitDeptSql(table, FIELD_CREATE_ID, deptList)).append(" AND ").append(buildExcludeAdminSql(table))
                            .append(")");
                }
                isFirstAppend = false;
            } else if (isFieldExists(tableClazz, StringUtils.toCamelCase(FIELD_DEPT_SCOPE))) {
                // dept 模式：JSON_OVERLAPS 过滤 dept_scope
                if (allowAdminView) {
                    sb.append(buildDeptUnitSql(table, FIELD_DEPT_SCOPE, deptList));
                } else {
                    sb.append("(").append(buildDeptUnitSql(table, FIELD_DEPT_SCOPE, deptList)).append(" AND ").append(buildExcludeAdminSql(table)).append(")");
                }
                isFirstAppend = false;
            }
            // allowAdminView=true 时追加"OR 超管数据可见"，仅在部门过滤存在时有意义
            if (allowAdminView && !isFirstAppend) {
                sb.append(" OR ").append(buildAllowAdminViewSql(table));
            }
        }

        // 用户过滤（兜底：至少能看到自己的数据），始终使用 create_id
        if (!userList.isEmpty() && isFieldExists(tableClazz, StringUtils.toCamelCase(FIELD_CREATE_ID))) {
            if (!isFirstAppend)
                sb.append(" OR ");
            sb.append(buildUserListSql(table, FIELD_CREATE_ID, userList));
            isFirstAppend = false;
        }

        if (isFirstAppend)
            return; // 没有任何条件可拼，直接跳过

        // 防止同一 QueryWrapper 被重复注入
        String flagKey = "customScopeContext";
        if (CPI.getContext(queryWrapper, flagKey) == null || Boolean.FALSE.equals(CPI.getContext(queryWrapper, flagKey))) {
            queryWrapper.where("(" + sb + ")");
            CPI.putContext(queryWrapper, flagKey, true);
        }
    }

    private boolean isFieldExists(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            log.error("[DataScope]: Entity `{}` field `{}` not found.", clazz.getSimpleName(), fieldName);
        }
        return false;
    }

    protected String appendCollection(Collection<Long> collection) {
        return collection.stream().map(String::valueOf).collect(Collectors.joining(", ", "(", ")"));
    }
}
