package com.sz.admin.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysMenuMapper;
import com.sz.admin.system.mapper.SysUserRoleMapper;
import com.sz.admin.system.pojo.dto.sysmenu.MenuPermissionDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuCreateDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuListDTO;
import com.sz.admin.system.pojo.po.SysMenu;
import com.sz.admin.system.pojo.po.SysUserDataRole;
import com.sz.admin.system.pojo.po.table.SysMenuTableDef;
import com.sz.admin.system.pojo.vo.sysmenu.MenuPermissionVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.SysMenuVO;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysRoleService;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.TreeUtils;
import com.sz.core.util.Utils;
import com.sz.generator.service.GeneratorTableService;
import com.sz.platform.enums.AdminResponseEnum;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import com.sz.redis.RedisService;
import com.sz.security.core.util.LoginUtils;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysDataRoleMenuTableDef.SYS_DATA_ROLE_MENU;
import static com.sz.admin.system.pojo.po.table.SysMenuTableDef.SYS_MENU;
import static com.sz.admin.system.pojo.po.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static com.sz.admin.system.pojo.po.table.SysUserDataRoleTableDef.SYS_USER_DATA_ROLE;
import static com.sz.admin.system.pojo.po.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysRoleService sysRoleService;

    private final RedisService redisService;

    private final GeneratorTableService generatorTableService;

    private final EventPublisher eventPublisher;

    /**
     * 创建菜单
     *
     * @param dto
     *            dto
     */
    @Transactional
    @Override
    public void create(SysMenuCreateDTO dto) {
        SysMenu menu = BeanCopyUtils.springCopy(dto, SysMenu.class);
        menu.setId(Utils.generateUUIDs());
        QueryWrapper wrapper;
        if (!("1002003").equals(dto.getMenuTypeCd())) { // 对非按钮进行唯一性校验
            wrapper = QueryWrapper.create().eq(SysMenu::getName, dto.getName()).eq(SysMenu::getDelFlag, "F");
            AdminResponseEnum.MENU_NAME_EXISTS.assertTrue(count(wrapper) > 0);

            wrapper = QueryWrapper.create().eq(SysMenu::getPath, dto.getPath()).eq(SysMenu::getDelFlag, "F");
            CommonResponseEnum.EXISTS.message("menuPath已存在").assertTrue(count(wrapper) > 0);
        }

        int deep;
        if (isRoot(dto.getPid())) {
            deep = 1;
            menu.setPid("0");
        } else {
            wrapper = QueryWrapper.create().where(SysMenuTableDef.SYS_MENU.ID.eq(dto.getPid()));
            Integer parentDeep = getOne(wrapper).getDeep();
            deep = parentDeep + 1;
        }
        menu.setDeep(deep);
        menu.setCreateId(LoginUtils.getLoginUser().getUserInfo().getId());
        menu.setHasChildren("F");
        save(menu);
        this.mapper.syncTreeDeep();
        this.mapper.syncTreeHasChildren();
        // 发布Permission 变更通知
        UserPermissionChangeMessage message = new UserPermissionChangeMessage(null, true);
        redisService.sendPermissionChangeMsg(message);

    }

    /**
     * 更新菜单
     *
     * @param dto
     *            dto
     */
    @Transactional
    @Override
    public void update(SysMenuCreateDTO dto) {
        QueryWrapper wrapper;
        SysMenu menu = BeanCopyUtils.springCopy(dto, SysMenu.class);
        // 菜单是否存在
        wrapper = QueryWrapper.create().where(SysMenuTableDef.SYS_MENU.ID.eq(dto.getId()));
        CommonResponseEnum.NOT_EXISTS.message("菜单不存在").assertTrue(count(wrapper) < 1);
        menu.setUpdateId(LoginUtils.getLoginUser().getUserInfo().getId());
        menu.setUpdateTime(LocalDateTime.now());
        updateById(menu);
        this.mapper.syncTreeDeep();
        this.mapper.syncTreeHasChildren();

        // 发布Permission 变更通知
        UserPermissionChangeMessage message = new UserPermissionChangeMessage(null, true);
        redisService.sendPermissionChangeMsg(message);

    }

    /**
     * 删除
     *
     * @param dto
     *            dto
     */
    @Transactional
    @Override
    public void remove(SelectIdsDTO dto) {
        if (Utils.isNotNull(dto.getIds())) {
            // 递归查询下边的子节点id
            List<String> list = this.mapper.selectMenuAndChildrenIds((List<String>) dto.getIds());
            this.mapper.updateMenuAndChildrenIsDelete(list);
            this.mapper.syncTreeDeep();
            this.mapper.syncTreeHasChildren();
            // 同时删除角色
            // sysRoleService.removeByMenuId(new SelectIdsDTO(list));
            // 发布Permission 变更通知
            UserPermissionChangeMessage message = new UserPermissionChangeMessage(null, true);
            redisService.sendPermissionChangeMsg(message);
        }
    }

    /**
     * 列表
     *
     * @param dto
     *            dto
     * @return {@link List}<{@link SysMenuVO}>
     */
    @Override
    public List<SysMenuVO> menuList(SysMenuListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysMenu::getDelFlag, "F")
                /* .orderBy(SYS_MENU.DEEP.asc()) */
                .orderBy(SysMenuTableDef.SYS_MENU.SORT.asc());

        if (!dto.isShowButton()) {
            wrapper.ne(SysMenu::getMenuTypeCd, "1002003");
        }
        // 菜单全部数据
        List<SysMenu> list = list(wrapper);
        List<SysMenuVO> treeList = new ArrayList<>();
        // 构建树形
        for (SysMenuVO rootNode : getRootNodes(list)) {
            SysMenuVO menuVO = BeanCopyUtils.springCopy(rootNode, SysMenuVO.class);
            SysMenuVO.Meta meta = BeanCopyUtils.springCopy(rootNode, SysMenuVO.Meta.class);
            menuVO.setMeta(meta);
            SysMenuVO childrenNode = getChildrenNode(menuVO, list);
            treeList.add(childrenNode);
        }
        return treeList;
    }

    @Override
    public List<SysMenuVO> findMenuListByUserId(Long userId) {
        List<SysMenuVO> treeList = new ArrayList<>();
        // 查询用户具有的menu_id
        List<String> menuIds = sysUserRoleMapper.queryMenuIdByUserId(userId);
        if (Utils.isNotNull(menuIds)) {
            // 菜单全部数据(当前用户下的)
            QueryWrapper wrapper = QueryWrapper.create().in(SysMenu::getId, menuIds).eq(SysMenu::getDelFlag, "F").ne(SysMenu::getMenuTypeCd, "1002003")
                    .orderBy(SysMenu::getDeep).asc().orderBy(SysMenu::getSort).asc();
            List<SysMenu> list = list(wrapper);
            // 构建树形
            for (SysMenuVO rootNode : getRootNodes(list)) {
                SysMenuVO menuVO = BeanCopyUtils.springCopy(rootNode, SysMenuVO.class);
                SysMenuVO.Meta meta = BeanCopyUtils.springCopy(rootNode, SysMenuVO.Meta.class);
                meta.setIsLink(("T").equals(meta.getIsLink()) ? menuVO.getRedirect() : "");
                menuVO.setMeta(meta);
                SysMenuVO childrenNode = getChildrenNode(menuVO, list);
                treeList.add(childrenNode);
            }
        }
        return treeList;
    }

    @Override
    public List<MenuTreeVO> getSimpleMenuTree(String nodeId) {
        // 创建根目录节点并将所有数据包裹在其中
        MenuTreeVO root = new MenuTreeVO();
        root.setId("0"); // 根目录ID通常为0
        root.setPid("-1"); // 设置一个无效的值作为根目录的PID
        root.setTitle("根目录"); // 根目录的标题

        QueryWrapper wrapper = QueryWrapper.create().eq(SysMenu::getDelFlag, "F").ne(SysMenu::getMenuTypeCd, "1002003") // 排除按钮
                .orderBy(SYS_MENU.DEEP.asc()).orderBy(SysMenuTableDef.SYS_MENU.SORT.asc());
        List<SysMenu> list = list(wrapper);
        List<MenuTreeVO> menuTreeVOS = BeanCopyUtils.copyList(list, MenuTreeVO.class);
        List<MenuTreeVO> tree = TreeUtils.buildTree(menuTreeVOS, root, nodeId);
        return tree;
    }

    @Override
    public List<MenuTreeVO> getMenuTreeVOS(String nodeId, boolean isShowButton) {
        List<String> childrenIds = new ArrayList<>();
        if (nodeId != null && !nodeId.equals("0")) {
            childrenIds = this.mapper.getMenuAndChildrenIds(nodeId, isShowButton);
        }
        List<SysMenuVO> sysMenuVOS;
        if (childrenIds.size() > 0) {
            sysMenuVOS = menuListTree(childrenIds);
        } else {
            SysMenuListDTO dto = new SysMenuListDTO();
            dto.setShowButton(isShowButton);
            sysMenuVOS = menuList(dto);
        }
        List<MenuTreeVO> menuTreeVOS = BeanCopyUtils.copyList(sysMenuVOS, MenuTreeVO.class);
        return menuTreeVOS;
    }

    @Override
    public List<MenuTreeVO> queryRoleMenuTree(boolean isShowButton) {
        SysMenuListDTO dto = new SysMenuListDTO();
        dto.setShowButton(isShowButton);
        List<SysMenuVO> sysMenuVOS = menuList(dto);
        List<MenuTreeVO> menuTreeVOS = BeanCopyUtils.copyList(sysMenuVOS, MenuTreeVO.class);
        return menuTreeVOS;
    }

    @SneakyThrows
    @Override
    public String exportMenuSql(SelectIdsDTO dto) {
        String generatedContent = "";
        if (Utils.isNotNull(dto.getIds())) {
            // 递归查询下边的子节点id
            List<String> list = this.mapper.selectMenuAndChildrenIds((List<String>) dto.getIds());
            QueryWrapper queryWrapper = QueryWrapper.create().in(SysMenu::getId, list).orderBy(SysMenu::getDeep).asc().orderBy(SysMenu::getSort).asc();
            List<SysMenu> sysMenuList = list(queryWrapper);
            if (Utils.isNotNull(sysMenuList)) {
                Map<String, Object> dataModel = new HashMap<>();
                dataModel.put("sysMenuList", sysMenuList);
                Template template = generatorTableService.getMenuSqlTemplate();
                StringWriter writer = new StringWriter();
                try {
                    template.process(dataModel, writer);
                    generatedContent = writer.toString();
                } finally {
                    writer.close();
                }
            }
        }
        return generatedContent;
    }

    /**
     * 菜单属性查询(排除自己和自己的子节点)
     *
     * @param excludingIds
     * @return
     */
    private List<SysMenuVO> menuListTree(List<String> excludingIds) {
        QueryWrapper wrapper = QueryWrapper.create().notIn(SysMenu::getId, excludingIds).ne(SysMenu::getMenuTypeCd, "10023").orderBy(SysMenu::getDeep).asc()
                .orderBy(SysMenu::getSort).asc().eq(SysMenu::getDelFlag, "F");

        // 菜单全部数据
        List<SysMenu> list = list(wrapper);
        List<SysMenuVO> treeList = new ArrayList<>();
        // 构建树形
        for (SysMenuVO rootNode : getRootNodes(list)) {
            SysMenuVO menuVO = BeanCopyUtils.springCopy(rootNode, SysMenuVO.class);
            SysMenuVO.Meta meta = BeanCopyUtils.springCopy(rootNode, SysMenuVO.Meta.class);
            menuVO.setMeta(meta);
            SysMenuVO childrenNode = getChildrenNode(menuVO, list);
            treeList.add(childrenNode);
        }
        return treeList;
    }

    /**
     * 详情
     *
     * @return {@link SysMenuVO}
     */
    @Override
    public SysMenu detail(String id) {
        SysMenu menu = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(menu);
        return menu;
    }

    /**
     * 是否是根节点
     *
     * @param pid
     *            父级Id
     * @return true:是根节点
     */
    private boolean isRoot(String pid) {
        return pid == null || pid.equals("0");
    }

    /**
     * 获取父级跟节点
     *
     * @param list
     * @return
     */
    private List<SysMenuVO> getRootNodes(List<SysMenu> list) {
        List<SysMenuVO> rootList = new ArrayList<>();
        for (SysMenu sysMenu : list) {
            // 找到所有父级节点
            if (sysMenu.getPid() == null || sysMenu.getPid().equals("0")) {
                SysMenuVO sysMenuTreeVO = BeanCopyUtils.springCopy(sysMenu, SysMenuVO.class);
                rootList.add(sysMenuTreeVO);
            }
        }
        return rootList;
    }

    private SysMenuVO getChildrenNode(SysMenuVO sysMenu, List<SysMenu> menuList) {
        List<SysMenuVO> childrenList = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getPid().equals(sysMenu.getId())) {
                SysMenuVO childrenNode = BeanCopyUtils.springCopy(menu, SysMenuVO.class);
                SysMenuVO.Meta meta = BeanCopyUtils.springCopy(menu, SysMenuVO.Meta.class);
                meta.setIsLink(("T").equals(meta.getIsLink()) ? childrenNode.getRedirect() : "");
                childrenNode.setMeta(meta);
                childrenList.add(getChildrenNode(childrenNode, menuList));
            }
        }
        sysMenu.setChildren(childrenList);
        return sysMenu;
    }

    /**
     * 验证是否有权限标识
     *
     * @param dto
     * @return
     */
    @Override
    public MenuPermissionVO hasExistsPermissions(MenuPermissionDTO dto) {
        MenuPermissionVO permissionVO = new MenuPermissionVO();
        if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
            return permissionVO;
        }
        QueryWrapper wrapper = QueryWrapper.create().ne(SysMenu::getId, dto.getId()).eq(SysMenu::getPermissions, dto.getPermissions());
        long count = count(wrapper);
        permissionVO.setPermissionCount((int) count);
        return permissionVO;
    }

    @Override
    /**
     * 查询权限按钮
     *
     * @return
     */
    public List<String> findPermission() {
        List<String> permissions = sysUserRoleMapper.queryPermissionByUserId(StpUtil.getLoginIdAsLong());
        return permissions;
    }

    @Override
    public List<String> findPermissionsByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_MENU.PERMISSIONS)).from(SYS_MENU).leftJoin(SYS_ROLE_MENU)
                .on(SYS_MENU.ID.eq(SYS_ROLE_MENU.MENU_ID)).leftJoin(SYS_USER_ROLE).on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId)).where(SYS_MENU.PERMISSIONS.isNotNull()).where(SYS_MENU.PERMISSIONS.ne(""));
        List<String> list = listAs(queryWrapper, String.class);
        return list;
    }

    @Override
    public List<String> findAllPermissions() {
        QueryWrapper queryWrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_MENU.PERMISSIONS)).from(SYS_MENU).eq(SysMenu::getDelFlag, "F")
                .isNotNull(SysMenu::getPermissions).ne(SysMenu::getPermissions, "");

        return listAs(queryWrapper, String.class);
    }

    @Override
    public Map<String, String> getBtnMenuByPermissions(Collection<String> permissions) {
        Map<String, String> btnMenuMap = new HashMap<>();
        if (permissions.isEmpty()) {
            return btnMenuMap;
        }
        try {
            QueryWrapper wrapper = QueryWrapper.create().from(SYS_MENU).where(SYS_MENU.PERMISSIONS.in(permissions));
            List<SysMenu> list = list(wrapper);
            if (list.isEmpty()) {
                return btnMenuMap;
            }
            Set<String> pids = list.stream().map(SysMenu::getPid).collect(Collectors.toSet());
            QueryWrapper checkWrapper = QueryWrapper.create().select(SYS_MENU.ID).from(SYS_MENU).where(SYS_MENU.ID.in(pids));
            List<String> existsMenuIds = listAs(checkWrapper, String.class);
            for (SysMenu menu : list) {
                if (existsMenuIds.contains(menu.getPid()) || existsMenuIds.contains(menu.getId())) { // 过滤脏数据
                    String key = menu.getPermissions();
                    String value;
                    if (("1002002").equals(menu.getMenuTypeCd())) {
                        value = menu.getId();
                    } else {
                        value = menu.getPid();
                    }
                    btnMenuMap.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error(" sync menuButton info err ", e);
        }
        return btnMenuMap;
    }

    @Override
    public List<MenuTreeVO> queryDataRoleMenu() {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_MENU.USE_DATA_SCOPE.eq("T")).where(SYS_MENU.MENU_TYPE_CD.eq("1002002"));
        List<SysMenu> list = list(wrapper);
        List<MenuTreeVO> menuTreeVOS = BeanCopyUtils.copyList(list, MenuTreeVO.class);
        return menuTreeVOS;
    }

    @Override
    public void changeMenuDataScope(String menuId) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_MENU.ID.eq(menuId));
        SysMenu menu = getOne(wrapper);
        CommonResponseEnum.INVALID_ID.assertNull(menu);
        if (("F").equals(menu.getUseDataScope())) {
            menu.setUseDataScope("T");
        } else {
            menu.setUseDataScope("F");
        }
        updateById(menu);
        List<Long> changeUserIds = QueryChain.of(SysUserDataRole.class).select(SYS_USER_DATA_ROLE.USER_ID).from(SYS_USER_DATA_ROLE).leftJoin(SYS_DATA_ROLE_MENU)
                .on(SYS_DATA_ROLE_MENU.ROLE_ID.eq(SYS_USER_DATA_ROLE.ROLE_ID)).where(SYS_DATA_ROLE_MENU.MENU_ID.eq(menuId)).listAs(Long.class);
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(changeUserIds)));
    }

}
