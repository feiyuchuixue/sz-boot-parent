package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.po.SysImportFailRecord;
import com.sz.excel.imports.spi.ImportFailRecordWriter;

public interface SysImportFailRecordService extends IService<SysImportFailRecord>, ImportFailRecordWriter {

}
