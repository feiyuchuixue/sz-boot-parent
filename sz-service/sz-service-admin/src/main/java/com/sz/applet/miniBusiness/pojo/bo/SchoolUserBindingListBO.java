package com.sz.applet.miniBusiness.pojo.bo;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 学校用户绑定小程序用户查询BO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校用户绑定小程序用户查询BO")
public class SchoolUserBindingListBO extends PageQuery {

    @Schema(description = "学校用户ID")
    private Long schoolUserId;

    @Schema(description = "小程序用户ID")
    private Long miniUserId;

    @Schema(description = "绑定状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;
}