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
@Schema(description = "字典来源选项")
public class SysDictSourceOptionVO {

    @Schema(description = "来源编码")
    private String sourceCode;

    @Schema(description = "来源名称")
    private String sourceName;

    @Schema(description = "起始ID")
    private long startId;

    @Schema(description = "结束ID")
    private long endId;

}
