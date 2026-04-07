package com.sz.mysql;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import com.mybatisflex.core.table.TableInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;

/**
 * 逻辑删除监听器，支持自动填充删除时间和删除人字段（如果实体类上使用了 {@link LogicDeleteFill} 注解并且表中存在对应字段）。
 * **只对指定了 @LogicDeleteFill注解的实体类生效，且仅在执行逻辑删除操作时触发。**
 */
@Slf4j
public class EntityLogicDeleteListener extends DefaultLogicDeleteProcessor {

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect iDialect) {
        StringBuilder sqlBuilder = new StringBuilder();
        Class<?> entityClass = tableInfo.getEntityClass();
        LogicDeleteFill annotation = entityClass.getAnnotation(LogicDeleteFill.class);
        sqlBuilder.append(iDialect.wrap(logicColumn)).append(EQUALS).append(prepareValue(getLogicDeletedValue()));
        if (annotation == null) {
            return sqlBuilder.toString();
        }
        String deleteTimeCol = annotation.deleteTimeColumn();
        String deleteByCol = annotation.deleteByColumn();

        List<String> columns = Arrays.asList(tableInfo.getAllColumns());
        if (!deleteTimeCol.isEmpty() && columns.contains(deleteTimeCol)) {
            sqlBuilder.append(", ").append(iDialect.wrap(deleteTimeCol)).append(EQUALS).append(" now()");
        }
        if (!deleteByCol.isEmpty() && isLogin() && columns.contains(deleteByCol)) {
            Object loginId = StpUtil.getStpLogic().getLoginId();
            sqlBuilder.append(", ").append(iDialect.wrap(deleteByCol)).append(EQUALS).append(prepareValue(loginId));
        }
        return sqlBuilder.toString();
    }

    private static Object prepareValue(Object value) {
        return (!(value instanceof Number) && !(value instanceof Boolean)) ? "'" + value + "'" : value;
    }

    private boolean isLogin() {
        try {
            return StpUtil.isLogin();
        } catch (NotWebContextException e) {
            log.error("[EntityLogicDeleteListener] Unexpected error user not login : {}", e.getMessage(), e);
            // 处理非 Web 环境异常，返回未登录
            return false;
        } catch (Exception e) {
            // 记录所有其他异常，并返回未登录
            log.error("[EntityLogicDeleteListener] Unexpected error during login check: {}", e.getMessage(), e);
            return false;
        }
    }

}
