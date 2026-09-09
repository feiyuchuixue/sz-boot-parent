package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sz
 * @since 2024/1/16 16:22
 */
@Data
@Schema(description = "preview")
public class GeneratorPreviewVO {

    @Schema(description = "文件名", example = "TestController.java")
    private String name;

    @Schema(description = "代码", example = "public class TeacherStatisticsController  {}")
    private String code;

    @Schema(description = "生成计划项内容")
    private String content;

    @Schema(description = "diff 内容")
    private String diff;

    @Schema(description = "目标项目内相对路径")
    private String relativePath;

    @Schema(description = "完整目标路径")
    private String fullPath;

    @Schema(description = "目标项目名")
    private String projectName;

    @Schema(description = "操作类型：CREATE_FILE/MODIFY_FILE/SKIP_EXISTS/SCRIPT")
    private String operationType;

    @Schema(description = "操作说明")
    private String message;

    @Schema(description = "语言", example = "java")
    private String language;

    @Schema(description = "别名", example = "controller")
    private String alias;

}
