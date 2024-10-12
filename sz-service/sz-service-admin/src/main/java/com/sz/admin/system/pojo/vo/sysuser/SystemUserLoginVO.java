package com.sz.admin.system.pojo.vo.sysuser;

import com.sz.core.common.entity.BaseUserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: sz
 * @date: 2022/5/24 19:47
 * @description: 临时login返回对象结构，后续oauth集成后失效
 */
@Builder
@Data
public class SystemUserLoginVO {

    @Schema(description = "角色")
    private List<Long> roles;

    @Schema(description = "基础用户信息")
    private BaseUserInfo userInfo;

    @Schema(description = "权限标识数组")
    private List<String> permissions;

    @Schema(description = "accessToken")
    private String accessToken;

    @Schema(description = "refreshToken")
    private String refreshToken;

}
