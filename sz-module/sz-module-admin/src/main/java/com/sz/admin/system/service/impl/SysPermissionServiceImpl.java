package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.sz.admin.system.pojo.po.SysDept;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.po.SysUserDept;
import com.sz.admin.system.service.SysDeptClosureService;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysPermissionService;
import com.sz.admin.system.service.SysUserRoleService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.enums.CommonResponseEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.sz.admin.system.pojo.po.table.SysDeptTableDef.SYS_DEPT;
import static com.sz.admin.system.pojo.po.table.SysUserDeptTableDef.SYS_USER_DEPT;

/**
 * SysPermissionServiceImpl
 * 
 * @author sz
 * @since 2024/2/4 15:12
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysMenuService sysMenuService;

    private final SysUserRoleService sysUserRoleService;

    private final SysDeptClosureService sysDeptClosureService;

    @Override
    public Set<String> getMenuPermissions(SysUser sysUser) {
        Set<String> permissions = new HashSet<>();
        CommonResponseEnum.INVALID_USER.assertNull(sysUser);
        if (isSuperAdmin(sysUser)) { // 获取超管权限
            permissions.addAll(sysMenuService.findAllPermissions());
        } else { // 获取指定用户的权限
            permissions.addAll(sysMenuService.findPermissionsByUserId(sysUser.getId()));
        }
        return permissions;
    }

    @Override
    public Set<String> getRoles(Long userId) {
        Set<String> roles = new HashSet<>();
        SysUser sysUser = QueryChain.of(SysUser.class).eq(SysUser::getId, userId).one();
        CommonResponseEnum.INVALID_USER.assertNull(sysUser);
        if (isSuperAdmin(sysUser)) { // 获取超管角色
            roles.add(GlobalConstant.SUPER_ROLE); // 超管角色设置为"admin"
        } else {
            roles.addAll(sysUserRoleService.getUserRolesByUserId(userId));
        }
        return roles;
    }

    @Override
    public Set<String> getRoles(SysUser sysUser) {
        Set<String> roles = new HashSet<>();
        if (isSuperAdmin(sysUser)) { // 获取超管角色
            roles.add(GlobalConstant.SUPER_ROLE); // 超管角色设置为"admin"
        } else {
            roles.addAll(sysUserRoleService.getUserRolesByUserId(sysUser.getId()));
        }
        return roles;
    }

    @Override
    public List<Long> getDepts(SysUser sysUser) {
        CommonResponseEnum.INVALID_USER.assertNull(sysUser);
        if (isSuperAdmin(sysUser)) {
            // 查询全部的部门ID
            return QueryChain.of(SysDept.class).select(SYS_DEPT.ID).listAs(Long.class);
        } else {
            return QueryChain.of(SysUserDept.class).select(SYS_USER_DEPT.DEPT_ID).where(SYS_USER_DEPT.USER_ID.eq(sysUser.getId())).listAs(Long.class);
        }
    }

    @Override
    public List<Long> getDeptAndChildren(SysUser sysUser) {
        return getDeptAndChildren(sysUser, getDepts(sysUser));
    }

    @Override
    public List<Long> getDeptAndChildren(SysUser sysUser, List<Long> depts) {
        if (isSuperAdmin(sysUser)) {
            // 超管 depts 已是全量查询结果（来自 getDepts），直接复用，避免重复查询 sys_dept
            return depts;
        } else {
            return sysDeptClosureService.descendants(depts);
        }
    }

    /**
     * 验证用户是否是【管理员身份】 验证方式：sys_user.user_tag_cd 字段； [1001001 测试用户; 1001002 超级管理员;
     * 1001003 普通用户] 。 详见字典：用户标签（user_tag）
     *
     * @param sysUser
     *            用户信息
     * @return 是否是超级管理员
     */
    private boolean isSuperAdmin(SysUser sysUser) {
        return sysUser != null && ("1001002").equals(sysUser.getUserTagCd());
    }

}
