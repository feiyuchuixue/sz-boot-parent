package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 数据权限管理
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-09
 */
@Data
@Table(value = "sys_data_role", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "数据权限管理")
public class SysDataRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "角色id")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据范围")
    private String dataScopeCd;

    @Schema(description = "简介")
    private String remark;

    @Column(isLogicDelete = true)
    @Schema(description = "删除与否")
    private String delFlag;

    @Schema(description = "是否锁定")
    private String isLock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新人")
    private Long updateId;

}