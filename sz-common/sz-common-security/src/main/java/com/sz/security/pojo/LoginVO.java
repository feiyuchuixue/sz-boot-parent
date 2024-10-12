package com.sz.security.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName LoginGlobalVO
 * @Author sz
 * @Date 2024/1/22 15:35
 * @Version 1.0
 */
@Data
@Schema(description = "通用用户返回")
public class LoginVO {

    @Schema(description = "access_token")
    private String accessToken;

    @Schema(description = "授权令牌 access_token 的有效期")
    private Long expireIn;

    @Schema(description = "用户信息")
    private Object UserInfo;

}
