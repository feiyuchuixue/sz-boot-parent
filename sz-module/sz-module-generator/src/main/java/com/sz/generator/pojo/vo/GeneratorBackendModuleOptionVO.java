package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 后端代码生成模块候选。
 */
@Data
@Schema(description = "后端代码生成模块候选")
public class GeneratorBackendModuleOptionVO {

    @Schema(description = "Maven 模块名，如 sz-module-admin")
    private String moduleName;

    @Schema(description = "模块编码，如 admin")
    private String moduleCode;

    @Schema(description = "模块路径")
    private String path;

    @Schema(description = "推荐 Java 根包")
    private String packageName;

    @Schema(description = "API 前缀模块编码")
    private String apiPrefixModule;

    @Schema(description = "API 前缀")
    private String apiPrefix;

    @Schema(description = "状态：ready 已接入；pending 待补齐；unavailable 不可用")
    private String status;

    @Schema(description = "是否推荐默认选中")
    private Boolean recommended = false;

    @Schema(description = "缺失接入项")
    private List<String> missingItems = new ArrayList<>();
}
