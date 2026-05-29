package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysLoginLog;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.system.pojo.dto.SysLoginLogCreateDTO;
import com.sz.admin.system.pojo.dto.SysLoginLogUpdateDTO;
import com.sz.admin.system.pojo.dto.SysLoginLogListDTO;
import com.sz.admin.system.pojo.vo.SysLoginLogVO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 登陆日志表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    void create(SysLoginLogCreateDTO dto);

    void update(SysLoginLogUpdateDTO dto);

    PageResult<SysLoginLogVO> page(SysLoginLogListDTO dto);

    List<SysLoginLogVO> list(SysLoginLogListDTO dto);

    void remove(SelectIdsDTO dto);

    SysLoginLogVO detail(Object id);

    void exportExcel(SysLoginLogListDTO dto, HttpServletResponse response);

    void recordLoginLog(String userName, String status, String msg);
}