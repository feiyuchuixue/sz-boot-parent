package com.sz.core.common.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author sz
 * @since 2023/2/20 16:33
 */

@Data
@SuperBuilder
public class AccessResponseLog {

    private String traceId;

    /**
     * 开始时间戳
     */
    private long timestamp;

    private String userId;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求耗时 (ms)
     */
    private long ms;

    /**
     * 响应结果
     */
    private Object resBody;

    /**
     * url 参数体
     */
    private Object param;

    /**
     * form表单参数体
     */
    private Object form;

    /**
     * post body参数体
     */
    private Object reqBody;

    private String type = "response";

    private String method;

}
