package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigAddDTO;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigQueryDTO;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigUpDTO;
import com.sz.admin.system.pojo.po.SysConfig;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

/**
 * <p>
 * 参数配置表 服务类
 * </p>
 *
 * @author sz
 * @since 2023-11-23
 */
public interface SysConfigService extends IService<SysConfig> {

    void create(SysConfigAddDTO dto);

    void update(SysConfigUpDTO dto);

    PageResult<SysConfig> list(SysConfigQueryDTO queryDTO);

    void remove(SelectIdsDTO dto);

    SysConfig detail(Object id);
}
