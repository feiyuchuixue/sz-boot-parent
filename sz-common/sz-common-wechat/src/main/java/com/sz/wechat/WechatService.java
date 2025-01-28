package com.sz.wechat;

import com.sz.core.util.JsonUtils;
import com.sz.redis.RedisUtils;
import com.sz.wechat.pojo.AccessTokenResult;
import com.sz.wechat.pojo.ErrorMessage;
import com.sz.wechat.pojo.LoginInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

import static com.sz.wechat.WechatApiConstant.WECHAT_MINI_LOGIN_URL;
import static com.sz.wechat.WechatApiConstant.WECHAT_TOKEN_URL;

/**
 * @author sz
 * @since 2024/4/26 10:04
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WechatService {

    private final WechatProperties wechatProperties;

    private static final String WECHAT_MINI_TOKEN = "wechat:mini:token";

    /**
     * 获取accessToken【小程序】
     *
     * @return accessToken
     */
    public String getAccessToken() {
        if (RedisUtils.hasKey(WECHAT_MINI_TOKEN)) {
            return (String) RedisUtils.getValue(WECHAT_MINI_TOKEN);
        } else {
            ResponseEntity<AccessTokenResult> entity = RestClient.create().get()
                    .uri(WECHAT_TOKEN_URL, wechatProperties.getMini().getAppId(), wechatProperties.getMini().getAppSecret()).retrieve()
                    .toEntity(AccessTokenResult.class);
            AccessTokenResult result = entity.getBody();
            assert result != null;
            if (validSuccess(result)) {
                int expireTime = result.getExpiresIn() - 1200;
                RedisUtils.getRestTemplate().opsForValue().set(WECHAT_MINI_TOKEN, result.getAccessToken(), expireTime, TimeUnit.SECONDS);
                return result.getAccessToken();
            } else {
                log.error("【微信小程序】 获取accessToken失败，错误码：{}，错误信息：{}", result.getErrcode(), result.getErrmsg());
                return "";
            }
        }
    }

    /**
     * 微信小程序登录
     *
     * @param code
     *            code
     * @param accessToken
     *            accessToken
     * @return 登录信息
     */
    public LoginInfoResult miniLogin(String code, String accessToken) {
        // 微信小程序登录接口返回content-type是text/plain，因此无法直接映射对象。使用String接收，后续再做转换
        ResponseEntity<String> entity = RestClient.create().get()
                .uri(WECHAT_MINI_LOGIN_URL, wechatProperties.getMini().getAppId(), wechatProperties.getMini().getAppSecret(), code, accessToken).retrieve()
                .toEntity(String.class);
        return JsonUtils.parseObject(entity.getBody(), LoginInfoResult.class);
    }

    /**
     * 校验微信返回结果是否成功
     *
     * @param errorMessage
     *            微信返回结果
     * @return 是否成功
     */
    public boolean validSuccess(ErrorMessage errorMessage) {
        return errorMessage.getErrcode() == null || errorMessage.getErrcode() == 0;
    }

}
