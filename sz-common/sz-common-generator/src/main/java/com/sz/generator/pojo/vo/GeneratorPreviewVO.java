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

    @Schema(description = "语言", example = "java")
    private String language;

    @Schema(description = "别名", example = "controller")
    private String alias;

}
