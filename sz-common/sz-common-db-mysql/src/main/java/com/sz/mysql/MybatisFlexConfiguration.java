package com.sz.mysql;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.sz.logger.PrintSQL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MybatisFlexConfiguration
 * @Author sz
 * @Date 2024/5/11 14:58
 * @Version 1.0
 */
@Configuration
public class MybatisFlexConfiguration {

    public MybatisFlexConfiguration() {
        DialectFactory.registerDialect(DbType.MYSQL,new PermissionDialect());
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

}
