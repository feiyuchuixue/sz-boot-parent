package com.sz.www.test.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.util.SaResult;
import com.sz.core.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token-SSO Client端 Controller
 */
@SaIgnore
@RestController
public class SsoClientController {

    // 首页
    @RequestMapping("/home")
    public String index() {
        String str = "<h2>Sa-Token SSO-Client 应用端 (模式三)</h2>" +
                "<p>当前会话是否登录：" + StpUtil.isLogin() + " (" + StpUtil.getLoginId("") + ")</p>" +
                "<p> " +
                "<a href='/api/admin/sso/login?back=/home'>登录</a> - " +
                "<a href='/api/admin/sso/logoutByAlone?back=/'>单应用注销</a> - " +
                "<a href='/api/admin/sso/logout?back=self'>全端注销</a> - " +
                "<a href='/api/admin/sso/myInfo' target='_blank'>账号资料</a>" +
                "</p>";
        return str;
    }

    /*
     * SSO-Client端：处理所有SSO相关请求
     *         http://{host}:{port}/sso/login            -- Client 端登录地址
     *         http://{host}:{port}/sso/logout            -- Client 端注销地址（isSlo=true时打开）
     *         http://{host}:{port}/sso/pushC            -- Client 端接收消息推送地址
     */
/*    @SaIgnore
    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        Map<String, String> paramMap = SaHolder.getRequest().getParamMap();
        System.out.println("param ===" + paramMap.toString());
        return SaSsoClientProcessor.instance.dister();
    }*/

    // SSO-Client：登录地址
    @RequestMapping("/sso/login")
    public Object ssoLogin() {
        Object o = SaSsoClientProcessor.instance.ssoLogin();
        return o;
    }

    // SSO-Client：单点注销地址
    @RequestMapping("/sso/logout")
    public Object ssoLogout() {
        return SaSsoClientProcessor.instance.ssoLogout();
    }

    // SSO-Client：单点注销回调
    @RequestMapping("/sso/logoutCall")
    public Object ssoLogoutCall() {
        return SaSsoClientProcessor.instance.ssoLogoutCall();
    }

    // SSO-Client：接收消息推送地址
    @RequestMapping("/sso/ssoPushC")
    public Object ssoPushC() {
        return SaSsoClientProcessor.instance.ssoPushC();
    }

    // 配置SSO相关参数
    @Autowired
    private void configSso(SaSsoClientTemplate ssoClientTemplate) {

    }

    // 当前应用独自注销 (不退出其它应用)
    @RequestMapping("/sso/logoutByAlone")
    public Object logoutByAlone() {
        StpUtil.logout();
        return SaSsoClientProcessor.instance._ssoLogoutBack(SaHolder.getRequest(), SaHolder.getResponse());
    }

    // 判断当前是否登录
    @RequestMapping("/sso/isLogin")
    public Object isLogin() {
        return SaResult.data(StpUtil.isLogin()).set("loginId", StpUtil.getLoginIdDefaultNull());
    }

    // 返回SSO认证中心登录地址
    @RequestMapping("/sso/getSsoAuthUrl")
    public SaResult getSsoAuthUrl(String clientLoginUrl) {
        String serverAuthUrl = SaSsoClientUtil.buildServerAuthUrl(clientLoginUrl, "");
        return SaResult.data(serverAuthUrl);
    }

    // 根据 ticket 进行登录
    @RequestMapping("/sso/doLoginByTicket")
    public SaResult doLoginByTicket(String ticket) {
        SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket(ticket);
        System.out.println("ctr ==" + JsonUtils.toJsonString(ctr));
        // TODO 执行实际登录
        StpUtil.login(ctr.loginId, new SaLoginParameter()
                .setTimeout(ctr.remainTokenTimeout)
                .setDeviceId(ctr.deviceId)
        );
        return SaResult.data(StpUtil.getTokenValue());
    }

}
