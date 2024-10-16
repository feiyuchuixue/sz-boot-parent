package com.sz.core.common.entity;

import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author: sz
 * @date: 2022/8/25 11:12
 * @description: result parent类
 */
@Data
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "自定义响应码", example = "0000")
    public String code;

    @Schema(description = "响应信息", example = "SUCCESS")
    public String message;

    @Schema(description = "响应数据")
    public T data;

    @Schema(description = "额外参数")
    private Object param = new Object();

    {
        code = "0000";
        message = "SUCCESS";
    }

    public ApiResult() {
    }

    public ApiResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 默认的无参返回结果
     *
     * @param <T>
     * @return
     */
    public static <T> ApiResult success() {
        ApiResult<T> apiResult = new ApiResult();
        apiResult.data = (T) new HashMap<>(1);
        return apiResult;
    }

    /**
     * 有参返回结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.data = (data != null) ? data : (T) new HashMap<>(1);
        return apiResult;
    }

    /**
     * 有参返回结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ApiResult<T> success(T data, Object param) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.data = (data != null) ? data : (T) new HashMap<>(1);
        if (Utils.isNotNull(param)) {
            apiResult.param = param;
        }
        return apiResult;
    }

    public static <T> ApiResult<T> error(CommonResponseEnum responseEnum) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.code = getResponseCode(responseEnum);
        apiResult.message = responseEnum.getMessage();
        apiResult.data = (T) new HashMap<>(1);
        return apiResult;
    }

    protected static String getResponseCode(CommonResponseEnum responseEnum) {
        return responseEnum.getCodePrefixEnum().getPrefix() + responseEnum.getCode();
    }

}
