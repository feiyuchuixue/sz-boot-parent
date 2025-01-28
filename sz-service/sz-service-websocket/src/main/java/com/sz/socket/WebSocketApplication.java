package com.sz.socket;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sz")
public class WebSocketApplication {

    @Value("${app.version}")
    private String appVersion;

    @Getter
    private static String version;

    @PostConstruct
    public void init() {
        setVersion(appVersion);
    }

    private static void setVersion(String appVersion) {
        WebSocketApplication.version = appVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
        String template = """
                                                           __             _
                                                          [  |  _        / |_
                 .--.   ____  ______  .--.   .--.   .---.  | | / ] .---.`| |-'
                ( (`\\] [_   ]|______|( (`\\]/ .'`\\ \\/ /'`\\] | '' < / /__\\\\| |
                 `'.'.  .' /_         `'.'.| \\__. || \\__.  | |`\\ \\| \\__.,| |,
                [\\__) )[_____]       [\\__) )'.__.' '.___.'[__|  \\_]'.__.'\\__/
                ------------------%s (v%s)-------------------
                """;
        String result = String.format(template, "https://szadmin.cn", getVersion());
        System.out.println(result);
    }

}
