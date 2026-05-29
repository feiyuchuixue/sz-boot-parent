package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.db.id.SzIdGenerator;
import com.sz.platform.listener.TableSysDictSourceListener;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典来源表
 * </p>
 *
 * @author sz-admin
 * @since 2026-05-21
 */
@Data
@Table(value = "sys_dict_source", onInsert = TableSysDictSourceListener.class, onUpdate = TableSysDictSourceListener.class)
@Schema(description = "字典来源表")
public class SysDictSource implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = SzIdGenerator.NAME)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "来源编码")
    private String sourceCode;

    @Schema(description = "来源名称")
    private String sourceName;

    @Schema(description = "起始ID")
    private Long startId;

    @Schema(description = "结束ID")
    private Long endId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

}