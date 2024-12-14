package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.sz.admin.system.pojo.po.SysTempFileHistory;
import com.sz.core.common.entity.PageResult;

/**
 * <p>
 * 模版文件历史 Service
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
public interface SysTempFileHistoryService extends IService<SysTempFileHistory> {

    void create(SysTempFileHistoryCreateDTO dto);

    PageResult<SysTempFileHistory> historyList(SysTempFileHistoryListDTO dto);
}