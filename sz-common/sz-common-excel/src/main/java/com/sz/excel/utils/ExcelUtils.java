package com.sz.excel.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.sz.excel.convert.ExcelBigNumberConvert;
import com.sz.excel.core.CellMergeStrategy;
import com.sz.excel.core.DefaultExcelListener;
import com.sz.excel.core.ExcelDownHandler;
import com.sz.excel.core.ExcelResult;
import com.sz.excel.strategy.DefaultCellStyleStrategy;
import com.sz.excel.strategy.DefaultColumnWidthStyleStrategy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName ExcelUtils
 * @Author sz
 * @Date 2023/12/26 15:16
 * @Version 1.0
 */
public class ExcelUtils {

    /**
     * 异步导入 同步返回
     *
     * @param is
     * @param clazz
     * @param isValidate
     * @param <T>
     * @return
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<>(isValidate);
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os) {
        ExcelWriterSheetBuilder builder = EasyExcel.write(os, clazz).autoCloseStream(false)
                // 列宽自动适配
                .registerWriteHandler(new DefaultColumnWidthStyleStrategy())
                // 表格样式
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 大数值自动转换 防止失真
                .registerConverter(new ExcelBigNumberConvert()).sheet(sheetName);
        // 添加下拉框操作
        builder.registerWriteHandler(new ExcelDownHandler());
        builder.doWrite(list);
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os, boolean isMerge) {
        ExcelWriterSheetBuilder builder = EasyExcel.write(os, clazz).autoCloseStream(false)
                // 列宽自动适配
                .registerWriteHandler(new DefaultColumnWidthStyleStrategy())
                // 表格样式
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 大数值自动转换 防止失真
                .registerConverter(new ExcelBigNumberConvert()).sheet(sheetName);
        if (isMerge) {
            builder.registerWriteHandler(new CellMergeStrategy(list, 1));
        }

        // 添加下拉框操作
        builder.registerWriteHandler(new ExcelDownHandler());
        builder.doWrite(list);
    }

    /**
     * 解析值(import方向) 男=0,女=1,未知=2 禁言=1000003,禁用=1000002,正常=1000001
     *
     * @param propertyValue
     *            参数值
     * @param converterExp
     *            翻译注解
     * @param separator
     *            分隔符
     * @return 解析后值
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        String[] convertSource = converterExp.split(separator);
        for (String item : convertSource) {
            String[] itemArray = item.split("="); // ["禁言","1000003"]
            if (itemArray[0].equals(propertyValue)) { // propertyValue = 禁言
                return itemArray[1];
            }
        }
        return "";
    }

    /**
     * 解析值(export方向) 禁言=1000003,禁用=1000002,正常=1000001
     *
     * @param propertyValue
     *            参数值
     * @param converterExp
     *            翻译注解
     * @param separator
     *            分隔符
     * @return 解析后值
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        String[] convertSource = converterExp.split(separator);
        for (String item : convertSource) {
            String[] itemArray = item.split("="); // ["禁言", "1000003"]
            if (itemArray[1].equals(propertyValue)) { // propertyValue = 1000003
                return itemArray[0];
            }
        }
        return "";
    }

    /**
     * 将exp转为list
     *
     * @param converterExp
     * @param separator
     * @return
     */
    public static List<String> listByExp(String converterExp, String separator) {
        List<String> list = new ArrayList<>();
        String[] convertSource = converterExp.split(separator);
        for (String item : convertSource) {
            String[] itemArray = item.split("="); // ["禁言", "1000003"]
            list.add(itemArray[0]);
        }
        return list;
    }

}
