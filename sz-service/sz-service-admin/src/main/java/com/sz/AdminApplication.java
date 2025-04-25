package com.sz;

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
    }

}