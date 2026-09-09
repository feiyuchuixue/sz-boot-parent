package com.sz.db.config;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.sz.db.DataScopeProperties;
import com.sz.db.id.SzIdGenerator;
import com.sz.db.id.SzSnowflakeProperties;
import com.sz.db.listener.EntityLogicDeleteListener;
import com.sz.db.permission.AbstractPermissionDialect;
import com.sz.logger.AuditProperties;
import com.sz.logger.PrintSQL;
import cn.hutool.core.lang.Snowflake;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex 全局配置（与方言无关）。
 * <p>
 * 数据权限方言由 {@link AbstractPermissionDialect} 子类（如
 * MysqlPermissionDialect）通过组件扫描注册为 Bean， 这里通过 {@link ObjectProvider}
 * 懒加载注入并按其声明的 DbType 注册到 DialectFactory。
 *
 * @author sz
 */
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Resource
    private DataScopeProperties dataScopeProperties;

    @Resource
    private SzSnowflakeProperties szSnowflakeProperties;

    @Resource
    private ObjectProvider<AbstractPermissionDialect> permissionDialectProvider;

    @Resource
    private AuditProperties auditProperties;

    public MybatisFlexConfiguration() {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE); // 关闭全局null参数忽略设置
        // register sz global key generator
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(SzIdGenerator.NAME);
        keyConfig.setBefore(true);
        FlexGlobalConfig.getDefaultConfig().setKeyConfig(keyConfig);

    }

    @PostConstruct
    public void initSqlAudit() {
        PrintSQL.print(auditProperties.resolveSql());
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        return new EntityLogicDeleteListener();
    }

    /**
     * 全局唯一 Snowflake 实例，供框架层（SzIdGenerator）和业务层统一使用，避免多实例 ID 碰撞。
     */
    @Bean
    public Snowflake snowflake() {
        return new Snowflake(szSnowflakeProperties.getWorkerId(), szSnowflakeProperties.getDatacenterId());
    }

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        // 注册 sz 统一雪花 ID 生成器，复用 Spring 管理的 Snowflake 单例
        com.mybatisflex.core.keygen.KeyGeneratorFactory.register(SzIdGenerator.NAME, new SzIdGenerator(snowflake()));

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
