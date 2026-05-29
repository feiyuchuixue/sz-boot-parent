package com.sz.admin.teacher.service.support;

import com.sz.admin.system.service.SysImportBatchService;
import com.sz.admin.system.service.SysImportFailRecordService;
import com.sz.admin.teacher.mapper.TeacherStatisticsMapper;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsImportDTO;
import com.sz.admin.teacher.pojo.po.TeacherStatistics;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.JsonUtils;
import com.sz.excel.core.ExcelFailRow;
import com.sz.excel.imports.model.ExcelImportBizResult;
import com.sz.excel.imports.model.ExcelImportFailItem;
import com.sz.excel.imports.model.ExcelImportResultVO;
import com.sz.excel.imports.template.AbstractExcelImportTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 教师统计 Excel 导入实现。
 * <p>
 * 直接通过 mapper 本地批量入库，不涉及跨进程调用，也不需要分片（chunkSize=0 表示整批一次提交）。
 */
@Component
public class TeacherStatisticsExcelImporter extends AbstractExcelImportTemplate<TeacherStatisticsImportDTO> {

    private final TeacherStatisticsMapper mapper;

    public TeacherStatisticsExcelImporter(TeacherStatisticsMapper mapper, SysImportBatchService sysImportBatchService,
            SysImportFailRecordService sysImportFailRecordService) {
        super(sysImportBatchService, sysImportFailRecordService);
        this.mapper = mapper;
    }

    /**
     * 对外入口：保持原签名。
     */
    public ExcelImportResultVO importExcel(ImportExcelDTO dto) {
        return execute(dto);
    }

    @Override
    protected Class<TeacherStatisticsImportDTO> importDtoClass() {
        return TeacherStatisticsImportDTO.class;
    }

    @Override
    protected String bizType() {
        return "teacher_statistics";
    }

    @Override
    protected String bizName() {
        return "教师统计导入";
    }

    @Override
    protected ExcelImportBizResult doImport(String batchId, List<TeacherStatisticsImportDTO> rows) {
        if (rows == null || rows.isEmpty()) {
            return ExcelImportBizResult.empty();
        }

        List<TeacherStatistics> successList = new ArrayList<>();
        List<ExcelImportFailItem> failDetails = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            TeacherStatisticsImportDTO dto = rows.get(i);
            int rowIndex = i + 1;
            // 模拟错误
            if (rows.size() > 5 && (i == 2 || i == 4)) {
                failDetails.add(buildFailDetail(rowIndex, dto, "错误编号xxx" + i));
                continue;
            }
            TeacherStatistics statistics = BeanCopyUtils.copy(dto, TeacherStatistics.class);
            successList.add(statistics);
        }
        mapper.insertBatch(successList);
        return ExcelImportBizResult.of(successList.size(), failDetails);
    }

    @Override
    protected List<ExcelImportFailItem> convertExcelFailItems(List<ExcelFailRow<TeacherStatisticsImportDTO>> failRows) {
        if (failRows == null || failRows.isEmpty()) {
            return new ArrayList<>();
        }
        return failRows.stream().map(row -> buildExcelFailItem(row, TeacherStatisticsImportDTO::getTeacherId, "教师ID")).toList();
    }

    private ExcelImportFailItem buildFailDetail(int rowIndex, TeacherStatisticsImportDTO dto, String errorMsg) {
        return new ExcelImportFailItem(rowIndex, dto.getTeacherId(), "教师ID", null, errorMsg, JsonUtils.jsonToMap(JsonUtils.toJsonString(dto)));
    }

}
