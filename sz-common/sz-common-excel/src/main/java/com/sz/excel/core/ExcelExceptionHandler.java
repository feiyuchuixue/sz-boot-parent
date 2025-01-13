package com.sz.excel.core;

import cn.idev.excel.exception.ExcelAnalysisException;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author sz
 * @since 2024/12/27 16:21
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class ExcelExceptionHandler {

    @ExceptionHandler(value = ExcelAnalysisException.class)
    public ApiResult<Object> handlerNotPermissionException(ExcelAnalysisException e) {
        CommonResponseEnum message = CommonResponseEnum.EXCEL_IMPORT_ERROR.message(e.getMessage());
        // e.printStackTrace(); // 调试时可打开
        return ApiResult.error(message);
    }

}
