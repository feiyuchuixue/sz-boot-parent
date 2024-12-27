package com.sz.excel.strategy;

import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.List;

/**
 * 设置表头和填充内容的样式
 */
public class DefaultCellStyleStrategy extends HorizontalCellStyleStrategy {

    private final WriteCellStyle headWriteCellStyle;

    private final WriteCellStyle contentWriteCellStyle;

    /**
     * 操作列
     */
    private final List<Integer> columnIndexes;

    public DefaultCellStyleStrategy(List<Integer> columnIndexes, WriteCellStyle headWriteCellStyle, WriteCellStyle contentWriteCellStyle) {
        this.columnIndexes = columnIndexes;
        this.headWriteCellStyle = headWriteCellStyle;
        this.contentWriteCellStyle = contentWriteCellStyle;
    }

    // 设置头样式
    @Override
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        // 获取字体实例
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("宋体");
        // 表头不同处理
        /*
         * if (columnIndexes.get(0).equals(context.getRowIndex())) {
         * System.out.println("1111111111111");
         * headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
         * headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
         * headWriteFont.setFontHeightInPoints((short) 12);
         * headWriteFont.setBold(false); headWriteFont.setFontName("宋体"); } else {
         */
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteFont.setFontHeightInPoints((short) 14);
        headWriteFont.setBold(false);
        headWriteFont.setFontName("宋体");
        // }
        headWriteCellStyle.setWriteFont(headWriteFont);
        if (stopProcessing(context)) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(headWriteCellStyle, cellData.getOrCreateStyle());
    }

    // 设置填充数据样式
    @Override
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("宋体");
        contentWriteFont.setFontHeightInPoints((short) 12);
        // 设置数据填充后的实线边框
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(contentWriteCellStyle, cellData.getOrCreateStyle());
    }
}