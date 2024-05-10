package com.sz.redis;

public class RedisKeymapConstants {

    /**
     * 用户refresh
     */
    public final static String AUTH_REFRESH = "auth_refresh:${refreshToken}";

    /**
     * 失效的accessToken
     */
    public final static String AUTH_INVALID_ACCESS = "auth_access:invalid:${accessToken}";

    /**
     * 字典信息
     */
    public final static String SYS_DICT = "sys_dict";


}
