package com.sz.applet.miniBusiness.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 学校用户绑定小程序用户新增BO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校用户绑定小程序用户新增BO")
public class SchoolUserBindingCreateBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "学校用户ID")
    private Long schoolUserId;

    @Schema(description = "小程序用户ID")
    private Long miniUserId;
    
    @Schema(description = "绑定类型：1-主绑定（认证），2-辅助绑定（共享）")
    private Integer bindType;
}