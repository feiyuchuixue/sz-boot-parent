package com.sz.admin.system.service;

import com.sz.admin.system.pojo.po.SysUser;

import java.util.List;
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

    /**
     * 获取用户部门及子孙节点（传入已查好的直属部门，避免重复查询）
     *
     * @param sysUser
     *            用户信息
     * @param depts
     *            已查好的用户直属部门ID列表
     * @return 部门及子孙节点ID列表
     */
    List<Long> getDeptAndChildren(SysUser sysUser, List<Long> depts);

}
