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

}