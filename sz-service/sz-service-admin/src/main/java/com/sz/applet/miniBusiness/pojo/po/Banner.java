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
 * 小程序Banner表
 * </p>
 *
 * @author sz
 * @since 2024-09-27
 */
@Data
@Table(value = "applet_banner", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "小程序Banner表")
public class Banner implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "链接")
    @Column(value = "link")
    private String link;

    @Schema(description = "类型")
    @Column(value = "type")
    private String type;

    @Schema(description = "名称")
    @Column(value = "names")
    private String names;

    @Schema(description = "图片地址")
    @Column(value = "picture")
    private String picture;

    @Schema(description = "状态（1-启用）")
    @Column(value = "status")
    private String status;

    @Schema(description = "排序")
    @Column(value = "sort")
    private Integer sort;

    @Schema(description = "内容类型（link-链接）")
    @Column(value = "content_type")
    private String contentType;

    @Schema(description = "内容（链接地址）")
    @Column(value = "content")
    private String content;

    @Schema(description = "删除标识")
    @Column(value = "del_flag",isLogicDelete = true)
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

}