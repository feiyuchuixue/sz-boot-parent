package com.sz.mysql;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName RegisterCustomMybatisInterceptor
 * @Author sz
 * @Date 2024/6/17 16:05
 * @Version 1.0
 */
@Order(value = Integer.MAX_VALUE)
@Component
public class RegisterCustomMybatisInterceptor implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactories;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 拦截器执行顺序: 最后的先执行
        for (SqlSessionFactory factory : sqlSessionFactories) {
            // 加密解密拦截器
            factory.getConfiguration().addInterceptor(new DataScopeIntercept());
        }

    }
}
