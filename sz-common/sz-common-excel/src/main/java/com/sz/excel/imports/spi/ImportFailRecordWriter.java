package com.sz.excel.imports.spi;

import com.sz.excel.imports.model.ExcelImportFailItem;

import java.util.List;

/**
 * 导入失败记录写入 SPI。
 * <p>
 * 由业务底座（如 admin 侧的 SysImportFailRecordService）实现。
 */
public interface ImportFailRecordWriter {

    void saveFailRecords(String batchId, String bizType, List<ExcelImportFailItem> failItems);
}
