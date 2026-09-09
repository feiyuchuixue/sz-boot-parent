package com.sz.platform.constant.config;

/**
 * 用户账户相关配置 key 常量 对应 sys_config.config_key 前缀 "sys.user.*" / "sys.pwd.*" /
 * "sys_pwd.*"
 */
public final class UserConfigKeyConstant {

    private UserConfigKeyConstant() {
    }

    /** 新用户初始密码 */
    public static final String INIT_PWD = "sys.user.initPwd";

    /** 密码最大错误次数 */
    public static final String PWD_ERR_CNT = "sys.pwd.errCnt";

    /**
     * 密码锁定时间（分钟） 注意：数据库中 config_key 为 "sys_pwd.lockTime"（前缀使用下划线），与其他 key
     * 风格不同，常量值与数据库保持一致。
     */
    public static final String PWD_LOCK_TIME = "sys_pwd.lockTime";
}
