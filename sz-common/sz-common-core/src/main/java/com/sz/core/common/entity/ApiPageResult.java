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
 * 全局通用返回结构
 *
 * @author sz
 * @version 1.0
 * @since 2022/8/23
 */
@Data
public class ApiPageResult<T> extends ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static <T> ApiResult<PageResult<T>> success(List<T> data) {
        ApiResult<PageResult<T>> apiResult = new ApiPageResult<>();
        apiResult.data = (data != null) ? PageUtils.getPageResult(data) : PageUtils.getPageResult(new ArrayList<>());
        return apiResult;
    }

    public static <T> ApiResult<PageResult<T>> success(Page<T> page) {
        return success(PageUtils.getPageResult(page));
    }

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
