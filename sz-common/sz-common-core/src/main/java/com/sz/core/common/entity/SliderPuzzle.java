package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class SliderPuzzle {

    @Schema(description = "获取时间")
    private Long longTime;

    @Schema(description = "大图宽度")
    private Integer bigWidth;

    @Schema(description = "大图高度")
    private Integer bigHeight;

    @Schema(description = "大图转BASE64字符串")
    private String bigImageBase64;

    @Schema(description = "大图")
    private BufferedImage bigImage;

    @Schema(description = "小图宽度")
    private Integer smallWidth;

    @Schema(description = "小图高度")
    private Integer smallHeight;

    @Schema(description = "小图转BASE64字符串")
    private String smallImageBase64;

    @Schema(description = "小图")
    private BufferedImage smallImage;

    @Schema(description = "随机坐标Y")
    private Integer posY;

    @Schema(description = "随机坐标X")
    private Integer posX;
}