package com.sz.excel.imports.spi;

/**
 * 导入批次上下文。
 * <p>
 * 框架层最小契约：只需暴露 batchId。具体实现（例如 admin 的 SysImportBatch PO）
 * 可以在接口之上携带任意额外字段，实现方在自己的 Tracker 方法内部做向下转型即可。
 */
public interface ImportBatchContext {

    String getBatchId();
}
