package com.sz.platform.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.applet.miniuser.pojo.po.MiniLoginUser;
import com.sz.applet.miniuser.service.MiniUserService;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.Utils;
import com.sz.security.core.util.LoginUtils;
import com.sz.security.pojo.ClientVO;
import com.sz.security.pojo.LoginInfo;
import com.sz.security.pojo.LoginVO;
import com.sz.security.service.IAuthStrategy;
import com.sz.wechat.WechatService;
import com.sz.wechat.pojo.LoginInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序认证策略
 *
 * @ClassName AppletStrategy
 * @Author sz
 * @Date 2024/4/26 16:08
 * @Version 1.0
 */

@Slf4j
@Service("applet" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class AppletStrategy implements IAuthStrategy {

    private final WechatService wechatService;

    private final MiniUserService miniUserService;

    @Override
    public LoginVO login(LoginInfo info, ClientVO client) {
        String clientId = client.getClientId();
        String code = info.getCode();
        CommonResponseEnum.INVALID.message("无效的小程序code").assertFalse(Utils.isNotNull(code));

        String accessToken = wechatService.getAccessToken();
        LoginInfoResult result = wechatService.miniLogin(code, accessToken);
        log.info(" 小程序登录返回信息：{}", JsonUtils.toJsonString(result));
        String openid = result.getOpenid();
        String unionid = result.getUnionid();
        String sessionKey = result.getSession_key(); // 小程序登录凭证

        MiniLoginUser miniLoginUser = miniUserService.getUserByOpenId(openid, unionid);

        // 设置登录模型
        SaLoginModel model = createLoginModel(client);
        Long userId = miniLoginUser.getUserId();
        // 设置jwt额外数据
        Map<String, Object> extraData = createExtraData(clientId, userId);
        // 执行登录
        LoginUtils.performMiniLogin(userId, miniLoginUser, model, extraData);
        // 构造返回对象
        return createLoginVO(miniLoginUser);
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

    private LoginVO createLoginVO(MiniLoginUser miniLoginUser) {
        LoginVO loginVo = new LoginVO();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setUserInfo(miniLoginUser);
        return loginVo;
    }

}
