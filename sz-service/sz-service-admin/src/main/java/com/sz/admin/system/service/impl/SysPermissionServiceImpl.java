package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.sz.admin.system.pojo.po.SysDept;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.po.SysUserDataRole;
import com.sz.admin.system.pojo.po.SysUserDept;
import com.sz.admin.system.pojo.vo.sysuser.SysUserDataMetaVO;
import com.sz.admin.system.service.SysDeptClosureService;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysPermissionService;
import com.sz.admin.system.service.SysUserRoleService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysDataRoleMenuTableDef.SYS_DATA_ROLE_MENU;
import static com.sz.admin.system.pojo.po.table.SysDataRoleRelationTableDef.SYS_DATA_ROLE_RELATION;
import static com.sz.admin.system.pojo.po.table.SysDataRoleTableDef.SYS_DATA_ROLE;
import static com.sz.admin.system.pojo.po.table.SysDeptTableDef.SYS_DEPT;
import static com.sz.admin.system.pojo.po.table.SysMenuTableDef.SYS_MENU;
import static com.sz.admin.system.pojo.po.table.SysUserDataRoleTableDef.SYS_USER_DATA_ROLE;
import static com.sz.admin.system.pojo.po.table.SysUserDeptTableDef.SYS_USER_DEPT;

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

    private final SysMenuService sysMenuService;

    private final SysUserRoleService sysUserRoleService;

    private final SysDeptClosureService sysDeptClosureService;

    @Override
    public Set<String> getMenuPermissions(SysUser sysUser) {
        Set<String> permissions = new HashSet<>();
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
        SysUser sysUser = QueryChain.of(SysUser.class)
                .eq(SysUser::getId, userId).one();
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
        if (isSuperAdmin(sysUser)) {
            // 查询全部的部门ID
            return QueryChain.of(SysDept.class)
                    .select(SYS_DEPT.ID)
                    .listAs(Long.class);
        } else {
            return QueryChain.of(SysUserDept.class)
                    .select(SYS_USER_DEPT.DEPT_ID)
                    .where(SYS_USER_DEPT.USER_ID.eq(sysUser.getId()))
                    .listAs(Long.class);
        }
    }

    @Override
    public List<Long> getDeptAndChildren(SysUser sysUser) {
        if (isSuperAdmin(sysUser)) {
            // 查询全部的部门ID
            return QueryChain.of(SysDept.class)
                    .select(SYS_DEPT.ID)
                    .listAs(Long.class);
        } else {
            return sysDeptClosureService.descendants(getDepts(sysUser));
        }
    }

    public Map<String, String> buildMenuRuleMap(SysUser sysUser, Set<String> findMenuIds) {
        Map<String, String> ruleMap = new HashMap<>();

        // 如果用户是超级管理员，返回空规则映射
        if (isSuperAdmin(sysUser)) {
            return ruleMap;
        }

        // 如果没有菜单ID，返回默认规则
        if (findMenuIds.isEmpty()) {
            return ruleMap;
        }

        // 获取用户的部门信息
        List<Long> depts = getDepts(sysUser);
        if (depts.isEmpty()) {
            buildCustomScope(sysUser, findMenuIds, ruleMap);
            return ruleMap;
        }

        // 获取用户数据角色的元信息
        List<SysUserDataMetaVO> metaVOList = QueryChain.of(SysUserDataRole.class)
                .select(
                        SYS_USER_DATA_ROLE.USER_ID,
                        SYS_USER_DATA_ROLE.ROLE_ID,
                        SYS_DATA_ROLE.DATA_SCOPE_CD,
                        SYS_DATA_ROLE_MENU.MENU_ID,
                        SYS_DATA_ROLE_RELATION.RELATION_ID
                )
                .from(SYS_USER_DATA_ROLE)
                .leftJoin(SYS_DATA_ROLE).on(SYS_USER_DATA_ROLE.ROLE_ID.eq(SYS_DATA_ROLE.ID))
                .leftJoin(SYS_DATA_ROLE_MENU).on(SYS_USER_DATA_ROLE.ROLE_ID.eq(SYS_DATA_ROLE_MENU.ROLE_ID))
                .leftJoin(SYS_DATA_ROLE_RELATION).on(SYS_USER_DATA_ROLE.ROLE_ID.eq(SYS_DATA_ROLE_RELATION.ROLE_ID))
                .leftJoin(SYS_MENU).on(SYS_MENU.ID.eq(SYS_DATA_ROLE_MENU.MENU_ID)).where(SYS_MENU.USE_DATA_SCOPE.eq("T"))
                .where(SYS_USER_DATA_ROLE.USER_ID.eq(sysUser.getId()))
                .where(SYS_DATA_ROLE_RELATION.RELATION_TYPE_CD.eq("1007001"))
                .where(SYS_DATA_ROLE_RELATION.RELATION_ID.in(depts))
                .where(SYS_DATA_ROLE.DATA_SCOPE_CD.ne("1006005"))
                .where(SYS_DATA_ROLE_MENU.MENU_ID.in(findMenuIds))
                .where(SYS_DATA_ROLE.DATA_SCOPE_CD.isNotNull())
                .listAs(SysUserDataMetaVO.class);

        System.out.println("metaVOList ==" + JsonUtils.toJsonString(metaVOList));
        // 聚合菜单下的权限规则 （一次聚合）
        Map<String, List<SysUserDataMetaVO>> menuMap = metaVOList.stream()
                .collect(Collectors.groupingBy(SysUserDataMetaVO::getMenuId));
        System.out.println("一次聚合 menuMap ==" + JsonUtils.toJsonString(menuMap));
        // 根据最小权限规则生成最终规则映射 （二次聚合）
        for (Map.Entry<String, List<SysUserDataMetaVO>> entry : menuMap.entrySet()) {
            String menuId = entry.getKey();
            List<SysUserDataMetaVO> values = entry.getValue();
            SysUserDataMetaVO metaVO = values.stream()
                    .min(Comparator.comparing(SysUserDataMetaVO::getDataScopeCd))
                    .orElse(null);
            ruleMap.put(menuId, metaVO != null ? metaVO.getDataScopeCd() : "");
        }
        System.out.println("二次聚合 ruleMap ==" + JsonUtils.toJsonString(ruleMap));

        buildCustomScope(sysUser, findMenuIds, ruleMap);
        return ruleMap;
    }

    private static void buildCustomScope(SysUser sysUser, Set<String> findMenuIds, Map<String, String> ruleMap) {
        // 获取自定义权限
        List<SysUserDataMetaVO> customMetaList = QueryChain.of(SysUserDataRole.class)
                .select(
                        SYS_USER_DATA_ROLE.USER_ID,
                        SYS_USER_DATA_ROLE.ROLE_ID,
                        SYS_DATA_ROLE.DATA_SCOPE_CD,
                        SYS_DATA_ROLE_MENU.MENU_ID,
                        SYS_DATA_ROLE_RELATION.RELATION_ID,
                        SYS_DATA_ROLE_RELATION.RELATION_TYPE_CD
                )
                .from(SYS_USER_DATA_ROLE)
                .leftJoin(SYS_DATA_ROLE).on(SYS_USER_DATA_ROLE.ROLE_ID.eq(SYS_DATA_ROLE.ID))
                .leftJoin(SYS_DATA_ROLE_MENU).on(SYS_USER_DATA_ROLE.ROLE_ID.eq(SYS_DATA_ROLE_MENU.ROLE_ID))
                .leftJoin(SYS_DATA_ROLE_RELATION).on(SYS_USER_DATA_ROLE.ROLE_ID.eq(SYS_DATA_ROLE_RELATION.ROLE_ID))
                .leftJoin(SYS_MENU).on(SYS_MENU.ID.eq(SYS_DATA_ROLE_MENU.MENU_ID)).where(SYS_MENU.USE_DATA_SCOPE.eq("T"))
                .where(SYS_USER_DATA_ROLE.USER_ID.eq(sysUser.getId()))
                .where(SYS_DATA_ROLE.DATA_SCOPE_CD.eq("1006005"))
                .where(SYS_DATA_ROLE_MENU.MENU_ID.in(findMenuIds))
                .listAs(SysUserDataMetaVO.class);

        Set<Long> customUserIds = new HashSet<>();
        Set<Long> customDeptIds = new HashSet<>();
        for (SysUserDataMetaVO metaVO : customMetaList) {
            String menuId = metaVO.getMenuId();
            if (("1007001").equals(metaVO.getRelationTypeCd())) { // 自定义菜单
                if (ruleMap.containsKey(menuId)) { // 如果规则中已经存在当前menuId的规则，判断权限大小。以最大范围原则进行取值。
                    if (("1006004").equals(ruleMap.get(menuId))) { // 仅本人时，替换选用自定义权限,否则不替换 ！！！
                        ruleMap.put(metaVO.getMenuId(), metaVO.getDataScopeCd());
                    }
                } else {
                    ruleMap.put(metaVO.getMenuId(), metaVO.getDataScopeCd());
                }
                customDeptIds.add(metaVO.getRelationId());
            } else { // 自定义用户
                customUserIds.add(metaVO.getRelationId());
                if (ruleMap.containsKey(menuId)) {
                    if (("1006004").equals(ruleMap.get(menuId))) { // 仅本人时，替换选用自定义权限,否则不替换 ！！！
                        ruleMap.put(metaVO.getMenuId(), metaVO.getDataScopeCd());
                    }
                } else {
                    ruleMap.put(menuId, metaVO.getDataScopeCd());
                }
            }
        }
        // 将自定义权限添加到规则映射中
        if (!customUserIds.isEmpty()) {
            ruleMap.put("customUser", JsonUtils.toJsonString(customUserIds));
        }
        if (!customDeptIds.isEmpty()) {
            ruleMap.put("customDept", JsonUtils.toJsonString(customDeptIds));
        }
    }

    private boolean isSuperAdmin(SysUser sysUser) {
        if (sysUser != null && ("1001002").equals(sysUser.getUserTagCd())) {
            return true;
        }
        return false;
    }

}
