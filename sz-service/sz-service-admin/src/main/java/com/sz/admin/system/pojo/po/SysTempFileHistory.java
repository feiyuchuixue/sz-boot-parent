package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.time.LocalDateTime;

/**
 * <p>
 * 模版文件历史
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Table(value = "sys_temp_file_history", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "模版文件历史")
public class SysTempFileHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "模版文件ID")
    private Integer sysTempFileId;

    @Schema(description = "文件ID")
    private Integer sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    private String url;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}