package com.sz.admin.system.pojo.dto.sysclient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SysClient添加DTO
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
@Data
@Schema(description = "SysClient修改DTO")
public class SysClientUpdateDTO {

    @Schema(description = "客户端id")
    private String clientId;

    @Schema(description = "授权类型数组")
    private List<String> grantTypeCdList;

    @Schema(description = "授权类型")
    private String grantTypeCd;

    @Schema(description = "设备类型")
    private String deviceTypeCd;

    @Schema(description = "token活跃超时时间")
    private Integer activeTimeout;

    @Schema(description = "token固定超时")
    private Integer timeout;

    @Schema(description = "状态")
    private String clientStatusCd;

    @Schema(description = "备注")
    private String remark;

}