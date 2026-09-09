package com.sz.excel.core;

import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;

/**
 * 冻结首行 Handler。
 * <p>
 * 注册到 ExcelWriter 后，在 Sheet 创建完成时调用 {@code createFreezePane(0, 1)}，
 * 使表头行始终可见，方便用户在填写大量数据时对照表头。
 * </p>
 *
 * <h3>使用示例</h3>
 * 
 * <pre>{@code
 * FastExcelFactory.write(os, clazz).registerWriteHandler(new FreezeHeaderHandler()).sheet("导入模板").doWrite(Collections.emptyList());
 * }</pre>
 *
 * @author sz
 * @since 2026/03/25
 */
public class FreezeHeaderHandler implements SheetWriteHandler {

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 冻结首行：colSplit=0（不冻结列），rowSplit=1（冻结第 1 行）
        writeSheetHolder.getSheet().createFreezePane(0, 1);
    }

}
