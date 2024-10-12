package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysDataRoleMenu;

import java.util.List;

/**
 * <p>
 * 系统数据角色-菜单表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-11
 */
public interface SysDataRoleMenuService extends IService<SysDataRoleMenu> {

    void batchSave(Long roleId, List<String> menuIds);

    List<String> getSelectMenuIdByRoleId(Long roleId);
}
