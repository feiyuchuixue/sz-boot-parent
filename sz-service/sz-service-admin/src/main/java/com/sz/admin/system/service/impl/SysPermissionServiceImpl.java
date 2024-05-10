package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysPermissionService;
import com.sz.admin.system.service.SysUserRoleService;
import com.sz.core.common.constant.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName SysPermissionServiceImpl
 * @Author sz
 * @Date 2024/2/4 15:12
 * @Version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysUserMapper sysUserMapper;

    private final SysMenuService sysMenuService;

    private final SysUserRoleService sysUserRoleService;

    @Override
    public Set<String> getMenuPermissions(Long userId) {
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .eq(SysUser::getId, userId).one();
        Set<String> permissions = new HashSet<>();
        if (isSuperAdmin(sysUser)) { // 获取超管权限
            permissions.addAll(sysMenuService.findAllPermissions());
        } else { // 获取指定用户的权限
            permissions.addAll(sysMenuService.findPermissionsByUserId(userId));
        }
        return permissions;
    }

    @Override
    public Set<String> getRoles(Long userId) {
        Set<String> roles = new HashSet<>();
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .eq(SysUser::getId, userId).one();
        if (isSuperAdmin(sysUser)) { // 获取超管角色
            roles.add(GlobalConstant.SUPER_ROLE); // 超管角色设置为"admin"
        } else {
            roles.addAll(sysUserRoleService.getUserRolesByUserId(userId));
        }
        return roles;
    }

    private boolean isSuperAdmin(SysUser sysUser) {
        if (sysUser != null && ("1001002").equals(sysUser.getUserTagCd())) {
            return true;
        }
        return false;
    }

}
