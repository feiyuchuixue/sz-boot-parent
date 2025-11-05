package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysRoleMenuMapper;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.sz.admin.system.pojo.po.SysDataRoleRelation;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.core.common.entity.RoleMenuScopeVO;
import com.sz.admin.system.pojo.vo.sysrolemenu.SysRoleMenuVO;
import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import com.sz.admin.system.service.*;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.Utils;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static com.sz.admin.system.pojo.po.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.sz.admin.system.pojo.po.table.SysUserTableDef.SYS_USER;

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

    private final SysDeptService sysDeptService;

    private final SysDataRoleRelationService sysDataRoleRelationService;

    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void change(SysRoleMenuDTO dto) {
        Long roleId = dto.getRoleId();
        // 根据角色id 查询影响用户范围，获取到用户id
        QueryWrapper userWrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_USER_ROLE.USER_ID)).from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.ROLE_ID.eq(roleId));
        // 获取到影响范围的userId
        List<String> userIds = listAs(userWrapper, String.class);

        // 1. 功能权限的修改
        // 删除当前角色下的所有菜单记录
        QueryWrapper wrapper = QueryWrapper.create().eq(SysRoleMenu::getRoleId, roleId);
        remove(wrapper); // 移除角色-菜单关联记录
        sysDataRoleRelationService.deleteByRoleId(roleId); // 移除角色-数据权限关联记录
        if (Utils.isNotNull(dto.getMenu().getMenuIds())) {
            this.mapper.insertBatchSysRoleMenu(dto.getMenu().getMenuIds(), roleId, "menu", null); // 批量插入角色-菜单记录
        }

        // 2. 数据权限的修改
        List<SysRoleMenuDTO.Scope> scopeList = dto.getScope();
        List<SysRoleMenu> scopeRoleMenus = new ArrayList<>();
        SysRoleMenu roleMenu;
        for (SysRoleMenuDTO.Scope scope : scopeList) {
            String dataScope = scope.getDataScope();
            String menuId = scope.getMenuId();
            roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setPermissionType("scope");
            roleMenu.setDataScopeCd(dataScope);
            scopeRoleMenus.add(roleMenu);

            if ("1006005".equals(dataScope)) { // 自定义数据权限
                sysDataRoleRelationService.batchSave(roleId, menuId, "1007001", scope.getUserIds());
                sysDataRoleRelationService.batchSave(roleId, menuId, "1007002", scope.getDeptIds());
            }
        }
        saveBatch(scopeRoleMenus);

        // 3. 发布权限变更事件
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(userIds)));
    }

    /**
     * 查询角色分配的菜单，及全部菜单
     *
     * @param roleId
     *            角色id
     * @return 菜单
     */
    @Override
    public SysRoleMenuVO queryRoleMenu(Long roleId) {
        // 1. 查询基础信息：菜单树、部门树、用户列表
        List<MenuTreeVO> menuTreeVOS = sysMenuService.queryRoleMenuTree(true);
        List<String> menuIds = getMenuId(roleId, "menu");
        SysRoleMenuVO menuVO = new SysRoleMenuVO();
        menuVO.setMenuLists(menuTreeVOS);
        menuVO.setSelectMenuIds(menuIds);
        List<DeptTreeVO> deptTreeVOS = sysDeptService.getDeptTree(null, false, false);
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_USER.ID, SYS_USER.USERNAME, SYS_USER.NICKNAME);
        List<UserOptionVO> userOptions = sysUserMapper.selectListByQueryAs(wrapper, UserOptionVO.class);
        menuVO.setDeptLists(deptTreeVOS);
        menuVO.setUserLists(userOptions);

        List<SysRoleMenu> scopeRoleMenus = getMenuList(roleId, "scope");
        List<SysRoleMenuVO.Scope> scopes = new ArrayList<>();
        List<String> customMenuIds = new ArrayList<>();
        Map<String, SysRoleMenu> customMenuMap = new HashMap<>();
        for (SysRoleMenu menu : scopeRoleMenus) {
            if ("1006005".equals(menu.getDataScopeCd())) { // 自定义数据权限
                customMenuIds.add(menu.getMenuId());
                customMenuMap.put(menu.getMenuId(), menu);
            } else { // 非自定义数据权限
                SysRoleMenuVO.Scope scope = new SysRoleMenuVO.Scope();
                scope.setMenuId(menu.getMenuId());
                scope.setDataScope(menu.getDataScopeCd());
                scopes.add(scope);
            }
        }

        // 查询并聚合自定义数据权限
        if (!customMenuIds.isEmpty()) {
            List<SysDataRoleRelation> relations = sysDataRoleRelationService.queryRelationByRoleIdAndMenuIds(roleId, customMenuIds);
            Map<String, List<SysDataRoleRelation>> relationMap = relations.stream().collect(Collectors.groupingBy(SysDataRoleRelation::getMenuId)); // 按菜单ID聚合
            for (String menuId : customMenuIds) {
                List<SysDataRoleRelation> group = relationMap.getOrDefault(menuId, Collections.emptyList());
                SysRoleMenuVO.Scope scope = new SysRoleMenuVO.Scope();
                scope.setMenuId(menuId);
                scope.setDataScope("1006005");
                List<Long> deptIds = new ArrayList<>();
                List<Long> userIds = new ArrayList<>();
                for (SysDataRoleRelation relation : group) {
                    if ("1007001".equals(relation.getRelationTypeCd())) { // 用户维度
                        userIds.add(relation.getRelationId());
                    } else if ("1007002".equals(relation.getRelationTypeCd())) { // 部门维度
                        deptIds.add(relation.getRelationId());
                    }
                }
                scope.setDeptIds(deptIds);
                scope.setUserIds(userIds);
                scopes.add(scope);
            }
        }
        menuVO.setScope(scopes);
        return menuVO;
    }

    private List<SysRoleMenu> getMenuList(Long roleId, String permissionType) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysRoleMenu::getRoleId, roleId).eq(SysRoleMenu::getPermissionType, permissionType);
        return list(wrapper);
    }

    private List<String> getMenuId(Long roleId, String permissionType) {
        List<SysRoleMenu> list = getMenuList(roleId, permissionType);
        List<String> menuIds = new ArrayList<>();
        if (Utils.isNotNull(list)) {
            menuIds = list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        }
        return menuIds;
    }

    @Override
    public Map<String, RoleMenuScopeVO> getUserScope(Collection<String> roleIds) {
        Map<String, RoleMenuScopeVO> scopeVOMap = new HashMap<>();
        if (roleIds.isEmpty()) {
            return scopeVOMap;
        }
        List<String> customMenuIds = new ArrayList<>();
        Map<String, List<SysRoleMenu>> roleScopeMap = new HashMap<>();
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_ROLE_MENU.ROLE_ID.in(roleIds)).where(SYS_ROLE_MENU.PERMISSION_TYPE.eq("scope"));
        List<SysRoleMenu> list = list(wrapper);
        for (SysRoleMenu roleMenu : list) {
            String menuId = roleMenu.getMenuId();
            if (roleMenu.getDataScopeCd().equals("1006005")) {
                customMenuIds.add(menuId);
            }
            List<SysRoleMenu> roleMenus;
            if (roleScopeMap.containsKey(menuId)) {
                roleMenus = roleScopeMap.get(menuId);
                roleMenus.add(roleMenu);
            } else {
                roleMenus = new ArrayList<>();
                roleMenus.add(roleMenu);
                roleScopeMap.put(menuId, roleMenus);
            }
        }
        Map<String, RoleMenuScopeVO.CustomScope> customScopeMap = new HashMap<>();
        // 根据 角色 和 菜单 获取 自定义权限
        if (!customMenuIds.isEmpty()) {
            List<SysDataRoleRelation> relations = sysDataRoleRelationService.listByRoleIdsAndMenuIds(roleIds, customMenuIds);
            Map<String, List<SysDataRoleRelation>> relationMap = relations.stream().collect(Collectors.groupingBy(SysDataRoleRelation::getMenuId));
            for (String menuId : customMenuIds) {
                List<SysDataRoleRelation> group = relationMap.getOrDefault(menuId, Collections.emptyList());
                Set<Long> deptIds = new HashSet<>();
                Set<Long> userIds = new HashSet<>();
                for (SysDataRoleRelation relation : group) {
                    if ("1007001".equals(relation.getRelationTypeCd())) { // 用户维度
                        userIds.add(relation.getRelationId());
                    } else if ("1007002".equals(relation.getRelationTypeCd())) { // 部门维度
                        deptIds.add(relation.getRelationId());
                    }
                }
                RoleMenuScopeVO.CustomScope customScope = new RoleMenuScopeVO.CustomScope();
                customScope.setDeptIds(deptIds);
                customScope.setUserIds(userIds);
                customScopeMap.put(menuId, customScope);
            }
        }

        // 处理权限范围的合并逻辑
        for (Map.Entry<String, List<SysRoleMenu>> entry : roleScopeMap.entrySet()) {
            String menuId = entry.getKey();
            List<SysRoleMenu> menus = entry.getValue();
            RoleMenuScopeVO scopeVO = new RoleMenuScopeVO();
            scopeVO.setMenuId(menuId);

            // 1. 找出最高优先权 dataScopeCd=1006005 的所有菜单（可有多个角色）
            boolean hasCustom = false;
            for (SysRoleMenu menu : menus) {
                if ("1006005".equals(menu.getDataScopeCd())) {
                    hasCustom = true;
                    break;
                }
            }

            if (hasCustom) {
                scopeVO.setDataScopeCd("1006005");
                scopeVO.setCustomScope(customScopeMap.get(menuId));
            } else {
                // 没有自定义范围时，取dataScopeCd的最小值（字符串比较即可，通常1006001<..2<..3...）
                String minScope = menus.stream().map(SysRoleMenu::getDataScopeCd).min(String::compareTo).orElse("1006004"); // 默认取最小范围 仅本人
                scopeVO.setDataScopeCd(minScope);
                // 无自定义权限
                scopeVO.setCustomScope(null);
            }
            scopeVOMap.put(menuId, scopeVO);
        }
        return scopeVOMap;
    }

}
