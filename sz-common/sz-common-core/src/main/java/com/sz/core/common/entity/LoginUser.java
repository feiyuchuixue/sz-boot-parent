package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

@Data
public class LoginUser {

    @Schema(description = "基础用户信息")
    private BaseUserInfo userInfo;

    @Schema(description = "权限标识列表")
    private Set<String> permissions = new HashSet<>();

    @Schema(description = "角色列表")
    private Set<String> roles = new HashSet<>();

    @Schema(description = "所属部门")
    private List<Long> depts = new ArrayList<>();

    @Schema(description = "所属部门及其子孙节点部门")
    private List<Long> deptAndChildren = new ArrayList<>();

    @Schema(description = "权限标识与菜单关系数组")
    private Map<String, String> permissionAndMenuIds = new HashMap<>();

    @Schema(description = "数据权限范围")
    private Map<String, RoleMenuScopeVO> dataScope;

}
