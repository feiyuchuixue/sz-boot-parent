package com.sz.generator.pojo.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sz
 * @since 2024/1/15 8:38
 */
@Data
@Component
@ConfigurationProperties(prefix = "sz.generator")
public class GeneratorProperties {

    // 初始值配置
    private PathProperties path;

    // 全局配置
    private GlobalProperties global;

    // 模块名
    private String moduleName = "sz-service";

    // service 名
    private String serviceName = "sz-service-admin";

    @Data
    public static class GlobalProperties {

        // 作者
        private String author = "sz-admin";

        // 包名
        private String packages = "com.sz.admin";

        // 是否需要去掉前缀配置
        private IgnoreTablePrefix ignoreTablePrefix;
    }

    @Data
    public static class PathProperties {

        // 前端项目初始路径
        private String web;

        // 后端项目初始路径
        private String api;
    }

    @Data
    public static class IgnoreTablePrefix {

        // 是否开启
        private Boolean enabled = false;

        // 需要去除的前缀
        private String[] prefixes = new String[]{"t_"};

    }

}
