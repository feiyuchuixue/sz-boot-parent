package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * CheckPuzzle
 *
 * @version 1.0
 * @since 2025-01-08
 * @author sz
 */
@Data
public class CheckPuzzle {

    @Schema(description = "请求标识")
    private String requestId;

    @Schema(description = "移动变量（x轴位置）加密串")
    private String moveEncrypted;

    @Schema(description = "iv向量")
    private String iv;

}
