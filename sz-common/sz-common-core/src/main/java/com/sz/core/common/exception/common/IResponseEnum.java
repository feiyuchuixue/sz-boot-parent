package com.sz.core.common.exception.common;

import com.sz.core.common.enums.ErrorPrefixEnum;
import org.springframework.http.HttpStatus;

public interface IResponseEnum {

    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    String getMessage();

    /**
     * 获取错误码前缀
     * 
     * @return 错误码前缀
     */
    ErrorPrefixEnum getCodePrefixEnum();

    /**
     * 获取对应的 HTTP 状态码
     * <p>
     * 各枚举可覆盖此方法以声明语义明确的 HTTP 状态码； 默认返回 400 Bad Request 作为通用业务异常兜底。
     *
     * @return HTTP 状态码
     */
    default HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
