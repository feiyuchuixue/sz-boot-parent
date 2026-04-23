package com.sz.excel.imports.template;

import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.core.util.JsonUtils;
import com.sz.excel.core.ExcelFailRow;
import com.sz.excel.core.ExcelResult;
import com.sz.excel.imports.model.ExcelImportBizResult;
import com.sz.excel.imports.model.ExcelImportFailItem;
import com.sz.excel.imports.model.ExcelImportParseResult;
import com.sz.excel.imports.model.ExcelImportResultVO;
import com.sz.excel.imports.spi.ImportBatchContext;
import com.sz.excel.imports.spi.ImportBatchTracker;
import com.sz.excel.imports.spi.ImportFailRecordWriter;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Excel 导入模板方法。
 * <p>
 * 职责：
 * <ol>
 * <li>解析 Excel → {@link ExcelImportParseResult}</li>
 * <li>通过 {@link ImportBatchTracker} 创建批次 / 更新状态</li>
 * <li>调用子类 {@link #doImport(String, List)} 执行业务写入（支持可选分片
 * {@link #chunkSize()}）</li>
 * <li>合并 Excel 解析失败项 + 业务失败项，通过 {@link ImportFailRecordWriter} 落库</li>
 * <li>完成/异常状态流转，返回 {@link ExcelImportResultVO}</li>
 * </ol>
 * <p>
 * 生命周期钩子：{@link #beforeImport} / {@link #afterChunk} / {@link #afterImport} /
 * {@link #onFailBatch}。
 */
@Slf4j
public abstract class AbstractExcelImportTemplate<T> {

    protected final ImportBatchTracker batchTracker;

    protected final ImportFailRecordWriter failRecordWriter;

    protected AbstractExcelImportTemplate(ImportBatchTracker batchTracker, ImportFailRecordWriter failRecordWriter) {
        this.batchTracker = batchTracker;
        this.failRecordWriter = failRecordWriter;
    }

    public final ExcelImportResultVO execute(ImportExcelDTO dto) {
        ExcelImportParseResult<T> parseResult = parseImportExcel(dto);
        if (parseResult.isEmpty()) {
            return new ExcelImportResultVO("", 0, 0);
        }

        String fileName = (dto.getFile() != null) ? dto.getFile().getOriginalFilename() : null;
        List<T> successRows = parseResult.safeSuccessRows();

        log.info("[ExcelImport] start bizType={}, total={}, chunkSize={}", bizType(), parseResult.totalCount(), chunkSize());
        ImportBatchContext ctx = batchTracker.createBatch(bizType(), bizName(), fileName, currentOperatorId(), parseResult.totalCount());
        log.info("[ExcelImport] batch created, batchId={}", ctx.getBatchId());

        try {
            beforeImport(ctx.getBatchId(), successRows);

            ExcelImportBizResult aggregated = runImport(ctx.getBatchId(), successRows);

            List<ExcelImportFailItem> allFailItems = mergeFailItems(parseResult.safeFailItems(), aggregated.safeFailItems());
            failRecordWriter.saveFailRecords(ctx.getBatchId(), bizType(), allFailItems);

            batchTracker.finishBatch(ctx, aggregated.getSuccessCount(), allFailItems.size());
            afterImport(ctx.getBatchId(), aggregated);

            log.info("[ExcelImport] finished, batchId={}, success={}, fail={}", ctx.getBatchId(), aggregated.getSuccessCount(), allFailItems.size());
            ExcelImportResultVO resultVO = batchTracker.buildResult(ctx.getBatchId(), aggregated.getSuccessCount(), allFailItems.size());
            resultVO.setFailDetails(allFailItems.isEmpty() ? null : allFailItems);
            return resultVO;
        } catch (Exception e) {
            log.error("[ExcelImport] batch failed, batchId={}, reason={}", ctx.getBatchId(), e.getMessage(), e);
            batchTracker.failBatch(ctx, e.getMessage());
            onFailBatch(ctx.getBatchId(), e);
            throw e;
        }
    }

    // ===================== 必须实现 =====================

    protected abstract Class<T> importDtoClass();

    protected abstract String bizType();

    protected abstract String bizName();

    /**
     * 执行业务导入（单个分片或全量）。
     * <p>
     * <strong>行号约定</strong>：返回的 {@link ExcelImportBizResult#getFailItems()} 中每个
     * {@link ExcelImportFailItem#getRowNo()} 应为"块内下标"（1..rows.size()）。 分片场景下，框架层
     * {@link #runImport} 会自动叠加 rowOffset（= chunkIndex *
     * chunkSize）将其还原为"成功解析行的全局下标"（1..N）。 不分片场景（chunkSize=0）下 rowOffset 恒为
     * 0，块内下标即全局下标。
     *
     * @param batchId
     *            批次 ID
     * @param rows
     *            当前块的数据行（成功解析的业务 DTO）
     * @return 当前块的业务导入结果；failItems.rowNo 必须为块内下标（1..rows.size()）
     */
    protected abstract ExcelImportBizResult doImport(String batchId, List<T> rows);

    protected abstract List<ExcelImportFailItem> convertExcelFailItems(List<ExcelFailRow<T>> failRows);

    // ===================== 可选钩子 =====================

    /**
     * 分片大小。返回 chunkSize = 0 表示不分片，一次性调用 {@link #doImport(String, List)}。
     */
    protected int chunkSize() {
        return 0;
    }

    protected void beforeImport(String batchId, List<T> rows) {
    }

    protected void afterChunk(String batchId, int chunkIndex, int chunkTotal, ExcelImportBizResult chunkResult) {
    }

    protected void afterImport(String batchId, ExcelImportBizResult aggregated) {
    }

    protected void onFailBatch(String batchId, Throwable ex) {
    }

    /**
     * 当前操作人 ID。框架层不依赖任何认证组件，默认返回 null； 业务子类可根据所在模块自行覆盖（例如在 admin 模块使用 StpUtil）。
     */
    protected Long currentOperatorId() {
        return null;
    }

    // ===================== 内部工具 =====================

    @SneakyThrows
    protected ExcelImportParseResult<T> parseImportExcel(ImportExcelDTO dto) {
        ExcelResult<T> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), importDtoClass(), true);
        List<ExcelImportFailItem> excelFailItems = convertExcelFailItems(excelResult.getFailRowList());
        return new ExcelImportParseResult<>(excelResult.getList(), excelFailItems);
    }

    private ExcelImportBizResult runImport(String batchId, List<T> successRows) {
        int size = chunkSize();
        if (size <= 0 || successRows.size() <= size) {
            return safeResult(doImport(batchId, successRows));
        }

        List<List<T>> chunks = partition(successRows, size);
        int totalSuccess = 0;
        List<ExcelImportFailItem> allFailItems = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            ExcelImportBizResult chunkResult = safeResult(doImport(batchId, chunks.get(i)));
            // 框架层自动修正块内 rowNo → 全局 rowNo（成功解析行的全局下标 1..N）
            // 约定：子类 doImport 返回的 failItems.rowNo 为块内下标（1..chunkSize）
            // 不分片（chunkSize=0）时不会进入此分支，行为不变
            int rowOffset = i * size;
            if (rowOffset > 0) {
                for (ExcelImportFailItem failItem : chunkResult.safeFailItems()) {
                    if (failItem.getRowNo() != null) {
                        failItem.setRowNo(failItem.getRowNo() + rowOffset);
                    }
                }
            }
            totalSuccess += chunkResult.getSuccessCount();
            allFailItems.addAll(chunkResult.safeFailItems());
            log.info("[ExcelImport] chunk {}/{} done, success={}, fail={}, batchId={}", i + 1, chunks.size(), chunkResult.getSuccessCount(),
                    chunkResult.safeFailItems().size(), batchId);
            afterChunk(batchId, i, chunks.size(), chunkResult);
        }
        return new ExcelImportBizResult(totalSuccess, allFailItems);
    }

    private static ExcelImportBizResult safeResult(ExcelImportBizResult r) {
        return r == null ? ExcelImportBizResult.empty() : r;
    }

    private static List<ExcelImportFailItem> mergeFailItems(List<ExcelImportFailItem> excelFailItems, List<ExcelImportFailItem> bizFailItems) {
        List<ExcelImportFailItem> all = new ArrayList<>();
        if (excelFailItems != null && !excelFailItems.isEmpty()) {
            all.addAll(excelFailItems);
        }
        if (bizFailItems != null && !bizFailItems.isEmpty()) {
            all.addAll(bizFailItems);
        }
        return all;
    }

    private static <E> List<List<E>> partition(List<E> list, int size) {
        List<List<E>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }

    /**
     * 将单个 Excel 解析失败行转换为 {@link ExcelImportFailItem}。
     * <p>
     * 子类在实现 {@link #convertExcelFailItems} 时可直接调用此方法，无需重复手写转换样板。 rowData 由 DTO 对象自动
     * JSON 序列化得到，不需要手工枚举字段。
     *
     * @param failRow
     *            Excel 解析失败行
     * @param bizKeyFn
     *            从 DTO 提取业务主识别值的函数（DTO 为 null 时自动返回 null）
     * @param bizKeyLabel
     *            业务主识别值的可读标签（如"用户名"、"provider"）
     * @param <T>
     *            导入 DTO 类型
     * @return 填充好的 {@link ExcelImportFailItem}
     */
    protected static <T> ExcelImportFailItem buildExcelFailItem(ExcelFailRow<T> failRow, Function<T, String> bizKeyFn, String bizKeyLabel) {
        T rowData = failRow.getRowData();
        ExcelImportFailItem item = new ExcelImportFailItem();
        item.setRowNo(failRow.getRowNo());
        item.setErrorMsg(failRow.getErrorMsg());
        item.setBizKeyLabel(bizKeyLabel);
        if (rowData != null) {
            item.setBizKey(bizKeyFn.apply(rowData));
            item.setRowData(JsonUtils.jsonToMap(JsonUtils.toJsonString(rowData)));
        }
        return item;
    }
}
