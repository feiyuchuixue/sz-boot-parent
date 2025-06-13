package com.sz.sso.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SSO 平台中心模式示例，跳连接进入子系统 
 */
@RestController
public class HomeController {
    // 平台化首页
    @RequestMapping({"/", "/home"})
    public Object index() {
        // 如果未登录，则先去登录
        if(!StpUtil.isLogin()) {
            return SaHolder.getResponse().redirect("/sso/auth");
        }

        // 拼接各个子系统的地址，格式形如：/sso/auth?client=xxx&redirect=${子系统首页}/sso/login?back=${子系统首页}
        String link1 = "/sso/auth?client=sso-client3&redirect=http://sa-sso-client1.com:9003/sso/login?back=http://sa-sso-client1.com:9003/";
        String link2 = "/sso/auth?client=sso-client3&redirect=http://127.0.0.1:9991/api/admin/sso/login?back=http://127.0.0.1:9991/api/admin/home";
        // String link3 = "/sso/auth?client=sso-client3&redirect=http://127.0.0.1:9991/api/admin/sso/login?back=http://127.0.0.1:9991/api/admin/home";
        String link3 = "/sso/auth?client=sso-client3&redirect=http://localhost:9848/login?back=http://localhost:9848/login";

        // 组织网页结构返回到前端
        String title = "<h2>SSO 平台首页 (平台中心模式)</h2>";
        String client1 = "<p><a href='" + link1 + "' target='_blank'> 进入Client1系统 </a></p>";
        String client2 = "<p><a href='" + link2 + "' target='_blank'> 进入Client2系统 </a></p>";
        String client3 = "<p><a href='" + link3 + "' target='_blank'> 进入Client3系统 </a></p>";

        return title + client1 + client2 + client3;
    }
}
