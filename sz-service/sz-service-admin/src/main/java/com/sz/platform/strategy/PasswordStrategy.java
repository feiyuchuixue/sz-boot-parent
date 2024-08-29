package com.sz.platform.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.BaseUserInfo;
import com.sz.core.common.entity.LoginUser;
import com.sz.security.core.util.LoginUtils;
import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;
import com.sz.security.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码认证策略
 *
 * @ClassName PasswordStrategy
 * @Author sz
 * @Date 2024/1/23 10:29
 * @Version 1.0
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordStrategy implements IAuthStrategy {

    private final SysUserService sysUserService;

    @Override
    public LoginVO login(LoginInfo info, ClientVO client) {
        String clientId = client.getClientId();
        String password = info.getPassword();
        String username = info.getUsername();
        // 设置登录模型
        SaLoginModel model = createLoginModel(client);
        // 复制用户信息
        LoginUser loginUser = sysUserService.buildLoginUser(username, password);
        Long userId = loginUser.getUserInfo().getId();
        // 设置jwt额外数据
        Map<String, Object> extraData = createExtraData(clientId, userId);
        // 执行登录
        LoginUtils.performLogin(loginUser, model, extraData);
        // 构造返回对象
        return createLoginVO(loginUser.getUserInfo());
    }

    private SaLoginModel createLoginModel(ClientVO client) {
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceTypeCd());
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        return model;
    }

    private Map<String, Object> createExtraData(String clientId, Long userId) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("clientId", clientId);
        extraData.put("userId", userId);
        return extraData;
    }

    private LoginVO createLoginVO(BaseUserInfo userInfo) {
        LoginVO loginVo = new LoginVO();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setUserInfo(userInfo);
        return loginVo;
    }

}
