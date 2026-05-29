package com.sz.core.common.enums;

import org.springframework.http.HttpStatus;

/**
 * 异常枚举类
 */
public enum CommonResponseEnum implements ResponseEnumTemplate<CommonResponseEnum> {
    // @formatter:off
    // ---------- 参数校验 ----------
    VALID_ERROR(100, "参数校验异常", HttpStatus.BAD_REQUEST),

    // ---------- 认证（401）----------
    BAD_USERNAME_OR_PASSWORD(101, "账户不存在或密码错误", HttpStatus.UNAUTHORIZED),
    CNT_PASSWORD_ERR(102, "密码错误次数过多，账户锁定！", HttpStatus.UNAUTHORIZED),
    CLIENT_INVALID(103, "无效的ClientId", HttpStatus.UNAUTHORIZED),
    CLIENT_BLOCKED(104, "Client认证已禁用", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(105, "无效Token", HttpStatus.UNAUTHORIZED),
    INVALID_USER(106, "无效用户", HttpStatus.UNAUTHORIZED),
    BAD_USERNAME_STATUS_INVALID(107, "用户被禁用", HttpStatus.UNAUTHORIZED),

    // ---------- 鉴权（403）----------
    INVALID_PERMISSION(108, "抱歉，您目前无权执行此操作，请联系管理员获取相应权限。", HttpStatus.FORBIDDEN),

    // ---------- 服务内部（500）----------
    WEBSOCKET_SEND_FAIL(109, "WebSocket消息发送异常", HttpStatus.INTERNAL_SERVER_ERROR),

    // ---------- 限流/防抖（429）----------
    DEBOUNCE(110, "您的请求过于频繁，请稍后再试！", HttpStatus.TOO_MANY_REQUESTS),

    // ---------- 业务参数不合法（400）----------
    INVALID_ID(1000, "无效ID", HttpStatus.BAD_REQUEST),
    EXISTS(1001, "已存在", HttpStatus.CONFLICT),
    NOT_EXISTS(1002, "不存在", HttpStatus.NOT_FOUND),
    FILE_NOT_EXISTS(1003, "文件不存在", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_EXT_ERROR(1004, "文件类型不被允许，请检查文件扩展名和MIME类型", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_SIZE_ERROR(1005,"上传文件大小不能超过10MB", HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_UPLOAD_ERROR(1006, "上传文件失败", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTS(1007, "用户名已存在", HttpStatus.CONFLICT),
    INVALID(1008, "无效的数据", HttpStatus.BAD_REQUEST),
    EXCEL_IMPORT_ERROR(1009, "Excel导入失败", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPTCHA_LACK(1010, "验证参数缺失", HttpStatus.BAD_REQUEST),
    CAPTCHA_EXPIRED(1011, "验证码失效，请重新获取", HttpStatus.BAD_REQUEST),
    CAPTCHA_FAILED(1012, "验证失败", HttpStatus.BAD_REQUEST),
    CAPTCHA_LIMIT(1013, "验证码请求已达到最大次数", HttpStatus.TOO_MANY_REQUESTS),
    BACKGROUND_NOT_EXISTS(1014, "背景图片不存在", HttpStatus.NOT_FOUND),
    LOGIN_LIMIT(1015, "登录请求已达到最大次数",HttpStatus.TOO_MANY_REQUESTS),
    FILE_TEMPLATE_INVALID(1016, "文件地址无效", HttpStatus.BAD_REQUEST),
    MENU_NAME_EXISTS(1017, "Menu路由名称已存在", HttpStatus.CONFLICT),

    // ---------- 未知兜底（500）----------
    UNKNOWN(9999, "未知异常", HttpStatus.INTERNAL_SERVER_ERROR),;
    // @formatter:on
    /**
     * 返回码
     */
    private final int code;

    /**
     * 返回消息
     */
    private final String message;

    /**
     * 对应 HTTP 状态码
     */
    private final HttpStatus httpStatus;

    CommonResponseEnum(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public ErrorPrefixEnum getCodePrefixEnum() {
        return ErrorPrefixEnum.COMMON;
    }

    @Override
    public HttpStatus httpStatus() {
        return this.httpStatus;
    }
}