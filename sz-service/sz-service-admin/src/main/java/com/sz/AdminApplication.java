package com.sz;

import com.sz.core.util.AppVersionUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AdminApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AdminApplication.class, args);
        String template = """
                                                   __                _
                                                  |  ]              (_)
                 .--.   ____  ______  ,--.    .--.| |  _ .--..--.   __   _ .--.
                ( (`\\] [_   ]|______|`'_\\ : / /'`\\' | [ `.-. .-. | [  | [ `.-. |
                 `'.'.  .' /_        // | |,| \\__/  |  | | | | | |  | |  | | | |
                [\\__) )[_____]       \\'-;__/ '.__.;__][___||__||__][___][___||__]
                ------------------%s  (v%s)-------------------
                """;
        String version = AppVersionUtils.resolve(context.getEnvironment().getProperty("app.version"), AdminApplication.class);
        String result = String.format(template, "https://szadmin.cn", version);
        System.out.println(result);
    }

}
