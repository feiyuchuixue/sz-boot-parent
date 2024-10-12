package com.sz.logger.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author sz
 * @date 2022/02/16 09:39
 */
@Data
@SuperBuilder
public class AccessRequestLog {

    private String type;

    /**
     * 请求标识
     */
    private String traceId;

    private String userId;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 开始时间戳
     */
    private long timestamp;

    /**
     * http 方法：GET、POST等
     */
    private String method;

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
    private Object body;

    private String ip;

    private String contentType;

    private Object requestBody;

    {
        type = "request";
    }

}
