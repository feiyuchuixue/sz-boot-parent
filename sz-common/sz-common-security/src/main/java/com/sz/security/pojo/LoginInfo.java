package com.sz.security.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录信息
 *
 * @ClassName LoginInfo
 * @Author sz
 * @Date 2024/1/22 9:38
 * @Version 1.0
 */
@Data
public class LoginInfo {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "客户端id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String clientId;

    @Schema(description = "授权类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String grantType;

    @Schema(description = "微信小程序登录code")
    private String code;

}
