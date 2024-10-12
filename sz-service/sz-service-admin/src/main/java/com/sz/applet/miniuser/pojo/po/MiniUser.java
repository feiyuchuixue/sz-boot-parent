package com.sz.applet.miniuser.pojo.po;

import com.mybatisflex.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import com.sz.mysql.EntityChangeListener;
import java.time.LocalDateTime;
/**
 * <p>
 * 小程序用户表
 * </p>
 *
 * @author sz
 * @since 2024-04-26
 */
@Data
@Table(value = "mini_user", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "小程序用户表")
public class MiniUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Integer id;

    @Schema(description = "关联的系统用户ID")
    private Integer sysUserId;

    @Schema(description = "小程序用户的唯一标识")
    private String openid;

    @Schema(description = "公众号的唯一标识")
    private String unionid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户头像URL")
    private String avatarUrl;

    @Schema(description = "是否订阅公众号（1是0否）")
    private Integer subscribe;

    @Schema(description = "性别，0-未知 1-男性，2-女性")
    private Integer sex;

    @Schema(description = "删除标识")
    private String delFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}