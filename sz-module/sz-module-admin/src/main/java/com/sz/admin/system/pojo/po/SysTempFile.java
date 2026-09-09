package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.*;
import com.sz.db.handler.Jackson3TypeHandler;
import com.sz.resource.model.ResourceRef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.db.EntityChangeListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 模版文件表
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Table(value = "sys_temp_file", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "模版文件表")
public class SysTempFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "文件ID")
    private Long sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    @Column(typeHandler = Jackson3TypeHandler.class)
    private List<ResourceRef> url;

    @Schema(description = "备注")
    private String remark;

    @Column(isLogicDelete = true)
    @Schema(description = "逻辑删除")
    private String delFlag;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "标识")
    private String alias;

}