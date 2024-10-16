package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysRoleMenuMapper;
import com.sz.admin.system.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysrolemenu.SysRoleMenuVO;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysRoleMenuService;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.Utils;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * <p>
 * 系统角色-菜单表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final SysMenuService sysMenuService;

    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public void change(SysRoleMenuDTO dto) {
        // 根据角色id 查询影响用户范围，获取到用户id
        QueryWrapper userWrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_USER_ROLE.USER_ID)).from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.ROLE_ID.eq(dto.getRoleId()));
        // 获取到影响范围的userId
        List<String> userIds = listAs(userWrapper, String.class);

        // 删除当前角色下的所有记录
        QueryWrapper wrapper = QueryWrapper.create().eq(SysRoleMenu::getRoleId, dto.getRoleId());
        remove(wrapper);
        if (Utils.isNotNull(dto.getMenuIds())) {
            this.mapper.insertBatchSysRoleMenu(dto.getMenuIds(), dto.getRoleId());
        }

        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(userIds)));
    }

    /**
     * 查询角色分配的菜单，及全部菜单
     *
     * @param roleId
     * @return
     */
    @Override
    public SysRoleMenuVO queryRoleMenu(Long roleId) {
        List<MenuTreeVO> menuTreeVOS = sysMenuService.queryRoleMenuTree(true);
        QueryWrapper wrapper = QueryWrapper.create().select(SysRoleMenu::getMenuId).eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> list = list(wrapper);
        List<String> selectIds = new ArrayList<>();
        if (Utils.isNotNull(list)) {
            selectIds = list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        }
        SysRoleMenuVO menuVO = new SysRoleMenuVO();
        menuVO.setMenuLists(menuTreeVOS);
        menuVO.setSelectIds(selectIds);
        return menuVO;
    }

}
