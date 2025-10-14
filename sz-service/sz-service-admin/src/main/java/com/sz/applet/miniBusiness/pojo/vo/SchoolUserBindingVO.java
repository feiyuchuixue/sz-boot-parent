package com.sz.applet.miniBusiness.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 学校用户绑定小程序用户VO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校用户绑定小程序用户VO")
public class SchoolUserBindingVO {

    @Schema(description = "绑定ID")
    private Long id;

    @Schema(description = "学校用户ID")
    private Long schoolUserId;

    @Schema(description = "小程序用户ID")
    private Long miniUserId;

    @Schema(description = "绑定状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}