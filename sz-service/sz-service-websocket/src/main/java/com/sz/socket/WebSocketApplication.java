package com.sz.socket;

import com.sz.core.util.AppVersionUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.sz")
@EnableScheduling
public class WebSocketApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WebSocketApplication.class, args);
        String template = """
                                                           __             _
                                                          [  |  _        / |_
                 .--.   ____  ______  .--.   .--.   .---.  | | / ] .---.`| |-'
                ( (`\\] [_   ]|______|( (`\\]/ .'`\\ \\/ /'`\\] | '' < / /__\\\\| |
                 `'.'.  .' /_         `'.'.| \\__. || \\__.  | |`\\ \\| \\__.,| |,
                [\\__) )[_____]       [\\__) )'.__.' '.___.'[__|  \\_]'.__.'\\__/
                ------------------%s (v%s)-------------------
                """;
        String version = AppVersionUtils.resolve(context.getEnvironment().getProperty("app.version"), WebSocketApplication.class);
        String result = String.format(template, "https://szadmin.cn", version);
        System.out.println(result);
    }

}
