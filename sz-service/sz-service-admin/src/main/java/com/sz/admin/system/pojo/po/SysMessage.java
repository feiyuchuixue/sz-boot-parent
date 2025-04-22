package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.*;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 消息管理
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Data
@Table(value = "sys_message", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "消息管理")
public class SysMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "消息类型")
    private String messageTypeCd;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Column(isLogicDelete = true)
    @Schema(description = "删除标识")
    private String delFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人 ID")
    private Long createId;

    @Schema(description = "更新人 ID")
    private Long updateId;

    @Schema(description = "菜单路由地址")
    private String menuRouter;
}
