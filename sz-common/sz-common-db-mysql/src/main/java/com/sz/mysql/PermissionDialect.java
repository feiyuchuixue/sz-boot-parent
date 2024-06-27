package com.sz.mysql;

import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义方言 -- 数据权限
 *
 * @ClassName PermissionDialect
 * @Author sz
 * @Date 2024/6/17 10:36
 * @Version 1.0
 */
@Slf4j
public class PermissionDialect extends CommonsDialectImpl {

    private boolean shouldSkipAuth(OperateType operateType) {
        return operateType != OperateType.SELECT || !DataScopeHelper.isDataScope();
    }

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        if (shouldSkipAuth(operateType)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
        try {
            DataScope[] dataScopes = DataScopeHelper.getDataScope();
            List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
            List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
            if (queryTables.isEmpty()) {
                return;
            }
            for (QueryTable queryTable : queryTables) {
                if (queryTable instanceof SelectQueryTable){
                    prepareAuth(((SelectQueryTable) queryTable).getQueryWrapper(),operateType);
                }
            }

            // 判断是否是isJoin
            boolean isJoin = CPI.getJoins(queryWrapper) != null && !CPI.getJoins(queryWrapper).isEmpty();
            Map<String, QueryTable> tableMap = new HashMap<>();
            for (QueryTable queryTable : queryTables) {
                // TODO 临时方案：如果name为空或空字符串直接return；等待官方修复。忽略非正常结构 queryTables ==[SELECT * FROM TABLE]
                if (queryTable.getName() == null || ("").equals(queryTable.getName().trim())) {
                    return;
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
            for (DataScope scope : dataScopes) {
                // 忽略 ALL 权限
                if (DataScopeEnum.ALL.equals(scope.getScope())) {
                    continue;
                }
                // 根据scope 获取可访问的 ID 列表
                DataAccessService accessService = SpringApplicationContextUtils.getBean(DataAccessService.class);
                List<?> accessibleIds = accessService.getAccessibleIds(scope.getScope());
                // 忽略无效范围ID
                if (accessibleIds == null || accessibleIds.isEmpty()) {
                    continue;
                }
                // 忽略无效字段
                boolean fieldExists = isFieldExists(scope.getTableClass(), StringUtils.toCamelCase(scope.getColumnName()));
                if (!fieldExists) {
                    continue;
                }
                String simpleName = scope.getTableClass().getSimpleName(); // eg: TeacherStatics
                String tableName = StringUtils.toSnakeCase(simpleName); // eg: teacher_statics
                if (tableMap.containsKey(tableName)) {
                    QueryTable table = tableMap.get(tableName);
                    // 构造 in 查询 condition
                    QueryCondition queryCondition = QueryCondition.create(
                            new QueryColumn(
                                    table.getSchema(),
                                    table.getName(),
                                    scope.getColumnName(),
                                    table.getAlias()),
                            SqlConsts.IN,
                            accessibleIds);
                    queryWrapper.where(queryCondition);
                }
            }
        } catch (Exception e) {
            log.error(" PermissionDialect Exception :" + e.getMessage());
        } finally {
            // 清除数据作用域以避免分页场景中的SQL语句重复拼接问题。
            DataScopeHelper.clearDataScope();
            super.prepareAuth(queryWrapper, operateType);
        }
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

}
