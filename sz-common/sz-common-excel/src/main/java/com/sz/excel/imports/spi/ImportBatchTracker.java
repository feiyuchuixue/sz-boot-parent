package com.sz.excel.imports.spi;

import com.sz.excel.imports.model.ExcelImportResultVO;

/**
 * 导入批次跟踪 SPI。
 * <p>
 * 由业务底座（如 admin 侧的 SysImportBatchService）实现，负责：
 * <ul>
 * <li>分配 batchId 并落库为"处理中"状态</li>
 * <li>成功/失败/异常状态流转</li>
 * <li>构造对前端的结果 VO</li>
 * </ul>
 */
public interface ImportBatchTracker {

    ImportBatchContext createBatch(String bizType, String bizName, String fileName, Long operatorId, int totalCount);

    void finishBatch(ImportBatchContext ctx, int successCount, int failCount);

    void failBatch(ImportBatchContext ctx, String remark);

    ExcelImportResultVO buildResult(String batchId, int successCount, int failCount);
}
