package com.sz.redis;

/**
 * 公用 Redis Key 常量类
 * <p>
 * 该类包含了用于 Redis 操作的常量 Redis Key，方便在整个项目中统一使用和管理。
 *
 * @author sz
 * @since 2023/12/12 9:37
 * @since 2023-12-12
 */
public class CommonKeyConstants {

    private CommonKeyConstants() {
        throw new IllegalStateException("Utility class");
    }

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
