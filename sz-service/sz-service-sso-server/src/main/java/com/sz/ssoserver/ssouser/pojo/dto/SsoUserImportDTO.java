package com.sz.ssoserver.ssouser.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
/**
 * <p>
 * SsoUser导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Data
@Schema(description = "SsoUser导入DTO")
public class SsoUserImportDTO {

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

    @Schema(description = "上一次登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "上一次登录ip")
    private String lastLoginIp;

}