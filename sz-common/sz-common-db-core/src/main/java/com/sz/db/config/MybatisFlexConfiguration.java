package com.sz.db.config;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.sz.db.DataScopeProperties;
import com.sz.db.listener.EntityLogicDeleteListener;
import com.sz.db.permission.AbstractPermissionDialect;
import com.sz.logger.PrintSQL;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex 全局配置（与方言无关）。
 * <p>
 * 数据权限方言由 {@link AbstractPermissionDialect} 子类（如 MysqlPermissionDialect）通过组件扫描注册为 Bean，
 * 这里通过 {@link ObjectProvider} 懒加载注入并按其声明的 DbType 注册到 DialectFactory。
 *
 * @author sz
 */
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Resource
    private DataScopeProperties dataScopeProperties;

    @Resource
    private ObjectProvider<AbstractPermissionDialect> permissionDialectProvider;

    public MybatisFlexConfiguration() {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE); // 关闭全局null参数忽略设置
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
        if (!dataScopeProperties.isEnabled()) {
            return;
        }
        AbstractPermissionDialect dialect = permissionDialectProvider.getIfAvailable();
        if (dialect == null) {
            return;
        }
        DialectFactory.registerDialect(dialect.getDbType(), dialect);
    }

}
