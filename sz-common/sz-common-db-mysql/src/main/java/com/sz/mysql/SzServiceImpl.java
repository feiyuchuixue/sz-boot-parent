package com.sz.mysql;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.service.IService;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SzServiceImpl
 * @Author sz
 * @Date 2024/6/17 16:44
 * @Version 1.0
 */
@Slf4j
public class SzServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Autowired
    protected M mapper;

    @Override
    public BaseMapper<T> getMapper() {
        return mapper;
    }

    @Override
    public List<T> list(QueryWrapper query) {
        dataScopeQuery(query);
        //获取当前用户信息，为 queryWrapper 添加额外的条件
        return IService.super.list(query);
    }

    @Override
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        dataScopeQuery(query);
        return IService.super.listAs(query, asType);
    }

    @Override
    public Page<T> page(Page<T> page, QueryWrapper query) {
        dataScopeQuery(query);
        return IService.super.page(page, query);
    }

    @Override
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        dataScopeQuery(query);
        return IService.super.pageAs(page, query, asType);
    }

    private void dataScopeQuery(QueryWrapper queryWrapper) {
        DataScope[] dataScopes = DataScopeHelper.getDataScope();
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return;
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
        // 清除数据作用域以避免分页场景中的SQL语句重复拼接问题。
        // 此操作应在数据处理完成后立即执行，以确保数据的一致性和准确性。
        DataScopeHelper.clearDataScope();
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
