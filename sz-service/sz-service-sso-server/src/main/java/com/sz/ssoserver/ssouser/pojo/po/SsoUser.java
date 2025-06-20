package com.sz.ssoserver.ssouser.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 平台用户表
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-18
 */
@Data
@Table(value = "sso_user", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "平台用户表")
public class SsoUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Schema(description = "平台用户id，sso client 的 center_id映射")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "密码hash")
    private String passwordHash;

    @Schema(description = "平台用户状态，字典platform_user_status")
    private String platformUserStatusCd;

    @Schema(description = "头像地址")
    private String logo;

    @Schema(description = "上一次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "上一次登录ip")
    private String lastLoginIp;

    @Schema(description = "（首次登陆后或密码重置后）是否需要修改密码（非注册用户需要更改密码）")
    private String needChangePassword;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Column(isLogicDelete = true)
    @Schema(description = "逻辑删除")
    private String delFlag;

    @Schema(description = "删除时间，(正常状态为null，删除时有时间)")
    private LocalDateTime delTime;

    @Schema(description = "操作人id")
    private Long delId;

}