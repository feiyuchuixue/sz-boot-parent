package com.sz.wechat.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sz
 * @since 2024/4/26 11:27
 * @version 1.0
 */
@Data
public class LoginInfoResult extends ErrorMessage {

    private String openid;

    @JsonProperty("session_key")
    private String sessionKey;

    @JsonProperty("unionid")
    private String unionId;

}
