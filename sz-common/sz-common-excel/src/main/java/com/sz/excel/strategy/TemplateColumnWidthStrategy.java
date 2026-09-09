package com.sz.excel.strategy;

import cn.idev.excel.metadata.FieldCache;
import cn.idev.excel.metadata.FieldWrapper;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import cn.idev.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.sz.excel.annotation.ImportColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入模板专用列宽策略。
 * <p>
 * 与 {@link DefaultColumnWidthStyleStrategy} 的区别：
 * <ul>
 * <li><b>兜底最小宽度</b>：{@value #DEFAULT_MIN_WIDTH}（字符单位），避免空数据时列宽过窄。</li>
 * <li><b>自定义列宽</b>：如果字段标注了 {@link ImportColumn#columnWidth()} {@code > 0}，
 * 则强制使用该宽度，不参与自动计算。</li>
 * <li><b>数据余量 padding</b>：在表头字节数基础上额外加 {@value #PADDING}，
 * 为实际填写的数据内容预留足够宽度。</li>
 * </ul>
 * </p>
 *
 * <h3>宽度计算规则（由高到低优先级）</h3>
 * <ol>
 * <li>字段有 {@code @ImportColumn(columnWidth = N)}（N &gt; 0）→ 强制使用 N</li>
 * <li>表头字节数 + 中文字符额外加权 + {@value #PADDING}，取
 * {@code max(计算值, DEFAULT_MIN_WIDTH)}</li>
 * </ol>
 *
 * @author sz
 * @since 2026/03/25
 */
@Slf4j
public class TemplateColumnWidthStrategy extends AbstractColumnWidthStyleStrategy implements SheetWriteHandler {

    /** 列宽上限（Excel 规范 255 字符） */
    private static final int MAX_COLUMN_WIDTH = 255;

    /**
     * 默认兜底最小列宽（字符单位）。 26 ≈ 约 8 个汉字的宽度，足以容纳大多数短文本数据。
     */
    private static final int DEFAULT_MIN_WIDTH = 26;

    /**
     * 额外 padding：在表头宽度基础上多加此值，为用户填写的数据内容预留空间。 与 DefaultColumnWidthStyleStrategy 的
     * +5 相比，这里使用 +10 提供更宽裕的空间。
     */
    private static final int PADDING = 10;

    /** columnIndex → 强制宽度（来自 @ImportColumn(columnWidth=N)，仅 N>0 时有值） */
    private final Map<Integer, Integer> forcedWidthMap = new HashMap<>();

    /** 各 Sheet 的列宽缓存：sheetNo → (columnIndex → maxWidth) */
    private final Map<Integer, Map<Integer, Integer>> cache = new HashMap<>(8);

    /** 是否已完成字段扫描 */
    private boolean initialized = false;

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (initialized) {
            return;
        }
        initialized = true;

        try {
            FieldCache fieldCache = ClassUtils.declaredFields(writeWorkbookHolder.getClazz(), writeWorkbookHolder);
            for (Map.Entry<Integer, FieldWrapper> entry : fieldCache.getSortedFieldMap().entrySet()) {
                Field field = entry.getValue().getField();
                ImportColumn ann = field.getAnnotation(ImportColumn.class);
                if (ann != null && ann.columnWidth() > 0) {
                    forcedWidthMap.put(entry.getKey(), Math.min(ann.columnWidth(), MAX_COLUMN_WIDTH));
                }
            }
        } catch (Exception e) {
            log.warn("TemplateColumnWidthStrategy 扫描 @ImportColumn.columnWidth 失败", e);
        }
    }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex,
            Boolean isHead) {

        int columnIndex = cell.getColumnIndex();

        // 优先级 1：字段上@ImportColumn(columnWidth=N) 强制宽度
        if (forcedWidthMap.containsKey(columnIndex)) {
            int forced = forcedWidthMap.get(columnIndex);
            writeSheetHolder.getSheet().setColumnWidth(columnIndex, forced * 256);
            return;
        }

        // 仅在表头行触发自动计算（模板为空数据，数据行不会进入此分支）
        if (!Boolean.TRUE.equals(isHead)) {
            return;
        }

        // 优先级 2：按表头字节数计算，兜底 DEFAULT_MIN_WIDTH
        int calculated = getHeaderWidth(cell);
        int finalWidth = Math.max(calculated, DEFAULT_MIN_WIDTH);
        finalWidth = Math.min(finalWidth, MAX_COLUMN_WIDTH);

        Map<Integer, Integer> maxColumnWidthMap = cache.computeIfAbsent(writeSheetHolder.getSheetNo(), key -> new HashMap<>(16));
        Integer cached = maxColumnWidthMap.get(columnIndex);
        if (cached == null || finalWidth > cached) {
            maxColumnWidthMap.put(columnIndex, finalWidth);
            writeSheetHolder.getSheet().setColumnWidth(columnIndex, finalWidth * 256);
        }
    }

    /**
     * 计算表头单元格宽度： 字节数 + 中文字符额外加权 + {@value #PADDING}。 中文字符因宽度是 ASCII 的 ~1.8 倍，每个额外
     * +1。
     */
    private int getHeaderWidth(Cell cell) {
        String text = cell.getStringCellValue();
        if (text == null || text.isEmpty()) {
            return DEFAULT_MIN_WIDTH;
        }
        // 去掉 * 号前缀后再计算（@ExcelRequired 会在表头添加 *）
        String stripped = text.startsWith("*") ? text.substring(1) : text;

        int width = stripped.length();
        for (char ch : stripped.toCharArray()) {
            if (ch >= 0x4E00 && ch <= 0x9FA5) {
                width++; // 中文字符额外加 1
            }
        }
        return width + PADDING;
    }
}
