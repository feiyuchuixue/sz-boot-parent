package com.sz.configuration;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.sz.security.core.util.LoginUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;

/**
 * 逻辑删除处理： 填充删除人、 删除时间
 */
@Deprecated
public class SsoEntityLogicDeleteListener extends DefaultLogicDeleteProcessor {

    private static final String FIELD_DELETE_TIME = "delete_time";

    private static final String FIELD_DELETE_ID = "delete_id";

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        System.out.println("0000000000000000000");
        String[] columns = tableInfo.getColumns();
        // 如果 columns中有某个属性
        if (Arrays.asList(columns).contains("del_time")) {
            return dialect.wrap(logicColumn) + " IS NULL ";
        }
        return super.buildLogicNormalCondition(logicColumn, tableInfo, dialect);
    }

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo, String joinTableAlias) {
        System.out.println("22222222222222222222222");
        String[] columns = tableInfo.getColumns();
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()).as(joinTableAlias);
        QueryColumn queryColumn = new QueryColumn(queryTable, tableInfo.getLogicDeleteColumn());
        if (Arrays.asList(columns).contains("del_time")) {
            queryWrapper.and(queryColumn.isNull());
        } else {
            queryWrapper.and(queryColumn.eq(getLogicNormalValue()));
        }
    }

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect iDialect) {
        System.out.println("3333333333333333333333333333333333333");
        StringBuilder sqlBuilder = new StringBuilder();

        List<String> columns = Arrays.asList(tableInfo.getAllColumns());
        // 如果 columns中有某个属性
        if (columns.contains("del_time")) {
            sqlBuilder.append(", ").append(iDialect.wrap("del_time")).append(EQUALS).append("now()");
        } else {
            sqlBuilder.append(iDialect.wrap(logicColumn)).append(EQUALS).append(prepareValue(getLogicDeletedValue()));
        }

        if (columns.contains(FIELD_DELETE_TIME)) {
            sqlBuilder.append(", ").append(iDialect.wrap(FIELD_DELETE_TIME)).append(EQUALS).append("now()");
        }

        boolean isLogin = StpUtil.isLogin();
        if (isLogin && columns.contains(FIELD_DELETE_ID)) {
            sqlBuilder.append(", ").append(iDialect.wrap(FIELD_DELETE_ID)).append(EQUALS)
                    .append(Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId());
        }

        return sqlBuilder.toString();
    }

    @Override
    public Object getLogicDeletedValue() {
        return "now()";
    }

    @Override
    public Object getLogicNormalValue() {
        System.out.println("11111111111111111");

        return super.getLogicNormalValue();
    }

    private static Object prepareValue(Object value) {
        return (!(value instanceof Number) && !(value instanceof Boolean)) ? "'" + value + "'" : value;
    }
}
