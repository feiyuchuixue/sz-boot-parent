package com.sz.generator.pojo.vo;

import freemarker.template.Template;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName CodeGenTempResult
 * @Author sz
 * @Date 2024/1/16 10:49
 * @Version 1.0
 */
@Data
public class CodeGenTempResult {

    public CodeGenTempResult(Template template, String relativePath, String extension, String alias, String language, String outputMessage) {
        this.template = template;
        this.relativePath = relativePath;
        this.extension = extension;
        this.alias = alias;
        this.language = language;
        this.outputMessage = outputMessage;
    }

    private Template template;

    @Schema(description = " zip path （zip存储路径）")
    private String relativePath;

    @Schema(description = "扩展名")
    private String extension;

    @Schema(description = "别名")
    private String alias;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "输出信息")
    private String outputMessage;

}
