package com.sz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("                                   __                _\n" +
                "                                  |  ]              (_)\n" +
                " .--.   ____  ______  ,--.    .--.| |  _ .--..--.   __   _ .--.\n" +
                "( (`\\] [_   ]|______|`'_\\ : / /'`\\' | [ `.-. .-. | [  | [ `.-. |\n" +
                " `'.'.  .' /_        // | |,| \\__/  |  | | | | | |  | |  | | | |\n" +
                "[\\__) )[_____]       \\'-;__/ '.__.;__][___||__||__][___][___||__]\n" +
                "------------------https://szadmin.cn  (v0.6.0 Beta)-------------------"
        );
    }

}

