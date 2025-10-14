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
    @Column(value = "type")
    private String type;

    @Schema(description = "文章标题")
    @Column(value = "title")
    private String title;

    @Schema(description = "文章头图")
    @Column(value = "avatar")
    private String avatar;

    @Schema(description = "文章摘要")
    @Column(value = "summary")
    private String summary;

    @Schema(description = "发布时间")
    @Column(value = "time")
    private String time;

    @Schema(description = "标签")
    @Column(value = "label")
    private String label;

    @Schema(description = "作者")
    @Column(value = "author")
    private String author;

    @Schema(description = "状态（1-发布）")
    @Column(value = "status")
    private String status;

    @Schema(description = "内容类型（link-链接, html-富文本）")
    @Column(value = "content_type")
    private String contentType;

    @Schema(description = "内容（链接地址或富文本内容）")
    @Column(value = "content")
    private String content;

    @Schema(description = "是否置顶（0-否，1-是）")
    @Column(value = "is_top")
    private Integer isTop;

    @Schema(description = "浏览量")
    @Column(value = "view_count")
    private Integer viewCount;

    @Schema(description = "点赞数")
    @Column(value = "like_count")
    private Integer likeCount;

    @Schema(description = "排序")
    @Column(value = "sort")
    private Integer sort;

    @Schema(description = "删除标识")
    @Column(value = "del_flag", isLogicDelete = true)
    private String delFlag;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    @Schema(description = "创建人ID")
    @Column(value = "create_id")
    private Long createId;

    @Schema(description = "更新人ID")
    @Column(value = "update_id")
    private Long updateId;

    @Schema(description = "发布人ID")
    @Column(value = "publish_id")
    private Long publishId;

}