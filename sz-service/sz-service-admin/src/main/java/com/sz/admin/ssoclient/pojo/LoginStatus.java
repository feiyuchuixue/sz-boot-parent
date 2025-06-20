package com.sz.admin.ssoclient.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * LoginStatus - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/6/20
 */
@Data
@Schema(description = "登录状态信息")
public class LoginStatus {

    @Schema(description = "当前是否登录")
    private boolean hasLogin;

    @Schema(description = "登录ID")
    private Object loginId;

}
