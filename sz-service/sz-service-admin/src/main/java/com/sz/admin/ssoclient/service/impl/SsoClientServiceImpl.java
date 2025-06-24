package com.sz.admin.ssoclient.service.impl;


import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.sz.admin.ssoclient.service.SsoClientService;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.LoginUser;
import com.sz.security.core.util.LoginUtils;
import com.sz.security.pojo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * SsoClientServiceImpl - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/6/23
 */
@Service
@RequiredArgsConstructor
public class SsoClientServiceImpl implements SsoClientService {

    private final SysUserService sysUserService;

    /**
     * <h2>这里的登录方法与原client login方法逻辑保持一致， 即`PasswordStrategy.login`</h2>
     * @param ctr SaCheckTicketResult
     * @return LoginVO
     */
    @Override
    public LoginVO login(SaCheckTicketResult ctr){

        String clientId = "195da9fcce574852b850068771cde034";
        SaLoginParameter parameter = new SaLoginParameter();
        parameter.setDeviceType("sso-client3");
        parameter.setDeviceId(ctr.deviceId);
        parameter.setTimeout(ctr.remainTokenTimeout); // 设置超时时间为 sso的 `此账号 token 剩余有效期`
        parameter.setActiveTimeout(ctr.remainTokenTimeout);

        Object loginId = ctr.loginId; // 用户id
        Object centerId = ctr.centerId; // ucenterId

        LoginUser loginUser = sysUserService.buildLoginUser(Long.valueOf(""+ loginId));

        Map<String, Object> extraData = new HashMap<>();
        extraData.put("clientId", clientId);
        extraData.put("userId", loginId);

        LoginUtils.performLogin(loginUser, parameter, extraData);

        LoginVO loginVo = new LoginVO();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setUserInfo(loginUser.getUserInfo());

        return loginVo;
    }


}
