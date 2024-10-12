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

    private final FlywayProperties flywayProperties;

    private final Flyway frameworkFlyway;

    private final Flyway businessFlyway;

    @Getter
    private static String version;

    @PostConstruct
    public void init() {
        AdminApplication.version = appVersion;
        FlywayProperties.FlywayConfig business = flywayProperties.getBusiness();
        FlywayProperties.FlywayConfig framework = flywayProperties.getFramework();
        if (framework.isEnabled())
            frameworkFlyway.migrate();
        if (business.isEnabled())
            businessFlyway.migrate();
    }

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("                                   __                _\n" + "                                  |  ]              (_)\n"
                + " .--.   ____  ______  ,--.    .--.| |  _ .--..--.   __   _ .--.\n" + "( (`\\] [_   ]|______|`'_\\ : / /'`\\' | [ `.-. .-. | [  | [ `.-. |\n"
                + " `'.'.  .' /_        // | |,| \\__/  |  | | | | | |  | |  | | | |\n"
                + "[\\__) )[_____]       \\'-;__/ '.__.;__][___||__||__][___][___||__]\n" + "------------------https://szadmin.cn  (v" + getVersion()
                + ")-------------------");
    }

}
