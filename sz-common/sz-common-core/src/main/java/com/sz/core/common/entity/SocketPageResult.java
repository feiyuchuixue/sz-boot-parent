package com.sz.core.common.entity;

import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.PageUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @date 2023/9/6 10:17
 */
@Data
public class SocketPageResult<T> extends SocketResult<T> {

    public static <T> SocketPageResult<PageResult<T>> success(List<T> data) {
        SocketPageResult<PageResult<T>> apiResult = new SocketPageResult<>();
        apiResult.data = (data != null) ? PageUtils.getPageResult(data) : PageUtils.getPageResult(new ArrayList<>());
        return apiResult;
    }

    public static <T> SocketPageResult<PageResult<T>> success(SocketChannelEnum channel, PageResult<T> data) {
        SocketPageResult<PageResult<T>> apiResult = new SocketPageResult<>();
        apiResult.data = (data != null) ? data : PageUtils.getPageResult(new ArrayList<>());
        apiResult.channel = channel;
        return apiResult;
    }
}
