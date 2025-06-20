package com.sz.admin.ssoclient.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.sz.admin.ssoclient.pojo.LoginStatus;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.util.JsonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SsoClientController - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/6/20
 */
@Tag(name = "SSO客户端", description = "SSO客户端相关接口")
@RestController
@RequestMapping("/sso")
@SaIgnore
public class SsoClientController {

    // 当前是否登录
    @GetMapping("/isLogin")
    public ApiResult<LoginStatus> isLogin() {
        LoginStatus status = new LoginStatus();
        status.setHasLogin(StpUtil.isLogin());
        status.setLoginId(StpUtil.getLoginIdDefaultNull());
        return ApiResult.success(status);
    }

    // 返回SSO认证中心登录地址
    @GetMapping("/getSsoAuthUrl")
    public ApiResult<String> getSsoAuthUrl(String clientLoginUrl) {
        String serverAuthUrl = SaSsoClientUtil.buildServerAuthUrl(clientLoginUrl, "");
        System.out.println("serverAuthUrl===" + serverAuthUrl);
        return ApiResult.success(serverAuthUrl);
    }

    // 根据ticket进行登录
    @GetMapping("/doLoginByTicket")
    public ApiResult<String> doLoginByTicket(String ticket) {
        System.out.println(" ticket 登录 ===========================");
        SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket(ticket);
        System.out.println("ctr ===" + JsonUtils.toJsonString(ctr));
        // ========================== 未完续待 ... ============================
        // TODO 根据 client 自身业务 进行login 改造
        StpUtil.login(ctr.loginId, new SaLoginParameter().setTimeout(ctr.remainTokenTimeout).setDeviceId(ctr.deviceId));
        return ApiResult.success(StpUtil.getTokenValue());
    }

}
