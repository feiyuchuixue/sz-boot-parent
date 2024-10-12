package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysclient.SysClientCreateDTO;
import com.sz.admin.system.pojo.dto.sysclient.SysClientListDTO;
import com.sz.admin.system.pojo.dto.sysclient.SysClientUpdateDTO;
import com.sz.admin.system.pojo.po.SysClient;
import com.sz.admin.system.pojo.vo.sysclient.SysClientVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.security.pojo.ClientVO;

import java.util.List;

/**
 * <p>
 * 系统授权表 Service
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
public interface SysClientService extends IService<SysClient> {

    void create(SysClientCreateDTO dto);

    void update(SysClientUpdateDTO dto);

    PageResult<SysClientVO> page(SysClientListDTO dto);

    List<SysClientVO> list(SysClientListDTO dto);

    void remove(SelectIdsDTO dto);

    ClientVO detail(Object id);

}