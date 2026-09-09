package com.sz.admin.system.pojo.vo.scriptexport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Script export response.
 *
 * @author sz
 */
@Data
@Schema(description = "脚本导出响应")
public class ScriptExportVO {

    @Schema(description = "当前数据源方言")
    private String currentDialect;

    @Schema(description = "本次 SQL 输出方言")
    private String selectedDialect;

    @Schema(description = "脚本列表")
    private List<ScriptExportItemVO> items = new ArrayList<>();

}
