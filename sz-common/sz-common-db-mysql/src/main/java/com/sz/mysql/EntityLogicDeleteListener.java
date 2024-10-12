package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import com.mybatisflex.core.table.TableInfo;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;

/**
 * 逻辑删除处理： 填充删除人、 删除时间
 */
@Slf4j
public class EntityLogicDeleteListener extends DefaultLogicDeleteProcessor {

    private static final String FIELD_DELETE_TIME = "delete_time";

    private static final String FIELD_DELETE_ID = "delete_id";

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect iDialect) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(iDialect.wrap(logicColumn)).append(EQUALS).append(prepareValue(getLogicDeletedValue()));

        List<String> columns = Arrays.asList(tableInfo.getAllColumns());

        if (columns.contains(FIELD_DELETE_TIME)) {
            sqlBuilder.append(", ").append(iDialect.wrap(FIELD_DELETE_TIME)).append(EQUALS).append("now()");
        }

        boolean isLogin = StpUtil.isLogin();
        if (isLogin && columns.contains(FIELD_DELETE_ID)) {
            sqlBuilder.append(", ").append(iDialect.wrap(FIELD_DELETE_ID)).append(EQUALS).append(LoginUtils.getLoginUser().getUserInfo().getId());
        }

        return sqlBuilder.toString();
    }

    private static Object prepareValue(Object value) {
        return (!(value instanceof Number) && !(value instanceof Boolean)) ? "'" + value + "'" : value;
    }
}
