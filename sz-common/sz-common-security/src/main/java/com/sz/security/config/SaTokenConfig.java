package com.sz.security.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.core.common.configuration.WebMvcConfiguration;
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
        // 路由拦截鉴权
        registry.addInterceptor(new SaInterceptor(r -> {
            StpUtil.checkLogin();
        }).isAnnotation(false)).addPathPatterns("/**").excludePathPatterns(whitelistProperties.getWhitelist());

        // 打开注解鉴权
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**").excludePathPatterns(whitelistProperties.getWhitelist());
    }

}
