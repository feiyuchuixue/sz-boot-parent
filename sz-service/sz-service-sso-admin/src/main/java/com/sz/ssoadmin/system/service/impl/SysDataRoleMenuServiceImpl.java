package com.sz.ssoadmin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.ssoadmin.system.mapper.SysDataRoleMenuMapper;
import com.sz.ssoadmin.system.pojo.po.SysDataRoleMenu;
import com.sz.ssoadmin.system.service.SysDataRoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sz.ssoadmin.system.pojo.po.table.SysDataRoleMenuTableDef.SYS_DATA_ROLE_MENU;

/**
 * <p>
 * 系统数据角色-菜单表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-07-11
 */
@Service
@RequiredArgsConstructor
public class SysDataRoleMenuServiceImpl extends ServiceImpl<SysDataRoleMenuMapper, SysDataRoleMenu> implements SysDataRoleMenuService {

    @Override
    public void batchSave(Long roleId, List<String> menuIds) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_ROLE_MENU.ROLE_ID.eq(roleId));
        if (count(wrapper) > 0) {
            remove(wrapper);
        }

        List<SysDataRoleMenu> roleMenus = new ArrayList<>();
        SysDataRoleMenu roleMenu;
        for (String menuId : menuIds) {
            roleMenu = new SysDataRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        }
        if (!roleMenus.isEmpty())
            saveBatch(roleMenus);
    }

    @Override
    public List<String> getSelectMenuIdByRoleId(Long roleId) {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DATA_ROLE_MENU.MENU_ID).where(SYS_DATA_ROLE_MENU.ROLE_ID.eq(roleId));
        return listAs(wrapper, String.class);
    }

}