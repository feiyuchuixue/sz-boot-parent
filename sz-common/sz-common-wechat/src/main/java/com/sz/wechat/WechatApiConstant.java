package com.sz.wechat;

/**
 * 微信API常量类
 *
 * @author sz
 * @since 2024/4/26 9:41
 * @version 1.0
 */
public class WechatApiConstant {

    private WechatApiConstant() {
        throw new IllegalStateException("Utility class");
    }

    public static final String WECHAT_API_BASE_URL = "https://api.weixin.qq.com";

    // 小程序接口调用凭证
    public static final String WECHAT_TOKEN_URL = WECHAT_API_BASE_URL + "/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APP_SECRET}";

    // 小程序登录
    public static final String WECHAT_MINI_LOGIN_URL = WECHAT_API_BASE_URL
            + "/sns/jscode2session?appid={APPID}&secret={APP_SECRET}&js_code={JS_CODE}&grant_type=authorization_code&access_token={ACCESS_TOKEN}";

    // ----------------------- 企业微信 ---------------------------
    private static final String WORK_WECHAT_API_BASE_URL = "https://qyapi.weixin.qq.com";

    // 企业微信接口access_token
    public static final String WORK_WECHAT_TOKEN_URL = WORK_WECHAT_API_BASE_URL + "/cgi-bin/gettoken?corpid={CORPID}&corpsecret={CORPSECRET}";

    // 企业微信发送消息
    public static final String WORK_WECHAT_MESSAGE_SEND_URL = WORK_WECHAT_API_BASE_URL + "/cgi-bin/message/send?access_token={ACCESS_TOKEN}";

}
