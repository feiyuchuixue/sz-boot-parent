package com.sz;

import cn.dev33.satoken.sso.SaSsoManager;
import com.sz.mysql.FlywayProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class AdminApplication {

    @Value("${app.version}")
    private String appVersion;

    @Deprecated(since = "v1.3.0-beta", forRemoval = true)
    private final FlywayProperties flywayProperties;

    @Deprecated(since = "v1.3.0-beta", forRemoval = true)
    private final Flyway frameworkFlyway;

    @Deprecated(since = "v1.3.0-beta", forRemoval = true)
    private final Flyway businessFlyway;

    @Getter
    private static String version;

    @PostConstruct
    public void init() {
        setVersion(appVersion); // 通过辅助方法设置静态字段
        initFlyway();
    }

    @Deprecated(since = "v1.3.0-beta", forRemoval = true)
    private void initFlyway() {
        FlywayProperties.FlywayConfig business = flywayProperties.getBusiness();
        FlywayProperties.FlywayConfig framework = flywayProperties.getFramework();
        if (framework.isEnabled())
            frameworkFlyway.migrate();
        if (business.isEnabled())
            businessFlyway.migrate();
    }

    private static void setVersion(String appVersion) {
        AdminApplication.version = appVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        String template = """
                                                   __                _
                                                  |  ]              (_)
                 .--.   ____  ______  ,--.    .--.| |  _ .--..--.   __   _ .--.
                ( (`\\] [_   ]|______|`'_\\ : / /'`\\' | [ `.-. .-. | [  | [ `.-. |
                 `'.'.  .' /_        // | |,| \\__/  |  | | | | | |  | |  | | | |
                [\\__) )[_____]       \\'-;__/ '.__.;__][___||__||__][___][___||__]
                ------------------%s  (v%s)-------------------
                """;
        String result = String.format(template, "https://szadmin.cn", getVersion());
        System.out.println(result);

        System.out.println();
        System.out.println("---------------------- Sa-Token SSO 模式三 Client 端启动成功 ----------------------");
        System.out.println("配置信息：" + SaSsoManager.getClientConfig());
        System.out.println("测试访问应用端一: http://sa-sso-client1.com:9002");
        System.out.println("测试访问应用端二: http://127.0.0.1:9991/api/admin/");
        System.out.println("测试访问应用端三: http://sa-sso-client3.com:9002");
        System.out.println("测试前需要根据官网文档修改hosts文件，测试账号密码：sa / 123456");
        System.out.println();

    }

}