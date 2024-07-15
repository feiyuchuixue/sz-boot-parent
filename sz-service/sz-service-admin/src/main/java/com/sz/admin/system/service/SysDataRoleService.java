package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysDataRole;
import com.sz.admin.system.pojo.vo.sysdatarole.SysDataRoleMenuVO;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;

import com.sz.admin.system.pojo.dto.sysdatarole.SysDataRoleCreateDTO;
import com.sz.admin.system.pojo.dto.sysdatarole.SysDataRoleUpdateDTO;
import com.sz.admin.system.pojo.dto.sysdatarole.SysDataRoleListDTO;
import com.sz.admin.system.pojo.vo.sysdatarole.SysDataRoleVO;

/**
 * <p>
 * 数据权限管理 Service
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-09
 */
public interface SysDataRoleService extends IService<SysDataRole> {

    void create(SysDataRoleCreateDTO dto);

    void update(SysDataRoleUpdateDTO dto);

    PageResult<SysDataRoleVO> page(SysDataRoleListDTO dto);

    List<SysDataRoleVO> list(SysDataRoleListDTO dto);

    void remove(SelectIdsDTO dto);

    SysDataRoleVO detail(Object id);

    SysDataRoleMenuVO queryDataRoleMenu();
}
