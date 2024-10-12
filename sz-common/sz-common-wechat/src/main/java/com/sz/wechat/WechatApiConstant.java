package com.sz.wechat;

/**
 * 微信API常量类
 *
 * @ClassName WechatApiConstant
 * @Author sz
 * @Date 2024/4/26 9:41
 * @Version 1.0
 */
public class WechatApiConstant {

    public static final String WECHAT_API_BASE_URL = "https://api.weixin.qq.com";

    // 小程序接口调用凭证
    public static final String WECHAT_TOKEN_URL = WECHAT_API_BASE_URL + "/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APP_SECRET}";

    // 小程序登录
    public static final String WECHAT_MINI_LOGIN_URL = WECHAT_API_BASE_URL
            + "/sns/jscode2session?appid={APPID}&secret={APP_SECRET}&js_code={JS_CODE}&grant_type=authorization_code&access_token={ACCESS_TOKEN}";

}
