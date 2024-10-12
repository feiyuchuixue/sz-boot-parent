package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysUserRole;

import java.util.List;

/**
 * <p>
 * 系统用户-角色关联表 服务类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    List<String> getUserRolesByUserId(Long userId);
}
