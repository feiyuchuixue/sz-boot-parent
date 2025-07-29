package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.time.LocalDateTime;

/**
 * <p>
 * 登陆日志表
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
@Data
@Accessors(chain = true)
@Table(value = "sys_login_log", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "登陆日志表")
public class SysLoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "登陆ID")
    private Integer id;

    @Column(isLogicDelete = true)
    @Schema(description = "删除标识")
    private String delFlag;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "登陆状态")
    private String loginStatus;

    @Schema(description = "登陆时间")
    private LocalDateTime loginTime;

    @Schema(description = "登陆ip地址")
    private String ipAddress;

    @Schema(description = "登陆地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "提示消息")
    private String msg;

    @Schema(description = "备注")
    private String remark;

}