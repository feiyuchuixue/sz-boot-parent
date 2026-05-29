package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.sz.excel.imports.spi.ImportBatchContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table("sys_import_batch")
@Schema(description = "导入批次表")
public class SysImportBatch implements Serializable, ImportBatchContext {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "导入批次ID")
    private String batchId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务名称")
    private String bizName;

    @Schema(description = "导入文件名")
    private String fileName;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "总条数")
    private Integer totalCount;

    @Schema(description = "成功条数")
    private Integer successCount;

    @Schema(description = "失败条数")
    private Integer failCount;

    @Schema(description = "批次状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;
}
