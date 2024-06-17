package com.sz.mysql;

import cn.dev33.satoken.stp.StpUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @ClassName DataScopeIntercept 数据权限
 * @Author sz
 * @Date 2024/6/17 16:01
 * @Version 1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class DataScopeIntercept implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 对sql进行处理
        Object[] queryArgs = invocation.getArgs();
        MappedStatement ms = (MappedStatement) queryArgs[0];
        Object parameter = queryArgs[1];
        BoundSql boundSql = ms.getBoundSql(parameter);
        String sql = boundSql.getSql();
        // 输出sql类型标识
        System.out.println(" SQL CMD type is 222222222 :" + sql);
        System.out.println(" mapper-id : " + ms.getId());

        String loginIdAsString = StpUtil.getLoginIdAsString();
        System.out.println(" loginIdAsString : " + loginIdAsString);

        // TODO 数据权限的处理。。。

        return invocation.proceed();
    }
}
