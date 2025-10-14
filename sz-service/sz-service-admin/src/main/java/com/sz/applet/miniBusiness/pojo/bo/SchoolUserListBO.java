package com.sz.applet.miniBusiness.pojo.bo;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 学校师生表查询BO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校师生表查询BO")
public class SchoolUserListBO extends PageQuery {

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "身份（1-校友，2-教师）")
    private Integer identity;

    @Schema(description = "申请状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;
}