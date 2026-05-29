package com.sz.admin.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysImportFailRecordMapper;
import com.sz.admin.system.pojo.po.SysImportFailRecord;
import com.sz.admin.system.service.SysImportFailRecordService;
import com.sz.excel.imports.model.ExcelImportFailItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysImportFailRecordServiceImpl extends ServiceImpl<SysImportFailRecordMapper, SysImportFailRecord> implements SysImportFailRecordService {

    @Override
    public void saveFailRecords(String batchId, String bizType, List<ExcelImportFailItem> failItems) {
        if (failItems == null || failItems.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<SysImportFailRecord> records = new ArrayList<>(failItems.size());
        for (ExcelImportFailItem item : failItems) {
            SysImportFailRecord record = new SysImportFailRecord();
            record.setBatchId(batchId);
            record.setBizType(bizType);
            record.setRowNo(item.getRowNo());
            record.setBizKey(item.getBizKey());
            record.setBizKeyLabel(item.getBizKeyLabel());
            record.setErrorCode(item.getErrorCode());
            record.setErrorMsg(item.getErrorMsg());
            record.setHandleStatus("PENDING");
            record.setRowData(item.getRowData());
            record.setCreateTime(now);
            record.setUpdateTime(now);
            records.add(record);
        }
        saveBatch(records);
    }
}
