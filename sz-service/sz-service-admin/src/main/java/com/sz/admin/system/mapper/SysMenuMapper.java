package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

;

/**
 * <p>
 * 系统菜单表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2023-08-21
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<String> getMenuAndChildrenIds(@Param("menuId") String menuId, @Param("isShowButton") boolean isShowButton);

    /**
     * 更新has_children
     */
    void syncTreeHasChildren();

    /**
     * 更新deep
     */
    void syncTreeDeep();

    /**
     * 删除自己及子节点
     */
    void removeTree(@Param("nodeId") String nodeId);

    /**
     * @param ids
     */
    void updateMenuAndChildrenIsDelete(List<String> ids);

    /**
     * @param ids
     * @return
     */
    List<String> selectMenuAndChildrenIds(List<String> ids);

}
