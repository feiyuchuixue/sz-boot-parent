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
 * 消息接收用户表
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Data
@Table(value = "sys_message_user", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "消息接收用户表")
public class SysMessageUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "接收人ID")
    private Long receiverId;

    @Schema(description = "是否已读")
    private String isRead;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    @Column(isLogicDelete = true)
    @Schema(description = "删除标识")
    private String delFlag;
}
