package com.sz.applet.miniBusiness.pojo.po;

import com.mybatisflex.annotation.*;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 小程序文章表
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
@Data
@Table(value = "applet_article", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "小程序文章表")
public class Article implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
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

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "删除标识")
    @Column(isLogicDelete = true)
    private String delFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

    @Schema(description = "发布人ID")
    private Long publishId;

}