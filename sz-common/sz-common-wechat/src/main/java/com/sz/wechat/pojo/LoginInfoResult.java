package com.sz.wechat.pojo;

import lombok.Data;

/**
 * @author sz
 * @since 2024/4/26 11:27
 * @version 1.0
 */
@Data
public class LoginInfoResult extends ErrorMessage {

    private String openid;

    private String session_key;

    private String unionid;

}
