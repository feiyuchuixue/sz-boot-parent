package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName LoginUser
 * @Author sz
 * @Date 2024/1/23 15:14
 * @Version 1.0
 */
@Data
public class LoginUser {

    {
        permissions = new HashSet<>();
        roles = new HashSet<>();
    }

    @Schema(description = "基础用户信息")
    private BaseUserInfo userInfo;

    @Schema(description = "权限标识列表")
    private Set<String> permissions;

    @Schema(description = "角色列表")
    private Set<String> roles;


}
