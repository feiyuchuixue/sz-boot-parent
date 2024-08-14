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
 * 部门表
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
@Data
@Table(value = "sys_dept", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "部门表")
public class SysDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父级id")
    private Long pid;

    @Schema(description = "层级")
    private Integer deep;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否有子级")
    private String hasChildren;

    @Schema(description = "是否锁定")
    private String isLock;

    @Column(isLogicDelete = true)
    @Schema(description = "删除标识")
    private String delFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}