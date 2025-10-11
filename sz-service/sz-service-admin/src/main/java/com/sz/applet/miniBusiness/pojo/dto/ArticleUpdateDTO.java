package com.sz.applet.miniBusiness.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Article更新DTO
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
@Data
@Schema(description = "Article更新DTO")
public class ArticleUpdateDTO {

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "文章类型")
    private String type;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章头图")
    private String avatar;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "发布时间")
    private String time;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "状态（1-发布）")
    private String status;

    @Schema(description = "内容类型（link-链接, html-富文本）")
    private String contentType;

    @Schema(description = "内容（链接地址或富文本内容）")
    private String content;

    @Schema(description = "是否置顶（0-否，1-是）")
    private Integer isTop;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "发布人ID")
    private Long publishId;

}