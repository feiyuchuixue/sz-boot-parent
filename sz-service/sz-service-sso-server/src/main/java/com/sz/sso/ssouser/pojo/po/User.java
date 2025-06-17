package com.sz.sso.ssouser.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.time.LocalDateTime;

/**
* <p>
* 平台用户表
* </p>
*
* @author sz-admin
* @since 2025-06-16
*/
@Data
@Table(value = "sso_user", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "平台用户表")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description ="平台用户id，sso client 的 center_id映射")
    private Long id;

    @Schema(description ="用户名，唯一")
    private String username;

    @Schema(description ="昵称")
    private String nickname;

    @Schema(description ="手机号")
    private String phone;

    @Schema(description ="邮箱")
    private String email;

    @Schema(description ="密码")
    private String password;

    @Schema(description ="状态（0：正常，1：禁用，2：待验证）")
    private String status;

    @Schema(description ="头像地址")
    private String logo;

    @Schema(description ="上一次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description ="上一次登录ip")
    private String lastLoginIp;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description ="删除时间，(正常状态为null，删除时有时间)")
    private LocalDateTime delTime;

}