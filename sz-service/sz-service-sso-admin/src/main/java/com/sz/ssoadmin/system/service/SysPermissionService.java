package com.sz.ssoadmin.system.service;

import com.sz.ssoadmin.system.pojo.po.SysUser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SysPermissionService
 * 
 * @author sz
 * @since 2024/2/4 15:12
 * @version 1.0
 */
public interface SysPermissionService {

    Set<String> getMenuPermissions(SysUser sysUser);

    Set<String> getRoles(Long userId);

    Set<String> getRoles(SysUser sysUser);

    List<Long> getDepts(SysUser sysUser);

    List<Long> getDeptAndChildren(SysUser sysUser);

    Map<String, String> buildMenuRuleMap(SysUser sysUser, Set<String> findMenuIds);

}
