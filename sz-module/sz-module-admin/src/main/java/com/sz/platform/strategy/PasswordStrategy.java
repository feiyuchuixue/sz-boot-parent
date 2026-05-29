package com.sz.platform.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.sz.admin.system.service.SysLoginLogService;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.BaseUserInfo;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.exception.common.BaseException;
import com.sz.core.util.AESUtil;
import com.sz.redis.RedisCache;
import com.sz.security.core.util.LoginUtils;
import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;
import com.sz.security.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码认证策略
 * <p>
 * PasswordStrategy
 *
 * @author sz
 * @version 1.0
 * @since 2024/1/23 10:29
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordStrategy implements IAuthStrategy {

    private final SysUserService sysUserService;

    private final SysLoginLogService sysLoginLogService;

    private final RedisCache redisCache;

    @SneakyThrows
    @Override
    public LoginVO login(LoginInfo info, ClientVO client) {
        LoginVO loginVO = new LoginVO();
        String username = info.getUsername();
        try {
            String clientId = client.getClientId();
            String password = info.getPassword(); // 前端加密后的密码
            String secretKey = redisCache.getLoginSecret(info.getRequestId());
            String passwordReal = AESUtil.aesDecrypt(password, secretKey, info.getIv()); // 解密后的密码
            redisCache.clearLoginSecret(info.getRequestId()); // 清除临时密钥
            // 设置登录模型
            SaLoginParameter model = createLoginModel(client);
            LoginUser loginUser = sysUserService.buildLoginUser(username, passwordReal);
            Long userId = loginUser.getUserInfo().getId();
            // 设置jwt额外数据
            Map<String, Object> extraData = createExtraData(clientId, userId);
            // 执行登录
            LoginUtils.performLogin(loginUser, model, extraData);
            loginVO = createLoginVO(loginUser.getUserInfo());
            sysLoginLogService.recordLoginLog(info.getUsername(), "1009001", "登陆成功");
        } catch (BaseException e) {
            sysLoginLogService.recordLoginLog(username, "1009002", e.getMessage());
            throw e;
        } catch (Exception e) {
            sysLoginLogService.recordLoginLog(username, "1009002", "系统异常:" + e.getMessage());
            throw e;
        }
        return loginVO;
    }

    private SaLoginParameter createLoginModel(ClientVO client) {
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(client.getDeviceTypeCd());
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
