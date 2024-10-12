package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName GenCheckedInfoVO
 * @Author sz
 * @Date 2024/4/16 16:11
 * @Version 1.0
 */
@Data
public class GenCheckedInfoVO {

    {
        checkedApiPath = true;
        checkedWebPath = true;
    }

    @Schema(description = "API路径验证状态")
    private Boolean checkedApiPath;

    @Schema(description = "Web路径验证状态")
    private Boolean checkedWebPath;

    @Schema(description = "API路径")
    private String pathApi;

    @Schema(description = "Web路径")
    private String pathWeb;

}
