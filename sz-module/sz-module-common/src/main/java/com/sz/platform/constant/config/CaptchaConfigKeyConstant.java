package com.sz.platform.constant.config;

/**
 * 验证码相关配置 key 常量 对应 sys_config.config_key 前缀 "sys.captcha.*"
 */
public final class CaptchaConfigKeyConstant {

    private CaptchaConfigKeyConstant() {
    }

    /** 验证码开关状态 */
    public static final String STATE = "sys.captcha.state";

    /** 验证码过期时间（秒） */
    public static final String EXPIRE = "sys.captcha.expire";

    /** 验证码请求次数限制 */
    public static final String REQUEST_LIMIT = "sys.captcha.requestLimit";

    /** 验证码请求周期（秒） */
    public static final String REQUEST_CYCLE = "sys.captcha.requestCycle";

    /** 水印文字 */
    public static final String WATER_TEXT = "sys.captcha.waterText";

    /** 是否启用水印 */
    public static final String WATER_ENABLE = "sys.captcha.waterEnable";

    /** 水印字体 */
    public static final String WATER_FONT = "sys.captcha.waterFont";
}
