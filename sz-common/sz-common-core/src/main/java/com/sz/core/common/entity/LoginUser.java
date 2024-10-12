package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

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
        permissionAndMenuIds = new HashMap<>();
        ruleMap = new HashMap<>();
        depts = new ArrayList<>();
        userRuleMap = new HashMap<>();
        deptRuleMap = new HashMap<>();
    }

    @Schema(description = "基础用户信息")
    private BaseUserInfo userInfo;

    @Schema(description = "权限标识列表")
    private Set<String> permissions;

    @Schema(description = "角色列表")
    private Set<String> roles;

    @Schema(description = "所属部门")
    private List<Long> depts;

    @Schema(description = "所属部门及其子孙节点部门")
    private List<Long> deptAndChildren;

    @Schema(description = "权限标识与菜单关系数组")
    private Map<String, String> permissionAndMenuIds;

    @Schema(description = "菜单的查询规则")
    private Map<String, String> ruleMap;

    @Schema(description = "自定义userRule")
    private Map<String, Set<Long>> userRuleMap;

    @Schema(description = "自定义deptRule")
    private Map<String, Set<Long>> deptRuleMap;

}
