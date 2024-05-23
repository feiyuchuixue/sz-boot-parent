package com.sz;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AdminApplication {

    @Value("${app.version}")
    private String appVersion;

    private static String version;

    @PostConstruct
    public void init() {
        AdminApplication.version = appVersion;
    }

    public static String getVersion() {
        return version;
    }


    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("                                   __                _\n" +
                "                                  |  ]              (_)\n" +
                " .--.   ____  ______  ,--.    .--.| |  _ .--..--.   __   _ .--.\n" +
                "( (`\\] [_   ]|______|`'_\\ : / /'`\\' | [ `.-. .-. | [  | [ `.-. |\n" +
                " `'.'.  .' /_        // | |,| \\__/  |  | | | | | |  | |  | | | |\n" +
                "[\\__) )[_____]       \\'-;__/ '.__.;__][___||__||__][___][___||__]\n" +
                "------------------https://szadmin.cn  (v" + getVersion() + ")-------------------"
        );
    }

}

