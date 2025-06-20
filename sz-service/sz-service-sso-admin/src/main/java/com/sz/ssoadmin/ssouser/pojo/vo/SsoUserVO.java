package com.sz.ssoadmin.ssouser.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;
import org.springframework.format.annotation.DateTimeFormat;

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

    @ExcelIgnore
    @Schema(description = "用户唯一标识")
    private Long id;

    @ExcelProperty(value = "用户名")
    @Schema(description = "用户名")
    private String username;

    @ExcelProperty(value = "性别")
    @DictFormat(dictType = "user_sex")
    @Schema(description = "性别")
    private String sex;

    @ExcelProperty(value = "头像地址")
    @Schema(description = "头像地址")
    private String logo;

    @ExcelProperty(value = "昵称")
    @Schema(description = "昵称")
    private String nickname;

    @ExcelProperty(value = "手机号")
    @Schema(description = "手机号")
    private String phone;

    @ExcelProperty(value = "邮箱")
    @Schema(description = "邮箱")
    private String email;

    @ExcelProperty(value = "用户状态")
    @DictFormat(dictType = "platform_user_status")
    @Schema(description = "用户状态")
    private String platformUserStatusCd;

    @ExcelProperty(value = "上一次登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上一次登录时间")
    private LocalDateTime lastLoginTime;

    @ExcelProperty(value = "上一次登录ip")
    @Schema(description = "上一次登录ip")
    private String lastLoginIp;

    @ExcelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @ExcelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}