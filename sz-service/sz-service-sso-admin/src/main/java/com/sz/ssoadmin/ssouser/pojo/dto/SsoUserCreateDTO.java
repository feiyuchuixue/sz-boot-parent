package com.sz.ssoadmin.ssouser.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SsoUser添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Data
@Schema(description = "SsoUser添加DTO")
public class SsoUserCreateDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像地址")
    private String logo;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户状态")
    private String platformUserStatusCd;

}