package com.sz.ssoadmin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.ssoadmin.system.pojo.dto.sysconfig.SysConfigCreateDTO;
import com.sz.ssoadmin.system.pojo.dto.sysconfig.SysConfigListDTO;
import com.sz.ssoadmin.system.pojo.dto.sysconfig.SysConfigUpdateDTO;
import com.sz.ssoadmin.system.pojo.po.SysConfig;
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

    void create(SysConfigCreateDTO dto);

    void update(SysConfigUpdateDTO dto);

    PageResult<SysConfig> list(SysConfigListDTO queryDTO);

    void remove(SelectIdsDTO dto);

    SysConfig detail(Object id);
}
