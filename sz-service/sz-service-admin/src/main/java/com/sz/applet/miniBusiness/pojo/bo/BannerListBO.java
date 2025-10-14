package com.sz.applet.miniBusiness.pojo.bo;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Banner查询BO
 * </p>
 *
 * @author sz
 * @since 2024-09-27
 */
@Data
@Schema(description = "Banner查询BO")
public class BannerListBO extends PageQuery {

    @Schema(description = "类型")
    private String type;

    @Schema(description = "名称")
    private String names;

    @Schema(description = "状态（1-启用）")
    private String status;

}