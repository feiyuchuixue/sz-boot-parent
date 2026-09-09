package com.sz.platform.constant.config;

/**
 * 登录相关配置 key 常量 对应 sys_config.config_key 前缀 "sys.login.*"
 */
public final class LoginConfigKeyConstant {

    private LoginConfigKeyConstant() {
    }

    /** 登录请求次数限制 */
    public static final String REQUEST_LIMIT = "sys.login.requestLimit";

    /** 登录请求周期（秒） */
    public static final String REQUEST_CYCLE = "sys.login.requestCycle";
}
