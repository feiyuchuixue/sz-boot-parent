package com.sz.sso.controller;

import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token-SSO Server端 Controller 
 */
@RestController
public class SsoServerController {

    /**
     * SSO-Server端：处理所有SSO相关请求 
     *         http://{host}:{port}/sso/auth            -- 单点登录授权地址
     *         http://{host}:{port}/sso/doLogin        -- 账号密码登录接口，接受参数：name、pwd
     *         http://{host}:{port}/sso/signout        -- 单点注销地址（isSlo=true时打开）
     */
/*    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoServerProcessor.instance.dister();
    }*/

    // SSO-Server：统一认证地址
    @RequestMapping("/sso/auth")
    public Object ssoAuth() {
        return SaSsoServerProcessor.instance.ssoAuth();
    }

    // SSO-Server：RestAPI 登录接口
    @RequestMapping("/sso/doLogin")
    public Object ssoDoLogin() {
        return SaSsoServerProcessor.instance.ssoDoLogin();
    }

    // SSO-Server：接收推送消息地址
    @RequestMapping("/sso/pushS")
    public Object ssoPushS() {
        return SaSsoServerProcessor.instance.ssoPushS();
    }

    // SSO-Server：单点注销
    @RequestMapping("/sso/signout")
    public Object ssoSignout() {
        return SaSsoServerProcessor.instance.ssoSignout();
    }


    /**
     * 配置SSO相关参数 
     */
    @Autowired
    private void configSso(SaSsoServerTemplate ssoServerTemplate) {
        // 配置：未登录时返回的View 
        ssoServerTemplate.strategy.notLoginView = () -> {
            // 简化模拟表单
            String doLoginCode =
                    "fetch(`/sso/doLogin?name=${document.querySelector('#name').value}&pwd=${document.querySelector('#pwd').value}`) " +
                    " .then(res => res.json()) " +
                    " .then(res => { if(res.code === 200) { location.reload() } else { alert(res.msg) } } )";
            return "<h2>当前客户端在 SSO-Server 认证中心尚未登录，请先登录</h2>" +
            "用户：<input id='name' /> <br> " +
            "密码：<input id='pwd' /> <br>" +
            "<button onclick=\"" + doLoginCode + "\">登录</button>";
        };
        
        // 配置：登录处理函数 
        ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> {
            // 此处仅做模拟登录，真实环境应该查询数据库进行登录 
            if("sa".equals(name) && "123456".equals(pwd)) {
                StpUtil.login(10001);
                return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
            }
            return SaResult.error("登录失败！");
        };
    }
    
}
