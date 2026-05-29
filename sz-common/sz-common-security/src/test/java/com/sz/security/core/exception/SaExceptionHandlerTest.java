package com.sz.security.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class SaExceptionHandlerTest {

    private final SaExceptionHandler handler = new SaExceptionHandler();

    @Test
    @DisplayName("NotLoginException should return HTTP 401 and C105")
    void handlerNotLoginExceptionShouldReturnUnauthorizedWithInvalidTokenCode() {
        NotLoginException exception = new NotLoginException("invalid token", "login", NotLoginException.INVALID_TOKEN);

        ResponseEntity<ApiResult<Void>> response = handler.handlerNotLoginException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(codeOf(CommonResponseEnum.INVALID_TOKEN));
        assertThat(response.getBody().getMessage()).isNotBlank();
    }

    @Test
    @DisplayName("NotPermissionException should return HTTP 403 and C108")
    void handlerNotPermissionExceptionShouldReturnForbiddenWithInvalidPermissionCode() {
        ResponseEntity<ApiResult<Void>> response =
                handler.handlerNotPermissionException(new NotPermissionException("system:user:list", "login"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(codeOf(CommonResponseEnum.INVALID_PERMISSION));
    }

    @Test
    @DisplayName("NotRoleException should return HTTP 403 and C108")
    void handlerNotRoleExceptionShouldReturnForbiddenWithInvalidPermissionCode() {
        ResponseEntity<ApiResult<Void>> response =
                handler.handlerNotRoleException(new NotRoleException("admin", "login"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(codeOf(CommonResponseEnum.INVALID_PERMISSION));
    }

    private static String codeOf(CommonResponseEnum responseEnum) {
        return responseEnum.getCodePrefixEnum().getPrefix() + responseEnum.getCode();
    }

}
