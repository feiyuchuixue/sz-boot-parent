package com.sz.applet.miniBusiness.pojo.bo;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Article查询BO
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
@Data
@Schema(description = "Article查询BO")
public class ArticleListBO extends PageQuery {

    @Schema(description = "文章类型")
    private String type;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "状态（1-发布）")
    private String status;

}