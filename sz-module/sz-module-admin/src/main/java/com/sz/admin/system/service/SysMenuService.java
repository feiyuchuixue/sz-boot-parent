package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysmenu.MenuPermissionDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuCreateDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysMenuListDTO;
import com.sz.admin.system.pojo.po.SysMenu;
import com.sz.admin.system.pojo.vo.sysmenu.MenuPermissionVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.SysMenuVO;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.generator.pojo.dto.ScriptExportDTO;
import com.sz.generator.pojo.vo.ScriptExportVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统菜单表 服务类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 创建
     *
     * @param dto
     *            dto
     */
    void create(SysMenuCreateDTO dto);

    /**
     * 更新
     *
     * @param dto
     *            dto
     */
    void update(SysMenuCreateDTO dto);

    /**
     * 删除菜单
     *
     * @param dto
     *            dto
     */
    void remove(SelectIdsDTO dto);

    /**
     * 列表
     *
     * @param dto
     *            dto
     * @return {@link List}<{@link SysMenuVO}>
     */
    List<SysMenuVO> menuList(SysMenuListDTO dto);

    List<SysMenuVO> findMenuListByUserId(Long userId);

    List<MenuTreeVO> getSimpleMenuTree(Long nodeId);

    List<MenuTreeVO> getMenuTreeVOS(Long nodeId, boolean isShowButton);

    List<MenuTreeVO> queryRoleMenuTree(boolean isShowButton);

    String exportMenuSql(SelectIdsDTO dto);

    ScriptExportVO exportMenuScript(ScriptExportDTO dto);

    /**
     * 详情
     *
     * @return {@link SysMenu}
     */
    SysMenu detail(Long id);

    MenuPermissionVO hasExistsPermissions(MenuPermissionDTO dto);

    /**
     * 查询权限按钮
     *
     * @return 权限按钮集合
     */
    List<String> findPermission();

    List<String> findPermissionsByUserId(Long userId);

    List<String> findAllPermissions();

    Map<String, Long> getBtnMenuByPermissions(Collection<String> permissions);

    List<MenuTreeVO> queryDataRoleMenu();

    void changeMenuDataScope(Long menuId);
}
