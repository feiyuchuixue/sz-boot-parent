package com.sz.admin.ssoclient.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.admin.ssoclient.pojo.LoginStatus;
import com.sz.admin.ssoclient.service.SsoClientService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.util.JsonUtils;
import com.sz.platform.CenterUserConvertService;
import com.sz.security.pojo.LoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
public class SsoClientController {

    private final SsoClientService ssoClientService;

    private final CenterUserConvertService centerUserConvertService;

    // 配置SSO相关参数
    @Autowired
    private void configSso(SaSsoClientTemplate ssoClientTemplate) {
        // 重写 loginId 与 centerId 转换策略函数，做到本地应用 userId 与认证中心 userId 的互相映射

        // 将 centerId 转换为 loginId 的函数
        ssoClientTemplate.strategy.convertCenterIdToLoginId = (centerId) -> {
            System.out.println(" -------------------- 将 centerId 转换为 loginId 的函数");
            return centerUserConvertService.convertCenterIdToUserId(centerId);
        };
        // 将 loginId 转换为 centerId 的函数
        ssoClientTemplate.strategy.convertLoginIdToCenterId = (loginId) -> {
            System.out.println(" -------------------- 将 loginId 转换为 centerId 的函数");
            return centerUserConvertService.convertUserIdToCenterId(loginId);
        };
    }

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
    public ApiResult<LoginVO> doLoginByTicket(String ticket) {
        System.out.println(" ticket 登录 ===========================");
        SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket(ticket);
        System.out.println("ctr ===" + JsonUtils.toJsonString(ctr));
        // TODO 根据 client 自身业务 进行login 改造
        LoginVO loginVO = ssoClientService.login(ctr);
        System.out.println("loginVO ==" + JsonUtils.toJsonString(loginVO));
        return ApiResult.success(loginVO);
    }

    // SSO-Client：单点注销地址
    @RequestMapping("/logout")
    public ApiResult<Void> ssoLogout() {
        SaSsoClientProcessor.instance.ssoLogout();
        return ApiResult.success();
    }

    // SSO-Client：单点注销回调
    @RequestMapping("/logoutCall")
    public Object ssoLogoutCall() {
        return SaSsoClientProcessor.instance.ssoLogoutCall();
    }

    // SSO-Client：接收消息推送地址
    @RequestMapping("/ssoPushC")
    public Object ssoPushC() {
        Object o = SaSsoClientProcessor.instance.ssoPushC();
        System.out.println("接收消息推送地址返回：" + o);
        return o;
    }


}
