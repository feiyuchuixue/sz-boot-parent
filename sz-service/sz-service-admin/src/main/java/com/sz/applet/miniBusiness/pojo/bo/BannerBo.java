package com.sz.applet.miniBusiness.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Banner BO
 * </p>
 *
 * @author sz
 * @since 2024-09-27
 */
@Data
@Schema(description = "Banner BO")
public class BannerBo {


    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "链接")
    private String link;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "名称")
    private String names;

    @Schema(description = "图片地址")
    private String picture;

    @Schema(description = "状态（1-启用）")
    private String status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "内容类型（link-链接）")
    private String contentType;

    @Schema(description = "内容（链接地址）")
    private String content;

}