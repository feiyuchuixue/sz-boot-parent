package com.sz.core.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ApiResult
 *
 * @author sz
 * @version 1.0
 * @since 2025/1/12
 */
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@Data
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "自定义响应码", example = "0000")
    public String code = "0000";

    @Schema(description = "响应信息", example = "SUCCESS")
    public String message = "SUCCESS";

    @Schema(description = "响应数据")
    public T data;

    @Schema(description = "额外参数")
    private Object param = new Object();

    public ApiResult() {
    }

    public ApiResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResult<T> success() {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.data = null;
        return apiResult;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.data = data;
        return apiResult;
    }

    public static <T> ApiResult<T> success(T data, Object param) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.data = data;
        if (Utils.isNotNull(param)) {
            apiResult.param = param;
        }
        return apiResult;
    }

    public static <T> ApiResult<T> error(CommonResponseEnum responseEnum) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.code = getResponseCode(responseEnum);
        apiResult.message = responseEnum.getMessage();
        apiResult.data = null;
        return apiResult;
    }

    protected static String getResponseCode(CommonResponseEnum responseEnum) {
        return responseEnum.getCodePrefixEnum().getPrefix() + responseEnum.getCode();
    }

}
