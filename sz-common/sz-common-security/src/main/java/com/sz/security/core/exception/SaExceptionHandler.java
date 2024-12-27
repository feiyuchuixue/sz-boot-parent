package com.sz.security.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName SaExceptionHandler
 * @Author sz
 * @Date 2024/2/4 15:42
 * @Version 1.0
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class SaExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ApiResult handlerNotLoginException(NotLoginException e) {
        String message = "";
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效 token";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN) || e.getType().equals(NotLoginException.TOKEN_FREEZE)) {
            message = "您的登录信息已过期，请重新登录以继续访问。";
            // TODO: websocket close
            // sendWsClose();
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token 已过期";
        } else if (e.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "token 已被顶下线";
        } else if (e.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token 已被踢下线";
        } else if (e.getType().equals(NotLoginException.NO_PREFIX)) {
            message = "未按照指定前缀提交 token";
        } else {
            message = "当前会话未登录";
        }
        return new ApiResult<>(CommonResponseEnum.INVALID_TOKEN.getCode() + "", message);
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResult handlerNotPermissionException(NotPermissionException e) {
        return ApiResult.error(CommonResponseEnum.INVALID_PERMISSION);
    }

}
