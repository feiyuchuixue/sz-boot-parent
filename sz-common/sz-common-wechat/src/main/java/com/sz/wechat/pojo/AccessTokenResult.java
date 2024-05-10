package com.sz.wechat.pojo;

import lombok.Data;

/**
 * @Description 微信access_token返回结果
 * @ClassName AccessTokenResult
 * @Author sz
 * @Date 2024/4/26 11:12
 * @Version 1.0
 */
@Data
public class AccessTokenResult extends ErrorMessage {

    private String access_token;

    private Integer expires_in;

}
