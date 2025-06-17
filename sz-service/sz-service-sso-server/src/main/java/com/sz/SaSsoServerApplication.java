package com.sz;

import cn.dev33.satoken.sso.SaSsoManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class SaSsoServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaSsoServerApplication.class, args);

        System.out.println();
        System.out.println("---------------------- Sa-Token SSO 统一认证中心启动成功 ----------------------");
        System.out.println("配置信息：" + SaSsoManager.getServerConfig());
        System.out.println("统一认证登录地址：http://127.0.0.1:5000/sso/auth");
        System.out.println("测试前需要根据官网文档修改 hosts 文件，测试账号密码：sa / 123456");
        System.out.println();
    }
}
