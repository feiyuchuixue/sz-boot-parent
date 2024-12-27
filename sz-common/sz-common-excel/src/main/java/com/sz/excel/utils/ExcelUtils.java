package com.sz.excel.utils;

import cn.idev.excel.FastExcel;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import com.sz.core.common.entity.DictVO;
import com.sz.core.common.service.DictService;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.excel.convert.CustomIntegerStringConvert;
import com.sz.excel.convert.CustomLongStringConvert;
import com.sz.excel.convert.CustomStringStringConvert;
import com.sz.excel.core.*;
import com.sz.excel.strategy.DefaultCellStyleStrategy;
import com.sz.excel.strategy.DefaultColumnWidthStyleStrategy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
     * @param validateHeader
     * @param <T>
     * @return
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean validateHeader) {
        // 在这里获取字典传递给cover，减轻redis压力
        Map<String, List<DictVO>> dictmap = getDictList();
        ExcelListenerFactory listenerFactory = SpringApplicationContextUtils.getBean(ExcelListenerFactory.class);
        DefaultExcelListener<T> listener = listenerFactory.createListener(validateHeader, clazz);
        FastExcel.read(is, clazz, listener).registerConverter(new CustomStringStringConvert(dictmap)).registerConverter(new CustomIntegerStringConvert(dictmap))
                .registerConverter(new CustomLongStringConvert(dictmap)).sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os) {
        // 在这里获取字典传递给cover，减轻redis压力
        Map<String, List<DictVO>> dictmap = getDictList();
        ExcelWriterSheetBuilder builder = FastExcel.write(os, clazz).autoCloseStream(false)
                // 列宽自动适配
                .registerWriteHandler(new DefaultColumnWidthStyleStrategy())
                // 表格样式
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 自定义cover处理器
                .registerConverter(new CustomStringStringConvert(dictmap)).registerConverter(new CustomIntegerStringConvert(dictmap))
                .registerConverter(new CustomLongStringConvert(dictmap)).sheet(sheetName);
        // 添加下拉框操作
        builder.registerWriteHandler(new ExcelDownHandler());
        builder.doWrite(list);
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os, boolean isMerge) {
        // 在这里获取字典传递给cover，减轻redis压力
        Map<String, List<DictVO>> dictmap = getDictList();
        ExcelWriterSheetBuilder builder = FastExcel.write(os, clazz).autoCloseStream(false)
                // 列宽自动适配
                .registerWriteHandler(new DefaultColumnWidthStyleStrategy())
                // 表格样式
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 自定义cover处理器
                .registerConverter(new CustomStringStringConvert(dictmap)).registerConverter(new CustomIntegerStringConvert(dictmap))
                .registerConverter(new CustomLongStringConvert(dictmap)).sheet(sheetName);
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

    private static Map<String, List<DictVO>> getDictList() {
        DictService dictService = SpringApplicationContextUtils.getBean(DictService.class);
        return dictService.getAllDict();
    }

}
