package com.sz.core.common.exception;

import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.exception.common.BaseException;
import com.sz.core.common.exception.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: sz
 * @date: 2022/8/23 10:56
 * @description: 全局异常捕获处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        e.printStackTrace();
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 业务异常
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ApiResult handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return new ApiResult(e.getResponseEnum().getCode() + "", e.getMessage());
    }

    /**
     * 自定义异常
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ApiResult handleBaseException(BaseException e) {
        log.error(e.getMessage(), e);
        return new ApiResult(e.getResponseEnum().getCode() + "", e.getMessage());
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ApiResult apiResult = wrapperBindingResult(e);
        return new ResponseEntity(apiResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * 参数绑定异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        ApiResult apiResult = wrapperBindingResult(e);
        return new ResponseEntity(apiResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ApiResult wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(", ");
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
        }
        return new ApiResult(CommonResponseEnum.VALID_ERROR.getCode() + "", msg.substring(2));
    }

    private MultiValueMap<String, String> setHeader() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        // headers.add("X-Trace-Id", TraceLocal.getTraceId());
        return headers;
    }

}
