package com.sz.ssoserver.controller;

import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerUtil;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.platform.pojo.SsoLoginParam;
import com.sz.ssoserver.ssouser.service.SsoUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token-SSO Server端 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sso")
public class SsoServerController {

    private final SsoUserService ssoUserService;

    /**
     * SSO-Server端：处理所有SSO相关请求 http://{host}:{port}/sso/auth -- 单点登录授权地址
     * http://{host}:{port}/sso/doLogin -- 账号密码登录接口，接受参数：name、pwd
     * http://{host}:{port}/sso/signout -- 单点注销地址（isSlo=true时打开）
     */
    /*
     * @RequestMapping("/sso/*") public Object ssoRequest() { return
     * SaSsoServerProcessor.instance.dister(); }
     */

    // SSO-Server：统一认证地址
    @RequestMapping("/auth")
    public Object ssoAuth() {
        return SaSsoServerProcessor.instance.ssoAuth();
    }

    // SSO-Server：RestAPI 登录接口
    @RequestMapping("/doLogin")
    public Object ssoDoLogin() {
        return SaSsoServerProcessor.instance.ssoDoLogin();
    }

    // SSO-Server：接收推送消息地址
    @RequestMapping("/pushS")
    public Object ssoPushS() {
        return SaSsoServerProcessor.instance.ssoPushS();
    }

    // SSO-Server：单点注销
    @RequestMapping("/signout")
    public Object ssoSignout() {
        return SaSsoServerProcessor.instance.ssoSignout();
    }

    @Operation(summary = "SSO登录接口")
    @PostMapping("login")
    public ApiResult<SaTokenInfo> ssoLogin(@RequestBody SsoLoginParam param) {
        return ApiResult.success(ssoUserService.ssoLogin(param));
    }

    /**
     * 配置SSO相关参数
     */
    @Autowired
    private void configSso(SaSsoServerTemplate ssoServerTemplate) {
        // 配置：未登录时返回的View
        ssoServerTemplate.strategy.notLoginView = () -> {
            // 简化模拟表单
            String doLoginCode = "fetch(`/sso/doLogin?name=${document.querySelector('#name').value}&pwd=${document.querySelector('#pwd').value}`) "
                    + " .then(res => res.json()) " + " .then(res => { if(res.code === 200) { location.reload() } else { alert(res.msg) } } )";
            return "<h2>当前客户端在 SSO-Server 认证中心尚未登录，请先登录</h2>" + "用户：<input id='name' /> <br> " + "密码：<input id='pwd' /> <br>" + "<button onclick=\""
                    + doLoginCode + "\">登录</button>";
        };

        // 配置：登录处理函数
        /*
         * ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> { //boolean
         * checkPassword = ssoUserService.checkPassword(name, pwd);
         * 
         * 
         * };
         */
    }

    @RequestMapping("/getRedirectUrl")
    public ApiResult<String> getRedirectUrl(String client, String redirect, String mode) {
        // 未登录情况下，返回 code=401
        if (!StpUtil.isLogin()) {
            return ApiResult.error(CommonResponseEnum.SSO_UNKNOWN_LOGIN);
        }
        System.out.println("getRedirectUrl client=" + client + ", redirect=" + redirect + ", mode=" + mode);
        // 已登录情况下，构建 redirectUrl
        redirect = SaFoxUtil.decoderUrl(redirect);
        if (SaSsoConsts.MODE_SIMPLE.equals(mode)) {
            // 模式一
            SaSsoServerUtil.checkRedirectUrl(client, redirect);
            return ApiResult.success(redirect);
        } else {
            // 模式二或模式三
            String redirectUrl = SaSsoServerUtil.buildRedirectUrl(client, redirect, StpUtil.getLoginId(), StpUtil.getTokenValue());
            return ApiResult.success(redirectUrl);
        }
    }

}
