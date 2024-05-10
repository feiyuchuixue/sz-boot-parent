package com.sz.redis;

/**
 * 公用redis 常量 key
 *
 * @ClassName CommonKeyConstants
 * @Author sz
 * @Date 2023/12/12 9:37
 * @Version 1.0
 */
public class CommonKeyConstants {

    /**
     * 用户信息key
     */
    public static final String USER_INFO = "user_info:${user_name}";

    /**
     * 字典信息
     */
    public static final String SYS_DICT = "sys_dict";

    /**
     * 系统参数信息
     */
    public static final String SYS_CONFIG = "sys_config";

    public static final String TOKEN_SESSION = "Authorization:login:token-session:${token}";

    /**
     * 系统用户登录密码失败次数
     */
    public static final String SYS_PWD_ERR_CNT = "err:pwd-cnt:${username}";

}
