package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName UserInfo
 * @Author sz
 * @Date 2023/12/12 9:53
 * @Version 1.0
 */
@Data
public class BaseUserInfo {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "邮箱")
    private String email;

}
