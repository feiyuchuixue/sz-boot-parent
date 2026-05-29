package com.sz.excel.imports.template;

import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelFailRow;
import com.sz.excel.imports.model.ExcelImportBizResult;
import com.sz.excel.imports.model.ExcelImportFailItem;
import com.sz.excel.imports.model.ExcelImportParseResult;
import com.sz.excel.imports.model.ExcelImportResultVO;
import com.sz.excel.imports.spi.ImportBatchContext;
import com.sz.excel.imports.spi.ImportBatchTracker;
import com.sz.excel.imports.spi.ImportFailRecordWriter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractExcelImportTemplateTest {

    @Test
    void executeCreatesBatchChunksRowsMergesFailuresAndBuildsResult() {
        FakeBatchTracker tracker = new FakeBatchTracker();
        FakeFailRecordWriter writer = new FakeFailRecordWriter();
        FakeImporter importer = new FakeImporter(tracker, writer);
        importer.chunkSize = 2;
        importer.parseResult = new ExcelImportParseResult<>(List.of(new RowDTO("A"), new RowDTO("B"), new RowDTO("C")),
                List.of(fail(88, "excel-format", "Excel格式错误")));
        importer.chunkResults.add(ExcelImportBizResult.of(1, List.of(fail(2, "B", "名称重复"))));
        importer.chunkResults.add(ExcelImportBizResult.of(1, List.of(fail(1, "C", "状态无效"))));

        ExcelImportResultVO result = importer.execute(new ImportExcelDTO());

        assertThat(tracker.created).isTrue();
        assertThat(tracker.createdBizType).isEqualTo("teacher");
        assertThat(tracker.createdBizName).isEqualTo("教师导入");
        assertThat(tracker.createdTotalCount).isEqualTo(4);
        assertThat(importer.beforeImportRows).extracting(RowDTO::code).containsExactly("A", "B", "C");
        assertThat(importer.importedChunks).extracting(List::size).containsExactly(2, 1);
        assertThat(importer.afterChunkEvents).containsExactly("0/2:1", "1/2:1");
        assertThat(importer.afterImportSuccess).isEqualTo(2);

        assertThat(writer.savedBatchId).isEqualTo("batch-1");
        assertThat(writer.savedBizType).isEqualTo("teacher");
        assertThat(writer.savedFailItems).extracting(ExcelImportFailItem::getRowNo).containsExactly(88, 2, 3);
        assertThat(writer.savedFailItems).extracting(ExcelImportFailItem::getBizKey).containsExactly("excel-format", "B", "C");
        assertThat(tracker.finishedSuccessCount).isEqualTo(2);
        assertThat(tracker.finishedFailCount).isEqualTo(3);
        assertThat(result.getBatchId()).isEqualTo("batch-1");
        assertThat(result.getSuccess()).isEqualTo(2);
        assertThat(result.getFail()).isEqualTo(3);
        assertThat(result.getFailDetails()).hasSize(3);
    }

    @Test
    void executeReturnsEmptyResultWithoutCreatingBatchWhenExcelHasNoRows() {
        FakeBatchTracker tracker = new FakeBatchTracker();
        FakeFailRecordWriter writer = new FakeFailRecordWriter();
        FakeImporter importer = new FakeImporter(tracker, writer);
        importer.parseResult = new ExcelImportParseResult<>(List.of(), List.of());

        ExcelImportResultVO result = importer.execute(new ImportExcelDTO());

        assertThat(result.getBatchId()).isEmpty();
        assertThat(result.getSuccess()).isZero();
        assertThat(result.getFail()).isZero();
        assertThat(tracker.created).isFalse();
        assertThat(writer.savedFailItems).isNull();
    }

    @Test
    void executeMarksBatchFailedAndInvokesFailureHookWhenBusinessImportThrows() {
        FakeBatchTracker tracker = new FakeBatchTracker();
        FakeImporter importer = new FakeImporter(tracker, new FakeFailRecordWriter());
        importer.parseResult = new ExcelImportParseResult<>(List.of(new RowDTO("A")), List.of());
        importer.throwOnImport = true;

        assertThatThrownBy(() -> importer.execute(new ImportExcelDTO())).isInstanceOf(IllegalStateException.class).hasMessage("boom");

        assertThat(tracker.failed).isTrue();
        assertThat(tracker.failedRemark).isEqualTo("boom");
        assertThat(importer.failBatchId).isEqualTo("batch-1");
        assertThat(importer.failCause).isInstanceOf(IllegalStateException.class);
        assertThat(tracker.finished).isFalse();
    }

    private static ExcelImportFailItem fail(Integer rowNo, String bizKey, String message) {
        ExcelImportFailItem item = new ExcelImportFailItem();
        item.setRowNo(rowNo);
        item.setBizKey(bizKey);
        item.setErrorMsg(message);
        return item;
    }

    private record RowDTO(String code) {
    }

    private static class FakeImporter extends AbstractExcelImportTemplate<RowDTO> {

        private ExcelImportParseResult<RowDTO> parseResult;

        private int chunkSize;

        private boolean throwOnImport;

        private final List<ExcelImportBizResult> chunkResults = new ArrayList<>();

        private final List<List<RowDTO>> importedChunks = new ArrayList<>();

        private List<RowDTO> beforeImportRows = List.of();

        private final List<String> afterChunkEvents = new ArrayList<>();

        private int afterImportSuccess;

        private String failBatchId;

        private Throwable failCause;

        FakeImporter(ImportBatchTracker batchTracker, ImportFailRecordWriter failRecordWriter) {
            super(batchTracker, failRecordWriter);
        }

        @Override
        protected Class<RowDTO> importDtoClass() {
            return RowDTO.class;
        }

        @Override
        protected String bizType() {
            return "teacher";
        }

        @Override
        protected String bizName() {
            return "教师导入";
        }

        @Override
        protected ExcelImportBizResult doImport(String batchId, List<RowDTO> rows) {
            if (throwOnImport) {
                throw new IllegalStateException("boom");
            }
            importedChunks.add(List.copyOf(rows));
            return chunkResults.isEmpty() ? ExcelImportBizResult.of(rows.size(), List.of()) : chunkResults.remove(0);
        }

        @Override
        protected List<ExcelImportFailItem> convertExcelFailItems(List<ExcelFailRow<RowDTO>> failRows) {
            return List.of();
        }

        @Override
        protected int chunkSize() {
            return chunkSize;
        }

        @Override
        protected void beforeImport(String batchId, List<RowDTO> rows) {
            beforeImportRows = List.copyOf(rows);
        }

        @Override
        protected void afterChunk(String batchId, int chunkIndex, int chunkTotal, ExcelImportBizResult chunkResult) {
            afterChunkEvents.add(chunkIndex + "/" + chunkTotal + ":" + chunkResult.getSuccessCount());
        }

        @Override
        protected void afterImport(String batchId, ExcelImportBizResult aggregated) {
            afterImportSuccess = aggregated.getSuccessCount();
        }

        @Override
        protected void onFailBatch(String batchId, Throwable ex) {
            failBatchId = batchId;
            failCause = ex;
        }

        @Override
        protected ExcelImportParseResult<RowDTO> parseImportExcel(ImportExcelDTO dto) {
            return parseResult;
        }
    }

    private static class FakeBatchTracker implements ImportBatchTracker {

        private boolean created;

        private String createdBizType;

        private String createdBizName;

        private int createdTotalCount;

        private boolean finished;

        private int finishedSuccessCount;

        private int finishedFailCount;

        private boolean failed;

        private String failedRemark;

        @Override
        public ImportBatchContext createBatch(String bizType, String bizName, String fileName, Long operatorId, int totalCount) {
            created = true;
            createdBizType = bizType;
            createdBizName = bizName;
            createdTotalCount = totalCount;
            return () -> "batch-1";
        }

        @Override
        public void finishBatch(ImportBatchContext ctx, int successCount, int failCount) {
            finished = true;
            finishedSuccessCount = successCount;
            finishedFailCount = failCount;
        }

        @Override
        public void failBatch(ImportBatchContext ctx, String remark) {
            failed = true;
            failedRemark = remark;
        }

        @Override
        public ExcelImportResultVO buildResult(String batchId, int successCount, int failCount) {
            return new ExcelImportResultVO(batchId, successCount, failCount);
        }
    }

    private static class FakeFailRecordWriter implements ImportFailRecordWriter {

        private String savedBatchId;

        private String savedBizType;

        private List<ExcelImportFailItem> savedFailItems;

        @Override
        public void saveFailRecords(String batchId, String bizType, List<ExcelImportFailItem> failItems) {
            savedBatchId = batchId;
            savedBizType = bizType;
            savedFailItems = List.copyOf(failItems);
        }
    }
}
