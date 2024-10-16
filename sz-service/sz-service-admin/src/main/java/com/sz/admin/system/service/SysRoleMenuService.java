package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import com.sz.admin.system.pojo.vo.sysrolemenu.SysRoleMenuVO;

/**
 * <p>
 * 系统角色-菜单表 服务类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    void change(SysRoleMenuDTO dto);

    SysRoleMenuVO queryRoleMenu(Long roleId);
}
