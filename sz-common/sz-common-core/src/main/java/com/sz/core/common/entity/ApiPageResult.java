package com.sz.core.common.entity;

import com.mybatisflex.core.paginate.Page;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: sz
 * @date: 2022/8/23 10:22
 * @description: 全局通用返回结构(分页)
 */
@Data
public class ApiPageResult<T> extends ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    {
        code = "0000";
        message = "SUCCESS";
    }

    /**
     * 有参返回结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ApiResult<PageResult<T>> success(List<T> data) {
        ApiResult<PageResult<T>> apiResult = new ApiPageResult<>();
        apiResult.data = (data != null) ? PageUtils.getPageResult(data) : PageUtils.getPageResult(new ArrayList<>());
        return apiResult;
    }

    public static <T> ApiResult<PageResult<T>> success(Page<T> page) {
        return success(PageUtils.getPageResult(page));
    }

    /**
     * 有参返回结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ApiResult<PageResult<T>> success(List<T> data, Object param) {
        ApiResult<PageResult<T>> apiResult = new ApiPageResult<>();
        apiResult.data = (data != null) ? PageUtils.getPageResult(data) : PageUtils.getPageResult(new ArrayList<>());
        apiResult.setParam(param);
        return apiResult;
    }

    public static <T> ApiPageResult<PageResult<T>> success(PageResult<T> data) {
        ApiPageResult<PageResult<T>> apiResult = new ApiPageResult<>();
        apiResult.data = (data != null) ? data : PageUtils.getPageResult(new ArrayList<>());
        return apiResult;
    }

    public static <T> ApiPageResult<T> error(CommonResponseEnum responseEnum) {
        ApiPageResult<T> apiResult = new ApiPageResult<>();
        apiResult.setCode(getResponseCode(responseEnum));
        apiResult.setMessage(responseEnum.getMessage());
        return apiResult;
    }
}
