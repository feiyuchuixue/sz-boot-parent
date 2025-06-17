package com.sz.ssoadmin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.ssoadmin.system.pojo.dto.sysmenu.SysUserRoleDTO;
import com.sz.ssoadmin.system.pojo.po.SysUserDataRole;
import com.sz.ssoadmin.system.pojo.vo.sysuser.SysUserRoleVO;

/**
 * <p>
 * 系统用户-数据角色关联表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-11
 */
public interface SysUserDataRoleService extends IService<SysUserDataRole> {

    void changeRole(SysUserRoleDTO dto);

    SysUserRoleVO queryRoleMenu(Long userId);
}
