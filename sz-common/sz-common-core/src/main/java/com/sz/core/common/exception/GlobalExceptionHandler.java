package com.sz.core.common.exception;

import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.exception.common.BaseException;
import com.sz.core.common.exception.common.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常捕获处理
 *
 * @author sz
 * @since 2022/8/23 10:56
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Object>> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 业务异常
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        ApiResult<Void> body = new ApiResult<>(getCode(e), e.getMessage());
        return new ResponseEntity<>(body, e.getResponseEnum().httpStatus());
    }

    /**
     * 自定义异常
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<ApiResult<Void>> handleBaseException(BaseException e) {
        log.error(e.getMessage(), e);
        ApiResult<Void> body = new ApiResult<>(getCode(e), e.getMessage());
        return new ResponseEntity<>(body, e.getResponseEnum().httpStatus());
    }

    /**
     * 参数校验异常（请求体字段不合法）
     *
     * @param e
     *            参数校验异常
     * @return 异常结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Object>> handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ApiResult<Object> apiResult = wrapperBindingResult(e);
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数绑定异常（GET/表单参数绑定失败）
     *
     * @param e
     *            参数绑定异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ApiResult<Object>> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        ApiResult<Object> apiResult = wrapperBindingResult(e);
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * 单参数校验异常（@Validated + @NotNull 等方法参数）
     *
     * @param e
     *            约束违反异常
     * @return 异常结果
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String code = CommonResponseEnum.VALID_ERROR.getCodePrefixEnum().getPrefix() + CommonResponseEnum.VALID_ERROR.getCode();
        ApiResult<Object> body = new ApiResult<>(code, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求体 JSON 解析失败（格式错误、字段类型不匹配等）
     *
     * @param e
     *            消息不可读异常
     * @return 异常结果
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String code = CommonResponseEnum.VALID_ERROR.getCodePrefixEnum().getPrefix() + CommonResponseEnum.VALID_ERROR.getCode();
        ApiResult<Object> body = new ApiResult<>(code, "请求体格式有误，请检查请求参数");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求方法不支持
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Object>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        ApiResult<Object> body = new ApiResult<>("405", "请求方式不支持: " + e.getMethod());
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 文件上传超出大小限制
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResult<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        ApiResult<Object> body = ApiResult.error(CommonResponseEnum.FILE_UPLOAD_SIZE_ERROR);
        return new ResponseEntity<>(body, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * 请求路径不存在（404）
     *
     * @param e
     *            异常
     * @return 异常结果
     */
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ApiResult<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        if (e.getResourcePath().endsWith(".map")) {
            log.debug(e.getMessage());
        } else {
            log.warn(e.getMessage());
        }
        ApiResult<Object> body = new ApiResult<>("404", "请求资源不存在: " + e.getResourcePath());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // ---------------------------------------------------------------- private

    private ApiResult<Object> wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(", ");
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
        }
        String code = CommonResponseEnum.VALID_ERROR.getCodePrefixEnum().getPrefix() + CommonResponseEnum.VALID_ERROR.getCode();
        return new ApiResult<>(code, msg.substring(2));
    }

    private String getCode(BaseException e) {
        return e.getResponseEnum().getCodePrefixEnum().getPrefix() + e.getResponseEnum().getCode();
    }

}
