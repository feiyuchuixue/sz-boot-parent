package ${excelImporterPkg};

import com.sz.admin.system.service.SysImportBatchService;
import com.sz.admin.system.service.SysImportFailRecordService;
import ${mapperPkg}.${mapperClassName};
import ${dtoPkg}.${dtoImportClassName};
import ${poPkg}.${poClassName};
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
 * <p>
 * ${tableComment} Excel 导入实现。
 * 直接通过 mapper 本地批量入库，chunkSize=0 表示整批一次提交。
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Component
public class ${excelImporterClassName} extends AbstractExcelImportTemplate<${dtoImportClassName}> {

    private final ${mapperClassName} mapper;

    public ${excelImporterClassName}(${mapperClassName} mapper,
            SysImportBatchService sysImportBatchService,
            SysImportFailRecordService sysImportFailRecordService) {
        super(sysImportBatchService, sysImportFailRecordService);
        this.mapper = mapper;
    }

    /**
     * 对外入口：供 ServiceImpl 委托调用。
     */
    public ExcelImportResultVO importExcel(ImportExcelDTO dto) {
        return execute(dto);
    }

    @Override
    protected Class<${dtoImportClassName}> importDtoClass() {
        return ${dtoImportClassName}.class;
    }

    @Override
    protected String bizType() {
        return "${tableName}";
    }

    @Override
    protected String bizName() {
        return "${functionName}导入";
    }

    @Override
    protected ExcelImportBizResult doImport(String batchId, List<${dtoImportClassName}> rows) {
        if (rows == null || rows.isEmpty()) {
            return ExcelImportBizResult.empty();
        }
        List<${poClassName}> successList = new ArrayList<>();
        List<ExcelImportFailItem> failDetails = new ArrayList<>();
        for (${dtoImportClassName} dto : rows) {
            ${poClassName} po = BeanCopyUtils.copy(dto, ${poClassName}.class);
            successList.add(po);
        }
        mapper.insertBatch(successList);
        return ExcelImportBizResult.of(successList.size(), failDetails);
    }

    @Override
    protected List<ExcelImportFailItem> convertExcelFailItems(
            List<ExcelFailRow<${dtoImportClassName}>> failRows) {
        if (failRows == null || failRows.isEmpty()) {
            return new ArrayList<>();
        }
<#if pkColumns?has_content>
        return failRows.stream()
                .map(row -> buildExcelFailItem(row, ${dtoImportClassName}::get${pkColumns[0].upCamelField}, "${pkColumns[0].columnComment}"))
                .toList();
<#else>
        return failRows.stream()
                .map(row -> buildExcelFailItem(row, dto -> null, "主键"))
                .toList();
</#if>
    }
}
