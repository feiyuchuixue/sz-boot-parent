package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysRoleMenuMapper;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.pojo.dto.scriptexport.ScriptExportDTO;
import com.sz.admin.system.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.sz.admin.system.pojo.po.SysDataRoleRelation;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import com.sz.admin.system.pojo.vo.scriptexport.ScriptExportVO;
import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.core.common.entity.RoleMenuScopeVO;
import com.sz.admin.system.pojo.vo.sysrolemenu.SysRoleMenuVO;
import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import com.sz.admin.system.script.AdminScriptExportService;
import com.sz.admin.system.service.*;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.Utils;

import com.sz.db.permission.DataScopeConstant;
import com.sz.platform.constant.dict.DataScopeRelationTypeConstant;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    private final AdminScriptExportService scriptExportService;

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
            List<SysRoleMenu> menuRoles = dto.getMenu().getMenuIds().stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                rm.setPermissionType("menu");
                rm.setDataScopeCd(""); // menu 类型时 data_scope_cd 为空字符串（联合主键不允许 NULL）
                return rm;
            }).toList();
            this.mapper.insertBatch(menuRoles); // 批量插入角色-菜单记录
        }

        // 2. 数据权限的修改
        List<SysRoleMenuDTO.Scope> scopeList = dto.getScope();
        List<SysRoleMenu> scopeRoleMenus = new ArrayList<>();
        SysRoleMenu roleMenu;
        for (SysRoleMenuDTO.Scope scope : scopeList) {
            String dataScope = scope.getDataScope();
            Long menuId = scope.getMenuId();
            roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setPermissionType("scope");
            roleMenu.setDataScopeCd(dataScope);
            scopeRoleMenus.add(roleMenu);

            if (DataScopeConstant.CUSTOM.equals(dataScope)) { // 自定义数据权限
                sysDataRoleRelationService.batchSave(roleId, menuId, DataScopeRelationTypeConstant.USER, scope.getUserIds());
                sysDataRoleRelationService.batchSave(roleId, menuId, DataScopeRelationTypeConstant.DEPT, scope.getDeptIds());
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
        List<Long> menuIds = getMenuId(roleId, "menu");
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
        List<Long> customMenuIds = new ArrayList<>();
        Map<Long, SysRoleMenu> customMenuMap = new HashMap<>();
        for (SysRoleMenu menu : scopeRoleMenus) {
            if (DataScopeConstant.CUSTOM.equals(menu.getDataScopeCd())) { // 自定义数据权限
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
            Map<Long, List<SysDataRoleRelation>> relationMap = relations.stream().collect(Collectors.groupingBy(SysDataRoleRelation::getMenuId)); // 按菜单ID聚合
            for (Long menuId : customMenuIds) {
                List<SysDataRoleRelation> group = relationMap.getOrDefault(menuId, Collections.emptyList());
                SysRoleMenuVO.Scope scope = new SysRoleMenuVO.Scope();
                scope.setMenuId(menuId);
                scope.setDataScope(DataScopeConstant.CUSTOM);
                List<Long> deptIds = new ArrayList<>();
                List<Long> userIds = new ArrayList<>();
                for (SysDataRoleRelation relation : group) {
                    if (DataScopeRelationTypeConstant.USER.equals(relation.getRelationTypeCd())) { // 用户维度
                        userIds.add(relation.getRelationId());
                    } else if (DataScopeRelationTypeConstant.DEPT.equals(relation.getRelationTypeCd())) { // 部门维度
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

    @Override
    public ScriptExportVO exportRoleMenuScript(ScriptExportDTO dto) {
        try {
            return scriptExportService.renderRoleMenuExport(buildRoleMenuScriptModel(dto), dto.getSqlDialect());
        } catch (IOException e) {
            log.error("exportRoleMenuScript error", e);
            return new ScriptExportVO();
        }
    }

    private Map<String, Object> buildRoleMenuScriptModel(ScriptExportDTO dto) {
        Map<String, Object> dataModel = new HashMap<>();
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        List<SysDataRoleRelation> dataRoleRelationList = new ArrayList<>();

        if (Utils.isNotNull(dto.getIds())) {
            QueryWrapper roleMenuWrapper = QueryWrapper.create().in(SysRoleMenu::getRoleId, dto.getIds()).orderBy(SysRoleMenu::getRoleId).asc()
                    .orderBy(SysRoleMenu::getPermissionType).asc().orderBy(SysRoleMenu::getMenuId).asc().orderBy(SysRoleMenu::getDataScopeCd).asc();
            roleMenuList = list(roleMenuWrapper);

            List<Long> customScopeMenuIds = roleMenuList.stream().filter(item -> "scope".equals(item.getPermissionType()))
                    .filter(item -> DataScopeConstant.CUSTOM.equals(item.getDataScopeCd())).map(SysRoleMenu::getMenuId).distinct().toList();
            if (!customScopeMenuIds.isEmpty()) {
                QueryWrapper relationWrapper = QueryWrapper.create().in(SysDataRoleRelation::getRoleId, dto.getIds())
                        .in(SysDataRoleRelation::getMenuId, customScopeMenuIds).orderBy(SysDataRoleRelation::getRoleId).asc()
                        .orderBy(SysDataRoleRelation::getMenuId).asc().orderBy(SysDataRoleRelation::getRelationTypeCd).asc()
                        .orderBy(SysDataRoleRelation::getRelationId).asc();
                dataRoleRelationList = sysDataRoleRelationService.list(relationWrapper);
            }
        }

        dataModel.put("roleMenuList", roleMenuList);
        dataModel.put("dataRoleRelationList", dataRoleRelationList);
        return dataModel;
    }

    private List<SysRoleMenu> getMenuList(Long roleId, String permissionType) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysRoleMenu::getRoleId, roleId).eq(SysRoleMenu::getPermissionType, permissionType);
        return list(wrapper);
    }

    private List<Long> getMenuId(Long roleId, String permissionType) {
        List<SysRoleMenu> list = getMenuList(roleId, permissionType);
        List<Long> menuIds = new ArrayList<>();
        if (Utils.isNotNull(list)) {
            menuIds = list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        }
        return menuIds;
    }

    @Override
    public Map<Long, RoleMenuScopeVO> getUserScope(Collection<String> roleIds) {
        Map<Long, RoleMenuScopeVO> scopeVOMap = new HashMap<>();
        if (roleIds.isEmpty()) {
            return scopeVOMap;
        }
        // 超管拥有全部权限，无需查询数据权限范围
        if (roleIds.contains(GlobalConstant.SUPER_ROLE)) {
            return scopeVOMap;
        }
        // roles 中只含数字字符串（role id），转 Long 避免 PG bigint = varchar 类型不匹配
        List<Long> numericRoleIds = roleIds.stream().map(Long::valueOf).toList();
        List<Long> customMenuIds = new ArrayList<>();
        Map<Long, List<SysRoleMenu>> roleScopeMap = new HashMap<>();
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_ROLE_MENU.ROLE_ID.in(numericRoleIds)).where(SYS_ROLE_MENU.PERMISSION_TYPE.eq("scope"));
        List<SysRoleMenu> list = list(wrapper);
        for (SysRoleMenu roleMenu : list) {
            Long menuId = roleMenu.getMenuId();
            if (roleMenu.getDataScopeCd().equals(DataScopeConstant.CUSTOM)) {
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
        Map<Long, RoleMenuScopeVO.CustomScope> customScopeMap = new HashMap<>();
        // 根据 角色 和 菜单 获取 自定义权限
        if (!customMenuIds.isEmpty()) {
            List<SysDataRoleRelation> relations = sysDataRoleRelationService.listByRoleIdsAndMenuIds(roleIds, customMenuIds);
            Map<Long, List<SysDataRoleRelation>> relationMap = relations.stream().collect(Collectors.groupingBy(SysDataRoleRelation::getMenuId));
            for (Long menuId : customMenuIds) {
                List<SysDataRoleRelation> group = relationMap.getOrDefault(menuId, Collections.emptyList());
                Set<Long> deptIds = new HashSet<>();
                Set<Long> userIds = new HashSet<>();
                for (SysDataRoleRelation relation : group) {
                    if (DataScopeRelationTypeConstant.USER.equals(relation.getRelationTypeCd())) { // 用户维度
                        userIds.add(relation.getRelationId());
                    } else if (DataScopeRelationTypeConstant.DEPT.equals(relation.getRelationTypeCd())) { // 部门维度
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
        for (Map.Entry<Long, List<SysRoleMenu>> entry : roleScopeMap.entrySet()) {
            Long menuId = entry.getKey();
            List<SysRoleMenu> menus = entry.getValue();
            RoleMenuScopeVO scopeVO = new RoleMenuScopeVO();
            scopeVO.setMenuId(menuId);

            // 1. 分离：找出非自定义中的最小值（最宽松），以及是否存在自定义配置
            boolean hasCustom = menus.stream().anyMatch(m -> DataScopeConstant.CUSTOM.equals(m.getDataScopeCd()));
            String minNonCustom = menus.stream().map(SysRoleMenu::getDataScopeCd).filter(cd -> !DataScopeConstant.CUSTOM.equals(cd)).min(String::compareTo)
                    .orElse(null);

            if (!hasCustom) {
                // 纯非自定义：取最小值（最宽松），默认兜底仅本人
                scopeVO.setDataScopeCd(minNonCustom != null ? minNonCustom : DataScopeConstant.SELF_ONLY);
                scopeVO.setCustomScope(null);
                scopeVO.setExtraCustomScope(null);
            } else if (minNonCustom == null) {
                // 纯自定义：原逻辑不变
                scopeVO.setDataScopeCd(DataScopeConstant.CUSTOM);
                scopeVO.setCustomScope(customScopeMap.get(menuId));
                scopeVO.setExtraCustomScope(null);
            } else if (DataScopeConstant.ALL.equals(minNonCustom)) {
                // 非自定义最宽松为"全部"：直接放行，CUSTOM 子集被吸收，无需 OR
                scopeVO.setDataScopeCd(DataScopeConstant.ALL);
                scopeVO.setCustomScope(null);
                scopeVO.setExtraCustomScope(null);
            } else {
                // 非自定义（1006002~1006004）+ 自定义共存：取非自定义最宽松值，附加 extraCustomScope 做 OR
                scopeVO.setDataScopeCd(minNonCustom);
                scopeVO.setCustomScope(null);
                scopeVO.setExtraCustomScope(customScopeMap.get(menuId));
            }
            scopeVOMap.put(menuId, scopeVO);
        }
        return scopeVOMap;
    }

}
