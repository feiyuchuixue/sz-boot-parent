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
 * @ClassName WechatService
 * @Author sz
 * @Date 2024/4/26 10:04
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WechatService {

    private final WechatProperties wechatProperties;

    private final static String WECHAT_MINI_TOKEN = "wechat:mini:token";

    /**
     * 获取accessToken【小程序】
     *
     * @return
     */
    public String getAccessToken() {
        if (RedisUtils.hasKey(WECHAT_MINI_TOKEN)) {
            return (String) RedisUtils.getValue(WECHAT_MINI_TOKEN);
        } else {
            ResponseEntity<AccessTokenResult> entity = RestClient.create().get()
                    .uri(WECHAT_TOKEN_URL, wechatProperties.getMini().getAppId(), wechatProperties.getMini().getAppSecret()).retrieve()
                    .toEntity(AccessTokenResult.class);
            AccessTokenResult result = entity.getBody();
            if (validSuccess(result)) {
                Integer expireTime = result.getExpires_in() - 1200;
                RedisUtils.getRestTemplate().opsForValue().set(WECHAT_MINI_TOKEN, result.getAccess_token(), expireTime, TimeUnit.SECONDS);
                return result.getAccess_token();
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
     * @param accessToken
     * @return
     */
    public LoginInfoResult miniLogin(String code, String accessToken) {
        // 微信小程序登录接口返回content-type是text/plain，因此无法直接映射对象。使用String接收，后续再做转换
        ResponseEntity<String> entity = RestClient.create().get()
                .uri(WECHAT_MINI_LOGIN_URL, wechatProperties.getMini().getAppId(), wechatProperties.getMini().getAppSecret(), code, accessToken).retrieve()
                .toEntity(String.class);
        LoginInfoResult loginInfoResult = JsonUtils.parseObject(entity.getBody(), LoginInfoResult.class);
        return loginInfoResult;
    }

    /**
     * 校验微信返回结果是否成功
     *
     * @param errorMessage
     * @return
     */
    public boolean validSuccess(ErrorMessage errorMessage) {
        return errorMessage.getErrcode() == null || errorMessage.getErrcode() == 0;
    }

}
