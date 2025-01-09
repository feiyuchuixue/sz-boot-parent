package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName CheckPuzzle
 * @Author sz
 * @Date 2025/1/8 15:46
 * @Version 1.0
 */
@Data
public class CheckPuzzle {

    @Schema(description = "请求标识")
    private String requestId;

    @Schema(description = "移动变量（x轴位置）加密串")
    private String moveEncrypted;

}
