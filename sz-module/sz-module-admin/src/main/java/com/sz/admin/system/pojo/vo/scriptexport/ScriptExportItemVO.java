package com.sz.admin.system.pojo.vo.scriptexport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * One rendered script item.
 *
 * @author sz
 */
@Data
@Schema(description = "脚本导出项")
public class ScriptExportItemVO {

    @Schema(description = "格式：xml/sql")
    private String format;

    @Schema(description = "SQL 方言，XML 为空")
    private String dialect;

    @Schema(description = "代码高亮语言")
    private String language;

    @Schema(description = "显示标题")
    private String title;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "脚本内容")
    private String content;

}
