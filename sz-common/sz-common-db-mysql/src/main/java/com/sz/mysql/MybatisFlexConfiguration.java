package com.sz.mysql;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.sz.logger.PrintSQL;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MybatisFlexConfiguration
 * @Author sz
 * @Date 2024/5/11 14:58
 * @Version 1.0
 */
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Resource
    private DataScopeProperties dataScopeProperties;

    public MybatisFlexConfiguration() {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE);// 关闭全局null参数忽略设置
        PrintSQL.print();
    }

    /**
     * 分页插件
     *
     * @return
     */
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
