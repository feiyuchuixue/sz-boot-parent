package com.sz.admin.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysMenuMapper;
import com.sz.admin.system.mapper.SysUserRoleMapper;
import com.sz.admin.system.pojo.dto.scriptexport.ScriptExportDTO;
import com.sz.admin.system.pojo.dto.sysmenu.MenuPermissionDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuCreateDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuListDTO;
import com.sz.admin.system.pojo.po.SysMenu;
import com.sz.admin.system.pojo.po.SysUserRole;
import com.sz.admin.system.pojo.po.table.SysMenuTableDef;
import com.sz.admin.system.pojo.vo.scriptexport.ScriptExportVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuPermissionVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.SysMenuVO;
import com.sz.admin.system.script.AdminScriptExportService;
import com.sz.admin.system.service.SysMenuService;
import com.sz.config.FeatureProperties;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.UserPermissionChangeMessage;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.TreeUtils;
import com.sz.core.util.Utils;
import com.sz.platform.constant.dict.MenuTypeConstant;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import com.sz.platform.redis.RedisService;
import com.sz.security.core.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.sz.admin.system.pojo.po.table.SysMenuTableDef.SYS_MENU;
import static com.sz.admin.system.pojo.po.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
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

    private final RedisService redisService;

    private final AdminScriptExportService scriptExportService;

    private final EventPublisher eventPublisher;

    private final FeatureProperties featureProperties;

    /**
     * 创建菜单
     *
     * @param dto
     *            dto
     */
    @Transactional
    @Override
    public void create(SysMenuCreateDTO dto) {
        SysMenu menu = BeanCopyUtils.copy(dto, SysMenu.class);
        QueryWrapper wrapper;
        if (!(MenuTypeConstant.BUTTON).equals(dto.getMenuTypeCd())) { // 对非按钮进行唯一性校验
            wrapper = QueryWrapper.create().eq(SysMenu::getName, dto.getName()).eq(SysMenu::getDelFlag, "F");
            CommonResponseEnum.MENU_NAME_EXISTS.assertTrue(count(wrapper) > 0);

            wrapper = QueryWrapper.create().eq(SysMenu::getPath, dto.getPath()).eq(SysMenu::getDelFlag, "F");
            CommonResponseEnum.EXISTS.message("menuPath已存在").assertTrue(count(wrapper) > 0);
        }

        int deep;
        if (isRoot(dto.getPid())) {
            deep = 1;
            menu.setPid(0L);
        } else {
            deep = validateAndGetMenuDeep(null, dto.getPid());
        }
        menu.setDeep(deep);
        menu.setCreateId(Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId());
        menu.setHasChildren("F");
        save(menu);
        syncTreeDeep();
        syncTreeHasChildren();
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
        SysMenu menu = BeanCopyUtils.copy(dto, SysMenu.class);
        // 菜单是否存在
        wrapper = QueryWrapper.create().where(SysMenuTableDef.SYS_MENU.ID.eq(dto.getId()));
        CommonResponseEnum.NOT_EXISTS.message("菜单不存在").assertTrue(count(wrapper) < 1);
        int deep;
        if (isRoot(dto.getPid())) {
            deep = 1;
            menu.setPid(0L);
        } else {
            deep = validateAndGetMenuDeep(dto.getId(), dto.getPid());
        }
        menu.setDeep(deep);
        menu.setUpdateId(Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId());
        menu.setUpdateTime(LocalDateTime.now());
        updateById(menu);
        syncTreeDeep();
        syncTreeHasChildren();

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
            List<Long> list = this.mapper.selectMenuAndChildrenIds(dto.getIds());
            this.mapper.updateMenuAndChildrenIsDelete(list);
            syncTreeDeep();
            syncTreeHasChildren();
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
            wrapper.ne(SysMenu::getMenuTypeCd, MenuTypeConstant.BUTTON);
        }
        // 菜单全部数据
        List<SysMenu> list = filterDisabledFeatureMenus(list(wrapper));
        return buildMenuTree(list, false);
    }

    @Override
    public List<SysMenuVO> findMenuListByUserId(Long userId) {
        // 菜单全部数据(当前用户下的)
        QueryWrapper wrapper = QueryWrapper.create()
                .select(QueryMethods.distinct(SYS_MENU.ID, SYS_MENU.PID, SYS_MENU.PATH, SYS_MENU.NAME, SYS_MENU.TITLE, SYS_MENU.ICON, SYS_MENU.COMPONENT,
                        SYS_MENU.REDIRECT, SYS_MENU.SORT, SYS_MENU.DEEP, SYS_MENU.MENU_TYPE_CD, SYS_MENU.PERMISSIONS, SYS_MENU.IS_HIDDEN, SYS_MENU.HAS_CHILDREN,
                        SYS_MENU.IS_LINK, SYS_MENU.IS_FULL, SYS_MENU.IS_AFFIX, SYS_MENU.IS_KEEP_ALIVE, SYS_MENU.CREATE_TIME, SYS_MENU.UPDATE_TIME,
                        SYS_MENU.CREATE_ID, SYS_MENU.UPDATE_ID, SYS_MENU.DEL_FLAG, SYS_MENU.USE_DATA_SCOPE, SYS_MENU.DELETE_ID, SYS_MENU.DELETE_TIME))
                .from(SYS_USER_ROLE).leftJoin(SYS_ROLE_MENU).on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE_MENU.ROLE_ID)).leftJoin(SYS_MENU)
                .on(SYS_ROLE_MENU.MENU_ID.eq(SYS_MENU.ID)).where(SYS_MENU.MENU_TYPE_CD.ne(MenuTypeConstant.BUTTON)).where(SYS_USER_ROLE.USER_ID.eq(userId))
                .orderBy(SYS_MENU.DEEP.asc()).orderBy(SYS_MENU.SORT.asc());
        List<SysMenu> list = filterDisabledFeatureMenus(list(wrapper));
        return buildMenuTree(list, true);
    }

    @Override
    public List<MenuTreeVO> getSimpleMenuTree(Long nodeId) {
        // 创建根目录节点并将所有数据包裹在其中
        MenuTreeVO root = new MenuTreeVO();
        root.setId(0L); // 根目录ID通常为0
        root.setPid(-1L); // 设置一个无效的值作为根目录的PID
        root.setTitle("根目录"); // 根目录的标题

        QueryWrapper wrapper = QueryWrapper.create().eq(SysMenu::getDelFlag, "F").ne(SysMenu::getMenuTypeCd, MenuTypeConstant.BUTTON) // 排除按钮
                .orderBy(SYS_MENU.DEEP.asc()).orderBy(SysMenuTableDef.SYS_MENU.SORT.asc());
        List<SysMenu> list = filterDisabledFeatureMenus(list(wrapper));
        List<MenuTreeVO> menuTreeVOS = BeanCopyUtils.copyList(list, MenuTreeVO.class);
        return TreeUtils.buildTree(menuTreeVOS, root, nodeId);
    }

    @Override
    public List<MenuTreeVO> getMenuTreeVOS(Long nodeId, boolean isShowButton) {
        List<Long> childrenIds = new ArrayList<>();
        if (nodeId != null && !nodeId.equals(0L)) {
            childrenIds = this.mapper.getMenuAndChildrenIds(nodeId, isShowButton);
        }
        List<SysMenuVO> sysMenuVOS;
        if (!childrenIds.isEmpty()) {
            sysMenuVOS = menuListTree(childrenIds);
        } else {
            SysMenuListDTO dto = new SysMenuListDTO();
            dto.setShowButton(isShowButton);
            sysMenuVOS = menuList(dto);
        }
        return BeanCopyUtils.copyList(sysMenuVOS, MenuTreeVO.class);
    }

    @Override
    public List<MenuTreeVO> queryRoleMenuTree(boolean isShowButton) {
        SysMenuListDTO dto = new SysMenuListDTO();
        dto.setShowButton(isShowButton);
        List<SysMenuVO> sysMenuVOS = menuList(dto);
        return BeanCopyUtils.copyList(sysMenuVOS, MenuTreeVO.class);
    }

    @Override
    public String exportMenuSql(SelectIdsDTO dto) {
        try {
            return scriptExportService.renderMenuSql(buildMenuScriptModel(dto), null);
        } catch (IOException e) {
            log.error("exportMenuSql error", e);
            return "";
        }
    }

    @Override
    public ScriptExportVO exportMenuScript(ScriptExportDTO dto) {
        try {
            return scriptExportService.renderMenuExport(buildMenuScriptModel(dto), dto.getSqlDialect());
        } catch (IOException e) {
            log.error("exportMenuScript error", e);
            return new ScriptExportVO();
        }
    }

    private Map<String, Object> buildMenuScriptModel(SelectIdsDTO dto) {
        Map<String, Object> dataModel = new HashMap<>();
        List<SysMenu> sysMenuList = new ArrayList<>();
        if (Utils.isNotNull(dto.getIds())) {
            List<Long> list = this.mapper.selectMenuAndChildrenIds(dto.getIds());
            QueryWrapper queryWrapper = QueryWrapper.create().in(SysMenu::getId, list).orderBy(SysMenu::getDeep).asc().orderBy(SysMenu::getSort).asc();
            sysMenuList = list(queryWrapper);
        }
        dataModel.put("sysMenuList", sysMenuList);
        return dataModel;
    }

    /**
     * 菜单属性查询(排除自己和自己的子节点)
     *
     * @param excludingIds
     *            排除的id
     * @return 菜单属性
     */
    private List<SysMenuVO> menuListTree(List<Long> excludingIds) {
        QueryWrapper wrapper = QueryWrapper.create().notIn(SysMenu::getId, excludingIds).ne(SysMenu::getMenuTypeCd, "10023").orderBy(SysMenu::getDeep).asc()
                .orderBy(SysMenu::getSort).asc().eq(SysMenu::getDelFlag, "F");

        // 菜单全部数据
        List<SysMenu> list = filterDisabledFeatureMenus(list(wrapper));
        return buildMenuTree(list, false);
    }

    /**
     * 详情
     *
     * @return {@link SysMenuVO}
     */
    @Override
    public SysMenu detail(Long id) {
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
    private boolean isRoot(Long pid) {
        return pid == null || pid.equals(0L);
    }

    /**
     * 获取父级跟节点
     *
     * @param list
     *            菜单列表
     * @return 父级跟节点菜单列表
     */
    private List<SysMenuVO> getRootNodes(List<SysMenu> list) {
        List<SysMenuVO> rootList = new ArrayList<>();
        for (SysMenu sysMenu : list) {
            // 找到所有父级节点
            if (sysMenu.getPid() == null || sysMenu.getPid().equals(0L)) {
                SysMenuVO sysMenuTreeVO = BeanCopyUtils.copy(sysMenu, SysMenuVO.class);
                rootList.add(sysMenuTreeVO);
            }
        }
        return rootList;
    }

    private List<SysMenuVO> buildMenuTree(List<SysMenu> menuList, boolean fillRootMetaLink) {
        List<SysMenuVO> treeList = new ArrayList<>();
        Set<Long> renderedIds = new HashSet<>();
        Set<Long> loggedIds = new HashSet<>();
        for (SysMenuVO rootNode : getRootNodes(menuList)) {
            SysMenuVO menuVO = BeanCopyUtils.copy(rootNode, SysMenuVO.class);
            SysMenuVO.Meta meta = BeanCopyUtils.copy(rootNode, SysMenuVO.Meta.class);
            if (fillRootMetaLink) {
                meta.setIsLink(("T").equals(meta.getIsLink()) ? menuVO.getRedirect() : "");
            }
            menuVO.setMeta(meta);
            treeList.add(getChildrenNode(menuVO, menuList, new LinkedHashSet<>(), renderedIds, loggedIds));
        }
        logSkippedMenus(menuList, renderedIds, loggedIds);
        return treeList;
    }

    private List<SysMenu> filterDisabledFeatureMenus(List<SysMenu> menus) {
        if (featureProperties.isGenerator() || menus == null || menus.isEmpty()) {
            return menus;
        }
        return menus.stream().filter(menu -> !isGeneratorMenu(menu)).toList();
    }

    private boolean isGeneratorMenu(SysMenu menu) {
        if (menu == null) {
            return false;
        }
        return isGeneratorPermission(menu.getPermissions()) || containsGeneratorPath(menu.getPath()) || containsGeneratorPath(menu.getComponent());
    }

    private List<String> filterDisabledFeaturePermissions(List<String> permissions) {
        if (featureProperties.isGenerator() || permissions == null || permissions.isEmpty()) {
            return permissions;
        }
        return permissions.stream().filter(permission -> !isDisabledFeaturePermission(permission)).toList();
    }

    private boolean isDisabledFeaturePermission(String permission) {
        return !featureProperties.isGenerator() && isGeneratorPermission(permission);
    }

    private boolean isGeneratorPermission(String permission) {
        return permission != null && permission.startsWith("generator.");
    }

    private boolean containsGeneratorPath(String value) {
        return value != null && value.contains("/toolbox/generator");
    }

    private SysMenuVO getChildrenNode(SysMenuVO sysMenu, List<SysMenu> menuList, LinkedHashSet<Long> accessPath, Set<Long> renderedIds, Set<Long> loggedIds) {
        LinkedHashSet<Long> currentPath = new LinkedHashSet<>(accessPath);
        if (sysMenu.getId() != null) {
            currentPath.add(sysMenu.getId());
            renderedIds.add(sysMenu.getId());
        }
        List<SysMenuVO> childrenList = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getPid().equals(sysMenu.getId())) {
                if (menu.getId() != null && currentPath.contains(menu.getId())) {
                    if (loggedIds.add(menu.getId())) {
                        log.error("菜单树构建发现循环引用，已跳过异常节点, menuId={}, pid={}, parentId={}, title={}", menu.getId(), menu.getPid(), sysMenu.getId(),
                                sysMenu.getTitle());
                    }
                    continue;
                }
                SysMenuVO childrenNode = BeanCopyUtils.copy(menu, SysMenuVO.class);
                SysMenuVO.Meta meta = BeanCopyUtils.copy(menu, SysMenuVO.Meta.class);
                meta.setIsLink(("T").equals(meta.getIsLink()) ? childrenNode.getRedirect() : "");
                childrenNode.setMeta(meta);
                childrenList.add(getChildrenNode(childrenNode, menuList, currentPath, renderedIds, loggedIds));
            }
        }
        sysMenu.setChildren(childrenList);
        return sysMenu;
    }

    private void logSkippedMenus(List<SysMenu> menuList, Set<Long> renderedIds, Set<Long> loggedIds) {
        for (SysMenu menu : menuList) {
            if (menu.getId() != null && !renderedIds.contains(menu.getId()) && loggedIds.add(menu.getId())) {
                log.error("菜单树构建跳过未挂载节点，可能存在父节点缺失或循环引用, menuId={}, pid={}, title={}", menu.getId(), menu.getPid(), menu.getTitle());
            }
        }
    }

    private int validateAndGetMenuDeep(Long menuId, Long pid) {
        CommonResponseEnum.INVALID_ID.message("父级菜单不能选择自身").assertTrue(menuId != null && menuId.equals(pid));
        QueryWrapper parentWrapper = QueryWrapper.create().eq(SysMenu::getId, pid).eq(SysMenu::getDelFlag, "F");
        SysMenu parentMenu = getOne(parentWrapper);
        CommonResponseEnum.INVALID_ID.message("父级菜单不存在").assertNull(parentMenu);
        if (menuId != null) {
            Set<Long> descendants = getMenuDescendantIds(menuId);
            CommonResponseEnum.INVALID_ID.message("父级菜单不能选择当前菜单的子节点").assertTrue(descendants.contains(pid));
        }
        return parentMenu.getDeep() + 1;
    }

    private Set<Long> getMenuDescendantIds(Long menuId) {
        Map<Long, List<Long>> childrenMap = list(QueryWrapper.create().eq(SysMenu::getDelFlag, "F")).stream()
                .filter(menu -> menu.getPid() != null && menu.getId() != null)
                .collect(Collectors.groupingBy(SysMenu::getPid, Collectors.mapping(SysMenu::getId, Collectors.toList())));
        Set<Long> descendantIds = new HashSet<>();
        Deque<Long> deque = new ArrayDeque<>();
        descendantIds.add(menuId);
        deque.add(menuId);
        while (!deque.isEmpty()) {
            Long currentId = deque.removeFirst();
            for (Long childId : childrenMap.getOrDefault(currentId, Collections.emptyList())) {
                if (descendantIds.add(childId)) {
                    deque.addLast(childId);
                }
            }
        }
        return descendantIds;
    }

    /**
     * 验证是否有权限标识
     *
     * @param dto
     *            dto
     * @return 权限标识对象
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

    /**
     * 查询权限按钮
     *
     * @return 权限集合
     */
    @Override
    public List<String> findPermission() {
        return filterDisabledFeaturePermissions(sysUserRoleMapper.queryPermissionByUserId(StpUtil.getLoginIdAsLong()));
    }

    @Override
    public List<String> findPermissionsByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_MENU.PERMISSIONS)).from(SYS_MENU).leftJoin(SYS_ROLE_MENU)
                .on(SYS_MENU.ID.eq(SYS_ROLE_MENU.MENU_ID)).leftJoin(SYS_USER_ROLE).on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId)).where(SYS_MENU.PERMISSIONS.isNotNull()).where(SYS_MENU.PERMISSIONS.ne(""));
        return filterDisabledFeaturePermissions(listAs(queryWrapper, String.class));
    }

    @Override
    public List<String> findAllPermissions() {
        QueryWrapper queryWrapper = QueryWrapper.create().select(QueryMethods.distinct(SYS_MENU.PERMISSIONS)).from(SYS_MENU).eq(SysMenu::getDelFlag, "F")
                .isNotNull(SysMenu::getPermissions).ne(SysMenu::getPermissions, "");

        return filterDisabledFeaturePermissions(listAs(queryWrapper, String.class));
    }

    /**
     * 全量刷新菜单树的 deep 字段（兼容 MySQL 和 PostgreSQL）。
     * <p>
     * 原逻辑：每个节点的 deep = 父节点 deep + 1，根节点（pid=0）不参与更新。 通过 Java 层推导替代 MySQL 专有的
     * UPDATE...JOIN 语法。
     */
    private void syncTreeDeep() {
        // 查询所有未删除菜单的 id / pid / deep
        List<SysMenu> allMenus = QueryChain.of(SysMenu.class).select(SYS_MENU.ID, SYS_MENU.PID, SYS_MENU.DEEP).where(SYS_MENU.DEL_FLAG.eq("F")).list();
        if (allMenus.isEmpty())
            return;

        // 构建 id -> deep 映射，用于查父节点 deep
        Map<Long, Integer> deepMap = allMenus.stream().collect(Collectors.toMap(SysMenu::getId, SysMenu::getDeep));

        // 遍历非根节点，计算期望 deep，收集需要更新的行
        List<SysMenu> toUpdate = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getPid() == null || menu.getPid().equals(0L))
                continue;
            Integer parentDeep = deepMap.get(menu.getPid());
            if (parentDeep == null)
                continue;
            int expectedDeep = parentDeep + 1;
            if (menu.getDeep() == null || expectedDeep != menu.getDeep()) {
                SysMenu update = new SysMenu();
                update.setId(menu.getId());
                update.setDeep(expectedDeep);
                toUpdate.add(update);
            }
        }
        if (!toUpdate.isEmpty()) {
            updateBatch(toUpdate);
        }
    }

    /**
     * 全量刷新菜单树的 has_children 字段（兼容 MySQL 和 PostgreSQL）。
     * <p>
     * 原逻辑：有子节点的菜单标记 'T'，无子节点标记 'F'。 比原 SQL 更完整：原 SQL 只写 'T'，删除子节点后 'F' 不会被还原；此处同时修正
     * 'F'。
     */
    private void syncTreeHasChildren() {
        // 查询所有未删除菜单的 id / pid / has_children
        List<SysMenu> allMenus = QueryChain.of(SysMenu.class).select(SYS_MENU.ID, SYS_MENU.PID, SYS_MENU.HAS_CHILDREN).where(SYS_MENU.DEL_FLAG.eq("F")).list();
        if (allMenus.isEmpty())
            return;

        // 统计所有作为 pid 出现的 id（即有子节点的菜单 id）
        Set<Long> parentIds = allMenus.stream().map(SysMenu::getPid).filter(pid -> pid != null && !pid.equals(0L)).collect(Collectors.toSet());

        // 遍历全部菜单，对比期望值与当前值，收集需更新的行
        List<SysMenu> toUpdate = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            String expected = parentIds.contains(menu.getId()) ? "T" : "F";
            if (!expected.equals(menu.getHasChildren())) {
                SysMenu update = new SysMenu();
                update.setId(menu.getId());
                update.setHasChildren(expected);
                toUpdate.add(update);
            }
        }
        if (!toUpdate.isEmpty()) {
            updateBatch(toUpdate);
        }
    }

    @Override
    public Map<String, Long> getBtnMenuByPermissions(Collection<String> permissions) {
        Map<String, Long> btnMenuMap = new HashMap<>();
        if (permissions.isEmpty()) {
            return btnMenuMap;
        }
        try {
            QueryWrapper wrapper = QueryWrapper.create().from(SYS_MENU).where(SYS_MENU.PID
                    .in(select(SYS_MENU.ID).from(SYS_MENU).where(SYS_MENU.USE_DATA_SCOPE.eq("T")).where(SYS_MENU.MENU_TYPE_CD.eq(MenuTypeConstant.MENU))));
            List<SysMenu> list = list(wrapper);
            if (list.isEmpty()) {
                return btnMenuMap;
            }
            Set<Long> pids = list.stream().map(SysMenu::getPid).collect(Collectors.toSet());
            QueryWrapper checkWrapper = QueryWrapper.create().select(SYS_MENU.ID).from(SYS_MENU).where(SYS_MENU.ID.in(pids));
            List<Long> existsMenuIds = listAs(checkWrapper, Long.class);
            for (SysMenu menu : list) {
                if (existsMenuIds.contains(menu.getPid()) || existsMenuIds.contains(menu.getId())) { // 过滤脏数据
                    String key = menu.getPermissions();
                    if (isDisabledFeaturePermission(key)) {
                        continue;
                    }
                    Long value;
                    if (MenuTypeConstant.MENU.equals(menu.getMenuTypeCd())) {
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
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_MENU.USE_DATA_SCOPE.eq("T")).where(SYS_MENU.MENU_TYPE_CD.eq(MenuTypeConstant.MENU));
        List<SysMenu> list = list(wrapper);
        return BeanCopyUtils.copyList(list, MenuTreeVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeMenuDataScope(Long menuId) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_MENU.ID.eq(menuId));
        SysMenu menu = getOne(wrapper);
        CommonResponseEnum.INVALID_ID.assertNull(menu);
        String useDataScope;
        if (("F").equals(menu.getUseDataScope())) {
            useDataScope = "T";
            menu.setUseDataScope(useDataScope);
        } else {
            useDataScope = "F";
            menu.setUseDataScope(useDataScope);
        }
        updateById(menu);
        UpdateChain.of(SysMenu.class).set(SYS_MENU.USE_DATA_SCOPE, useDataScope).where(SYS_MENU.PID.eq(menu.getId())).update();
        List<Long> changeUserIds = QueryChain.of(SysUserRole.class).select(SYS_USER_ROLE.USER_ID).from(SYS_USER_ROLE).leftJoin(SYS_ROLE_MENU)
                .on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID)).where(SYS_ROLE_MENU.MENU_ID.eq(menuId)).listAs(Long.class);
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(changeUserIds)));

    }

}
