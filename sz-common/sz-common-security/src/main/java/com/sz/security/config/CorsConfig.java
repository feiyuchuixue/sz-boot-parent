package com.sz.security.config;

import com.sz.security.core.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName CorsConfig
 * @Author sz
 * @Date 2024/2/2 8:31
 * @Version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsProperties corsProperties;

    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>(corsFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // TODO: 推荐在生产环境中，origin设置成准确域名。不要设置为*
        // corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        CopyOnWriteArrayList<String> allowedOrigins = corsProperties.getAllowedOrigins();
        if (allowedOrigins.contains("*")) {
            corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        } else {
            for (String origin : allowedOrigins) {
                corsConfiguration.addAllowedOrigin(origin);
            }
        }
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.addExposedHeader(CorsConfiguration.ALL);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }
}
