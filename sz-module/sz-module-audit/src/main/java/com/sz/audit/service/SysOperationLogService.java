package com.sz.audit.service;

import com.mybatisflex.core.service.IService;
import com.sz.audit.pojo.dto.SysOperationLogListDTO;
import com.sz.audit.pojo.po.SysOperationLog;
import com.sz.audit.pojo.vo.SysOperationLogSummaryVO;
import com.sz.audit.pojo.vo.SysOperationLogVO;
import com.sz.core.common.entity.PageResult;
import com.sz.logger.event.AuditEvent;

/**
 * 操作审计日志服务。
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    Long saveMainLog(AuditEvent event);

    void saveDiagnosticDetail(AuditEvent event);

    PageResult<SysOperationLogVO> page(SysOperationLogListDTO dto);

    SysOperationLogSummaryVO summary(SysOperationLogListDTO dto);

    SysOperationLogVO detail(Long id);
}
