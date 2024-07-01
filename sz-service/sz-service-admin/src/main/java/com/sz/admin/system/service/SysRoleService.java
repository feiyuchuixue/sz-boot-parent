package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleCreateDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleListDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleUpdateDTO;
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

    void create(SysRoleCreateDTO dto);

    void update(SysRoleUpdateDTO dto);

    void remove(SelectIdsDTO dto);

    void removeByMenuId(SelectIdsDTO dto);

    PageResult<SysRole> list(SysRoleListDTO dto);
}
