package com.sz.mysql;

import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName PermissionDialect
 * @Author sz
 * @Date 2024/6/17 10:36
 * @Version 1.0
 */
public class PermissionDialect extends CommonsDialectImpl {

    // 数据权限是否仅针对查询有效
    private static boolean isSelectOnly = true;

    private boolean shouldSkipAuth(OperateType operateType) {
        return (isSelectOnly && operateType != OperateType.SELECT) || !DataScopeHelper.isDataScope();
    }

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        if (shouldSkipAuth(operateType)) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }

        DataScope[] dataScopes = DataScopeHelper.getDataScope();
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        System.out.println("queryTables ==" + queryTables.toString());
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return;
        }
        Map<String, QueryTable> queryTableMap = queryTables.stream()
                .collect(Collectors.toMap(
                        QueryTable::getName, // 作为键的函数
                        Function.identity()   // 保持值不变，即QueryTable对象本身
                ));
        // 单表查询
        if (queryTables.size() == 1) {
            System.out.println(" 单表查询");

        } else {
            // 多表查询
            System.out.println(" 多表查询");
        }
        System.out.println("dataScopes");
        for (DataScope ds : dataScopes) {
            // 忽略 ALL 权限
            if (DataScopeEnum.ALL.equals(ds.getScope())) {
                continue;
            }
            DataAccessService accessService = SpringApplicationContextUtils.getBean(DataAccessService.class);
            List<?> accessibleIds = accessService.getAccessibleIds(ds.getScope());
            // 忽略无效范围ID
            if (accessibleIds == null || accessibleIds.isEmpty()) {
                continue;
            }

            String simpleName = ds.getTableClass().getSimpleName(); // eg: TeacherStatics
            String tableName = StringUtils.toSnakeCase(simpleName); // eg: teacher_statics
            if (queryTableMap.containsKey(tableName)) {
                QueryTable table = queryTableMap.get(tableName);
                // 构造 in 查询 condition
                QueryCondition queryCondition = QueryCondition.create(
                        new QueryColumn(
                                table.getSchema(),
                                table.getName(),
                                ds.getColumnName(),
                                table.getAlias())
                        , SqlConsts.IN,
                        accessibleIds);
                queryWrapper.and(queryCondition);
            }
        }
        super.prepareAuth(queryWrapper, operateType);
    }
}
