package com.sz.configuration;


import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.logicdelete.impl.DateTimeLogicDeleteProcessor;
import com.sz.mysql.BaseFlexConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyMFConfiguration - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/6/16
 */
@MapperScan(basePackages = {"com.sz.sso.**.mapper"})
@Configuration
public class MyMFConfiguration extends BaseFlexConfiguration {

    @Override
    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        return new DateTimeLogicDeleteProcessor();
    }
}
