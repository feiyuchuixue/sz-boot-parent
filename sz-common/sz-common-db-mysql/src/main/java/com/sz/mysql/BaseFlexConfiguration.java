package com.sz.mysql;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;

/**
 * @author sz
 * @since 2024/5/11 14:58
 */
public class BaseFlexConfiguration implements MyBatisFlexCustomizer {

    @Resource
    private DataScopeProperties dataScopeProperties;

    public BaseFlexConfiguration() {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE);// 关闭全局null参数忽略设置
        PrintSQL.print();
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        return new EntityLogicDeleteListener();
    }

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        if (dataScopeProperties.isEnabled()) {
            // 注册查询权限监听方言
            DialectFactory.registerDialect(DbType.MYSQL, new SimplePermissionDialect());
        }
    }

}
