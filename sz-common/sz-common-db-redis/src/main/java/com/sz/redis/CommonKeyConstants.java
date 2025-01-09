package com.sz.redis;

/**
 * 公用 redis key 常量
 *
 * @ClassName CommonKeyConstants
 * @Author sz
 * @Date 2023/12/12 9:37
 * @Version 1.0
 */
public class CommonKeyConstants {

    /**
     * 字典信息
     */
    public static final String SYS_DICT = "sys_dict";

    /**
     * 系统参数信息
     */
    public static final String SYS_CONFIG = "sys_config";

    /**
     * sa-token token信息
     */
    public static final String TOKEN_SESSION = "Authorization:login:token-session:${token}";

    /**
     * 系统用户登录密码失败次数
     */
    public static final String SYS_PWD_ERR_CNT = "err:pwd-cnt:${username}";

    /**
     * 验证码
     */
    public static final String CAPTCHA_REQUEST_ID = "captcha:slide:${requestId}";

    /**
     * 验证码请求次数限制
     */
    public static final String CAPTCHA_REQUEST_LIMIT = "captcha:request-limit:${requestId}";

}
