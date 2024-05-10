package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleAddDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleQueryDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleUpDTO;
import com.sz.admin.system.pojo.po.SysRole;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
public interface SysRoleService extends IService<SysRole> {

    void create(SysRoleAddDTO dto);

    void update(SysRoleUpDTO dto);

    void remove(SelectIdsDTO dto);

    void removeByMenuId(SelectIdsDTO dto);

    PageResult<SysRole> list(SysRoleQueryDTO dto);
}
