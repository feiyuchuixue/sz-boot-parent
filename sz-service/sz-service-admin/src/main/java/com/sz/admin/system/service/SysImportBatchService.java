package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysImportBatch;
import com.sz.excel.imports.spi.ImportBatchTracker;

public interface SysImportBatchService extends IService<SysImportBatch>, ImportBatchTracker {

}
