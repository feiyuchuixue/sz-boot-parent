package com.sz.admin.system.pojo.vo.sysdict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典来源")
public class DictSourceVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "来源编码")
    private String sourceCode;

    @Schema(description = "来源名称")
    private String sourceName;

    @Schema(description = "起始ID")
    private long startId;

    @Schema(description = "结束ID")
    private long endId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "说明")
    private String remark;

}
