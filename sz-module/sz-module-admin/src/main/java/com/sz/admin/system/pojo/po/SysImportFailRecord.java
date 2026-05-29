package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.sz.db.handler.Jackson3TypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Table("sys_import_fail_record")
@Schema(description = "导入失败记录表")
public class SysImportFailRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "导入批次ID")
    private String batchId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "行号")
    private Integer rowNo;

    @Schema(description = "业务主识别值")
    private String bizKey;

    @Schema(description = "业务主识别值标签")
    private String bizKeyLabel;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "处理状态")
    private String handleStatus;

    @Schema(description = "失败行原始快照")
    @Column(typeHandler = Jackson3TypeHandler.class)
    private Map<String, Object> rowData;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
