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
        version = appVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
        System.out.println("                                           __             _    \n"
                + "                                          [  |  _        / |_  \n" + " .--.   ____  ______  .--.   .--.   .---.  | | / ] .---.`| |-' \n"
                + "( (`\\] [_   ]|______|( (`\\]/ .'`\\ \\/ /'`\\] | '' < / /__\\\\| |   \n"
                + " `'.'.  .' /_         `'.'.| \\__. || \\__.  | |`\\ \\| \\__.,| |,  \n"
                + "[\\__) )[_____]       [\\__) )'.__.' '.___.'[__|  \\_]'.__.'\\__/  \n" + "------------------https://szadmin.cn  (v" + getVersion()
                + ")-------------------");
    }

}
