package com.sz.security.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理器
 * <p>
 * - NotLoginException → HTTP 401（token 失效/未登录） - NotPermissionException → HTTP
 * 403（无接口权限） - NotRoleException → HTTP 403（无角色权限）
 *
 * @author sz
 * @since 2024/2/4 15:42
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class SaExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResult<Void>> handlerNotLoginException(NotLoginException e) {
        String message;
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效 token";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN) || e.getType().equals(NotLoginException.TOKEN_FREEZE)) {
            message = "您的登录信息已过期，请重新登录以继续访问。";
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
        String code = CommonResponseEnum.INVALID_TOKEN.getCodePrefixEnum().getPrefix() + CommonResponseEnum.INVALID_TOKEN.getCode();
        ApiResult<Void> body = new ApiResult<>(code, message);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<ApiResult<Void>> handlerNotPermissionException(NotPermissionException e) {
        ApiResult<Void> body = ApiResult.error(CommonResponseEnum.INVALID_PERMISSION);
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<ApiResult<Void>> handlerNotRoleException(NotRoleException e) {
        ApiResult<Void> body = ApiResult.error(CommonResponseEnum.INVALID_PERMISSION);
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

}
