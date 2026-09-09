package com.sz.excel.strategy;

import cn.idev.excel.metadata.FieldCache;
import cn.idev.excel.metadata.FieldWrapper;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import com.sz.excel.annotation.ImportColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 导入模板专用表头样式策略。
 * <p>
 * 对标注了 {@link ImportColumn}（且 {@code required=true}）的字段列做差异化处理：
 * <ul>
 * <li>表头前方追加 {@code "* "} 前缀，用于提示该列为必填</li>
 * <li>优先尝试通过 {@link XSSFRichTextString} 将星号渲染为红色</li>
 * <li>同时将整格表头字体设置为红色作为兜底，确保在流式写出场景下必填列仍有明显视觉区分</li>
 * </ul>
 * </p>
 * <p>
 * SXSSFWorkbook（FastExcel 默认写入模式）完全支持 {@link XSSFRichTextString}， 可通过
 * {@link Cell#setCellValue(org.apache.poi.ss.usermodel.RichTextString)} 写入富文本。
 * </p>
 *
 * @author sz
 * @since 2026/03/25
 */
@Slf4j
public class TemplateHeaderStyleStrategy implements CellWriteHandler, SheetWriteHandler {

    /** 带 @ImportColumn(required=true) 注解的字段列索引集合 */
    private Set<Integer> requiredColumnIndexes;

    private boolean initialized = false;

    /**
     * order=0（DEFAULT_ORDER），位于安全窗口： 晚于 HorizontalCellStyleStrategy(-50000)，早于
     * FillStyleCellWriteHandler(50000)。
     */
    @Override
    public int order() {
        return 0;
    }

    // SheetWriteHandler：Sheet 创建后扫描 @ImportColumn(required=true) 字段列索引
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        requiredColumnIndexes = new HashSet<>();
        try {
            FieldCache fieldCache = ClassUtils.declaredFields(writeWorkbookHolder.getClazz(), writeWorkbookHolder);
            for (Map.Entry<Integer, FieldWrapper> entry : fieldCache.getSortedFieldMap().entrySet()) {
                Field field = entry.getValue().getField();
                ImportColumn ann = field.getAnnotation(ImportColumn.class);
                if (ann != null && ann.required()) {
                    requiredColumnIndexes.add(entry.getKey());
                }
            }
        } catch (Exception e) {
            log.warn("TemplateHeaderStyleStrategy 扫描 @ImportColumn 字段失败", e);
        }
        initialized = true;
    }

    // -----------------------------------------------------------------------
    // CellWriteHandler：afterCellDispose（order=0）
    //
    // 策略：
    // 1. Cell 值直接写为 "* 原表头"，导入时由 DefaultExcelListener 统一去掉前缀再校验
    // 2. 优先尝试 RichText：红色 "* " + 黑色原文字
    // 3. 同时把整个表头字体设置为红色作为兜底，避免流式写出下 RichText 不生效时仍显示黑色
    // -----------------------------------------------------------------------

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (!initialized || !isRequiredHeader(context)) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        if (cellData == null) {
            return;
        }

        Cell cell = context.getCell();
        String text = cell.getStringCellValue();
        if (text == null) {
            return;
        }
        if (text.startsWith("*")) {
            return;
        }

        String displayText = "* " + text;

        // 1. 用 RichTextString 渲染：红色 "* " + 黑色原文字
        Workbook workbook = context.getWriteSheetHolder().getSheet().getWorkbook();

        // 红色字体：用于 "* " 前缀
        Font redFont = workbook.createFont();
        redFont.setColor(IndexedColors.RED.getIndex());

        // 黑色字体：用于原始表头文字
        Font blackFont = workbook.createFont();
        blackFont.setColor(IndexedColors.BLACK.getIndex());

        XSSFRichTextString richText = new XSSFRichTextString(displayText);
        richText.applyFont(0, 2, redFont); // "* " 红色
        richText.applyFont(2, richText.length(), blackFont); // 原文字黑色

        cell.setCellValue(richText);

        // 2. 同步更新 WriteCellData 样式（用于 FillStyleCellWriteHandler 写 CellStyle）
        // 这里使用整格红色作为兜底，保证必填列在流式导出下也有明显视觉效果
        WriteCellStyle style = cellData.getOrCreateStyle();
        WriteFont existingFont = style.getWriteFont();
        WriteFont newFont = new WriteFont();
        if (existingFont != null) {
            WriteFont.merge(existingFont, newFont);
        }
        newFont.setColor(IndexedColors.RED.getIndex());
        newFont.setBold(true);
        style.setWriteFont(newFont);
    }

    /** 判断当前 context 是否为"必填列的表头行" */
    private boolean isRequiredHeader(CellWriteHandlerContext context) {
        if (!Boolean.TRUE.equals(context.getHead())) {
            return false;
        }
        int columnIndex = context.getColumnIndex();
        return requiredColumnIndexes != null && requiredColumnIndexes.contains(columnIndex);
    }
}
