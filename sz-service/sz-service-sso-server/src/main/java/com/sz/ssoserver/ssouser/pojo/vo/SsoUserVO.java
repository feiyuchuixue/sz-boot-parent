package com.sz.ssoserver.ssouser.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * SsoUser返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Data
@Schema(description = "SsoUser返回vo")
public class SsoUserVO {

    @Schema(description = "用户唯一标识")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "头像地址")
    private String logo;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户状态")
    private String platformUserStatusCd;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上一次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "上一次登录ip")
    private String lastLoginIp;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}