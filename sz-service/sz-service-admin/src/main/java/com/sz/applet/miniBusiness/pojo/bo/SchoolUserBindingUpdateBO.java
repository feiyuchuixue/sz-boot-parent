package com.sz.applet.miniBusiness.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 学校用户绑定小程序用户修改BO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校用户绑定小程序用户修改BO")
public class SchoolUserBindingUpdateBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "绑定ID")
    private Long id;

    @Schema(description = "绑定状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;
}