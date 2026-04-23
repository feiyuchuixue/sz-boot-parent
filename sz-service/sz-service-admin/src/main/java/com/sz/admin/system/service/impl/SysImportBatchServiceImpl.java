package com.sz.admin.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysImportBatchMapper;
import com.sz.admin.system.pojo.po.SysImportBatch;
import com.sz.admin.system.service.SysImportBatchService;
import com.sz.excel.imports.model.ExcelImportResultVO;
import com.sz.excel.imports.spi.ImportBatchContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SysImportBatchServiceImpl extends ServiceImpl<SysImportBatchMapper, SysImportBatch> implements SysImportBatchService {

    @Override
    public SysImportBatch createBatch(String bizType, String bizName, String fileName, Long operatorId, int totalCount) {
        SysImportBatch importBatch = new SysImportBatch();
        importBatch.setBatchId(UUID.randomUUID().toString());
        importBatch.setBizType(bizType);
        importBatch.setBizName(bizName);
        importBatch.setFileName(fileName);
        importBatch.setOperatorId(operatorId);
        importBatch.setTotalCount(totalCount);
        importBatch.setSuccessCount(0);
        importBatch.setFailCount(0);
        importBatch.setStatus("PROCESSING");
        importBatch.setCreateTime(LocalDateTime.now());
        save(importBatch);
        return importBatch;
    }

    @Override
    public void finishBatch(ImportBatchContext ctx, int successCount, int failCount) {
        SysImportBatch importBatch = (SysImportBatch) ctx;
        importBatch.setSuccessCount(successCount);
        importBatch.setFailCount(failCount);
        importBatch.setStatus("FINISHED");
        importBatch.setFinishTime(LocalDateTime.now());
        updateById(importBatch);
    }

    @Override
    public void failBatch(ImportBatchContext ctx, String remark) {
        SysImportBatch importBatch = (SysImportBatch) ctx;
        importBatch.setStatus("FAILED");
        importBatch.setRemark(remark);
        importBatch.setFinishTime(LocalDateTime.now());
        updateById(importBatch);
    }

    @Override
    public ExcelImportResultVO buildResult(String batchId, int successCount, int failCount) {
        return new ExcelImportResultVO(batchId, successCount, failCount);
    }
}
