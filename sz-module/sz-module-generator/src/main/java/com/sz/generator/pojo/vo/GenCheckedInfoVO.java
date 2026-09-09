package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @since 2024/4/16 16:11
 */
@Data
public class GenCheckedInfoVO {

    @Schema(description = "API路径验证状态")
    private Boolean checkedApiPath = true;

    @Schema(description = "Web路径验证状态")
    private Boolean checkedWebPath = true;

    @Schema(description = "API路径")
    private String pathApi;

    @Schema(description = "Web路径")
    private String pathWeb;

    @Schema(description = "后端模块接入验证状态")
    private Boolean checkedBackendModule = true;

    @Schema(description = "数据权限字段验证状态")
    private Boolean checkedDataScope = true;

    @Schema(description = "错误信息")
    private List<String> errors = new ArrayList<>();

    @Schema(description = "提醒信息")
    private List<String> warnings = new ArrayList<>();

}
