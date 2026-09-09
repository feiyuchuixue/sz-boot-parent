package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysLoginLog;
import com.sz.core.common.entity.PageResult;
import com.sz.admin.system.pojo.dto.SysLoginLogListDTO;
import com.sz.admin.system.pojo.vo.SysLoginLogVO;

/**
 * <p>
 * 登陆日志表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    PageResult<SysLoginLogVO> page(SysLoginLogListDTO dto);

    void recordLoginLog(String userName, String status, String msg);
}