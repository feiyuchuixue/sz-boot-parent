package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

}
