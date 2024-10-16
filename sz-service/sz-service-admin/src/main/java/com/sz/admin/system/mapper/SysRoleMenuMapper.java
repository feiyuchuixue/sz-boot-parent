package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统角色-菜单表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2023-08-21
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    void insertBatchSysRoleMenu(@Param("menuIds") List<String> menuIds, @Param("roleId") Long roleId);

}
