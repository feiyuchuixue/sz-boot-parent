package com.sz.wechat.pojo;

import lombok.Data;

/**
 * 微信access_token返回结果
 * 
 * @author sz
 * @since 2024/4/26 11:12
 * @version 1.0
 */
@Data
public class AccessTokenResult extends ErrorMessage {

    private String access_token;

    private Integer expires_in;

}
