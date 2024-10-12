package com.sz.wechat.pojo;

import lombok.Data;

/**
 * @ClassName LoginInfoResult
 * @Author sz
 * @Date 2024/4/26 11:27
 * @Version 1.0
 */
@Data
public class LoginInfoResult extends ErrorMessage {

    private String openid;

    private String session_key;

    private String unionid;

}
