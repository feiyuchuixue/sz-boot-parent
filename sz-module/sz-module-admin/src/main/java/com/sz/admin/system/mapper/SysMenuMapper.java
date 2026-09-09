package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统菜单表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2023-08-21
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<Long> getMenuAndChildrenIds(@Param("menuId") Long menuId, @Param("isShowButton") boolean isShowButton);

    /**
     * 删除自己及子节点
     */
    void removeTree(@Param("nodeId") Long nodeId);

    /**
     * @param ids
     *            ids
     */
    void updateMenuAndChildrenIsDelete(List<Long> ids);

    /**
     * @param ids
     *            ids
     * @return 递归下边的子节点id集合
     */
    List<Long> selectMenuAndChildrenIds(List<Long> ids);

}
