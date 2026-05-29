package com.sz.audit.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.audit.mapper.SysOperationLogDetailMapper;
import com.sz.audit.mapper.SysOperationLogMapper;
import com.sz.audit.pojo.dto.SysOperationLogListDTO;
import com.sz.audit.pojo.po.SysOperationLog;
import com.sz.audit.pojo.po.SysOperationLogDetail;
import com.sz.audit.pojo.vo.SysOperationLogDetailVO;
import com.sz.audit.pojo.vo.SysOperationLogSummaryVO;
import com.sz.audit.pojo.vo.SysOperationLogVO;
import com.sz.audit.service.SysOperationLogService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.logger.AuditProperties;
import com.sz.logger.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * 操作审计日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    private final SysOperationLogDetailMapper detailMapper;

    private final AuditProperties auditProperties;

    @Override
    public Long saveMainLog(AuditEvent event) {
        SysOperationLog log = new SysOperationLog();
        log.setEventId(event.getEventId());
        log.setTraceId(event.getTraceId());
        log.setUserId(parseUserId(event.getUserId()));
        log.setUserName(event.getUserName());
        log.setModuleName(event.getModuleName());
        log.setOperationName(event.getOperationName());
        log.setOperationType(event.getOperationType());
        log.setPermissionCode(event.getPermissionCode());
        log.setRequestMethod(event.getRequestMethod());
        log.setRequestUri(event.getRequestUri());
        log.setBusinessId(event.getBusinessId());
        log.setIpAddress(event.getIpAddress());
        log.setOperationTime(event.getOccurredAt());
        log.setCostMs(event.getCostMs());
        log.setSlowFlag(Boolean.TRUE.equals(event.getSlow()) ? "T" : "F");
        log.setStatus(event.getStatus());
        log.setResponseCode(event.getResponseCode());
        log.setResponseMessage(event.getResponseMessage());
        log.setErrorType(event.getErrorType());
        log.setErrorMessage(event.getErrorMessage());
        save(log);
        event.setOperationLogId(log.getId());
        return log.getId();
    }

    @Override
    public void saveDiagnosticDetail(AuditEvent event) {
        if (event.getOperationLogId() == null) {
            return;
        }
        if (!shouldSaveDetail(event)) {
            return;
        }
        SysOperationLogDetail detail = new SysOperationLogDetail();
        detail.setOperationLogId(event.getOperationLogId());
        detail.setEventId(event.getEventId());
        detail.setTraceId(event.getTraceId());
        detail.setDetailType(resolveDetailType(event));
        detail.setRequestParams(event.getRequestParams());
        detail.setResponseBody(event.getResponseBody());
        detail.setExceptionStack(event.getExceptionStack());
        detail.setCreateTime(LocalDateTime.now());
        detailMapper.insert(detail);
    }

    @Override
    public PageResult<SysOperationLogVO> page(SysOperationLogListDTO dto) {
        Page<SysOperationLogVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysOperationLogVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public SysOperationLogSummaryVO summary(SysOperationLogListDTO dto) {
        long totalCount = countBy(dto, ignored -> {
        });
        long failCount = countBy(dto, item -> item.setStatus("FAIL"));
        long slowCount = countBy(dto, item -> item.setSlowFlag("T"));
        BigDecimal successRate = totalCount == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(totalCount - failCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        return new SysOperationLogSummaryVO().setTotalCount(totalCount).setFailCount(failCount).setSlowCount(slowCount).setExceptionCount(failCount)
                .setSuccessRate(successRate);
    }

    @Override
    public SysOperationLogVO detail(Long id) {
        SysOperationLog log = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(log);
        SysOperationLogVO vo = BeanCopyUtils.copy(log, SysOperationLogVO.class);
        QueryWrapper detailWrapper = QueryWrapper.create().eq(SysOperationLogDetail::getOperationLogId, id).orderBy(SysOperationLogDetail::getCreateTime)
                .desc();
        List<SysOperationLogDetailVO> details = detailMapper.selectListByQueryAs(detailWrapper, SysOperationLogDetailVO.class);
        vo.setDetails(details);
        return vo;
    }

    private QueryWrapper buildQueryWrapper(SysOperationLogListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysOperationLog.class);
        if (Utils.isNotNull(dto.getTraceId())) {
            wrapper.eq(SysOperationLog::getTraceId, dto.getTraceId());
        }
        if (Utils.isNotNull(dto.getUserId())) {
            wrapper.eq(SysOperationLog::getUserId, dto.getUserId());
        }
        if (Utils.isNotNull(dto.getUserName())) {
            wrapper.like(SysOperationLog::getUserName, dto.getUserName());
        }
        if (Utils.isNotNull(dto.getModuleName())) {
            wrapper.like(SysOperationLog::getModuleName, dto.getModuleName());
        }
        if (Utils.isNotNull(dto.getOperationName())) {
            wrapper.like(SysOperationLog::getOperationName, dto.getOperationName());
        }
        if (Utils.isNotNull(dto.getOperationType())) {
            wrapper.eq(SysOperationLog::getOperationType, dto.getOperationType());
        }
        if (Utils.isNotNull(dto.getStatus())) {
            wrapper.eq(SysOperationLog::getStatus, dto.getStatus());
        }
        if (Utils.isNotNull(dto.getRequestMethod())) {
            wrapper.eq(SysOperationLog::getRequestMethod, dto.getRequestMethod());
        }
        if (Utils.isNotNull(dto.getRequestUri())) {
            wrapper.like(SysOperationLog::getRequestUri, dto.getRequestUri());
        }
        if (Utils.isNotNull(dto.getBusinessId())) {
            wrapper.like(SysOperationLog::getBusinessId, dto.getBusinessId());
        }
        if (Utils.isNotNull(dto.getMinCostMs())) {
            wrapper.ge(SysOperationLog::getCostMs, dto.getMinCostMs());
        }
        if (Utils.isNotNull(dto.getSlowFlag())) {
            wrapper.eq(SysOperationLog::getSlowFlag, dto.getSlowFlag());
        }
        if (Utils.isNotNull(dto.getOperationTimeStart()) && Utils.isNotNull(dto.getOperationTimeEnd())) {
            wrapper.between(SysOperationLog::getOperationTime, dto.getOperationTimeStart(), dto.getOperationTimeEnd());
        }
        wrapper.orderBy(SysOperationLog::getOperationTime).desc();
        return wrapper;
    }

    private long countBy(SysOperationLogListDTO source, Consumer<SysOperationLogListDTO> customizer) {
        SysOperationLogListDTO dto = BeanCopyUtils.copy(source, SysOperationLogListDTO.class);
        customizer.accept(dto);
        return count(buildQueryWrapper(dto));
    }

    private boolean shouldSaveDetail(AuditEvent event) {
        AuditProperties.Diagnostic diagnostic = auditProperties.resolveDiagnostic();
        if (!diagnostic.isEnabled()) {
            return false;
        }
        if (Boolean.TRUE.equals(event.getSlow()) && diagnostic.isPerformanceEnabled()) {
            return true;
        }
        if ("FAIL".equals(event.getStatus()) && diagnostic.isExceptionEnabled()) {
            return true;
        }
        return Utils.isNotNull(event.getRequestParams()) || Utils.isNotNull(event.getResponseBody()) || Utils.isNotNull(event.getExceptionStack());
    }

    private String resolveDetailType(AuditEvent event) {
        if ("FAIL".equals(event.getStatus())) {
            return "EXCEPTION";
        }
        if (Boolean.TRUE.equals(event.getSlow())) {
            return "PERFORMANCE";
        }
        return "DETAIL";
    }

    private Long parseUserId(String userId) {
        if (!Utils.isNotNull(userId)) {
            return null;
        }
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
