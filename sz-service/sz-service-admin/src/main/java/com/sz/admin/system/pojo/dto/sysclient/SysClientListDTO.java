package com.sz.admin.system.pojo.dto.sysclient;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysClient添加DTO
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
@Data
@Schema(description = "SysClient查询DTO")
public class SysClientListDTO extends PageQuery {

    @Schema(description = "客户端key")
    private String clientKey;

    @Schema(description = "客户端秘钥")
    private String clientSecret;

    @Schema(description = "授权类型")
    private String grantTypeCd;

    @Schema(description = "设备类型")
    private String deviceTypeCd;

    @Schema(description = "token活跃超时时间")
    private Integer activeTimeout;

}