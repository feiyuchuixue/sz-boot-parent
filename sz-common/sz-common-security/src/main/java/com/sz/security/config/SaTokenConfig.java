package com.sz.security.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.sz.core.common.configuration.WebMvcConfiguration;
import com.sz.security.core.MySaCheckPermissionHandler;
import com.sz.security.core.interceptor.MySaInterceptor;
import com.sz.security.pojo.WhitelistProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * @ClassName SaTokenConfig
 * @Author sz
 * @Date 2024/1/22 16:36
 * @Version 1.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig extends WebMvcConfiguration {

    private final WhitelistProperties whitelistProperties;

    @Bean
    public StpLogic getStpLogicJwt() {
        // Sa-Token 整合 jwt (简单模式)
        return new StpLogicJwtForSimple();
    }

    /**
     * 注册 Sa-Token 路由拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义 @SaCheckPermission 注解Handler;
        SaAnnotationStrategy.instance.registerAnnotationHandler(new MySaCheckPermissionHandler());
        // 注册 自定义 MySaInterceptor 拦截器
        registry.addInterceptor(new MySaInterceptor(handler -> {
            SaRouter.match("/**", r -> StpUtil.checkLogin()); // 这里可以结合自己业务改造
        })).addPathPatterns("/**").excludePathPatterns(whitelistProperties.getWhitelist());
    }

}
