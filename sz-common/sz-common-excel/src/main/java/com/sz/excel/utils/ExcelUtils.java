package com.sz.excel.utils;

import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import com.sz.core.common.entity.DictVO;
import com.sz.core.common.dict.DictService;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.annotation.ExcelEnumFormat;
import com.sz.excel.convert.CustomEnumStringConvert;
import com.sz.excel.convert.CustomIntegerStringConvert;
import com.sz.excel.convert.CustomLongStringConvert;
import com.sz.excel.convert.CustomStringStringConvert;
import com.sz.excel.core.*;
import com.sz.excel.support.ExcelExportSafeValueResolver;
import com.sz.excel.strategy.DefaultCellStyleStrategy;
import com.sz.excel.strategy.DefaultColumnWidthStyleStrategy;
import com.sz.excel.strategy.TemplateColumnWidthStrategy;
import com.sz.excel.strategy.TemplateHeaderStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel工具类
 *
 * @author sz
 * @since 2023/12/26 15:16
 */
@Slf4j
public class ExcelUtils {

    /** 枚举类扫描结果缓存：同一 Class 的枚举字段集合只反射一次 */
    private static final Map<Class<?>, Set<Class<? extends Enum<?>>>> ENUM_CLASSES_CACHE = new ConcurrentHashMap<>();

    private ExcelUtils() {
        throw new IllegalStateException("ExcelUtils class Illegal");
    }

    /**
     * 异步导入 Excel 数据并同步返回结果。
     * <p>
     * 该方法从输入流中读取 Excel 文件，使用指定的类进行解析，并在解析过程中提供自定义字典转换。 可选择验证 Excel
     * 文件的表头格式。解析完成后，返回包含解析结果的对象。
     *
     * @param is
     *            Excel 文件的输入流
     * @param clazz
     *            用于映射 Excel 数据的类类型
     * @param validateHeader
     *            是否验证表头格式
     * @param <T>
     *            解析结果的类型
     * @return 包含解析结果的 ExcelResult 对象
     */

    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean validateHeader) {
        // 在这里获取字典传递给cover，减轻redis压力
        Map<String, List<DictVO>> dictmap = getDictList();
        ExcelListenerFactory listenerFactory = SpringApplicationContextUtils.getInstance().getBean(ExcelListenerFactory.class);
        DefaultExcelListener<T> listener = listenerFactory.createListener(validateHeader, clazz);
        InputStream normalizedInputStream = normalizeImportHeader(is);
        var readerBuilder = FastExcelFactory.read(normalizedInputStream, clazz, listener).registerConverter(new CustomStringStringConvert(dictmap))
                .registerConverter(new CustomIntegerStringConvert(dictmap)).registerConverter(new CustomLongStringConvert(dictmap));
        registerEnumConverters(readerBuilder, clazz);
        readerBuilder.sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os) {
        // 在这里获取字典传递给cover，减轻redis压力
        Map<String, List<DictVO>> dictmap = getDictList();
        List<T> safeRows = buildSafeExportRows(list, clazz);
        Set<String> excludedColumnFieldNames = collectExcludedColumnFieldNames(clazz);
        ExcelWriterSheetBuilder builder = FastExcelFactory.write(os, clazz).autoCloseStream(false)
                // 列宽自动适配
                .registerWriteHandler(new DefaultColumnWidthStyleStrategy())
                // 表格样式
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 自定义cover处理器
                .registerConverter(new CustomStringStringConvert(dictmap)).registerConverter(new CustomIntegerStringConvert(dictmap))
                .registerConverter(new CustomLongStringConvert(dictmap)).excludeColumnFieldNames(excludedColumnFieldNames).sheet(sheetName);
        registerEnumConverters(builder, clazz);
        // 添加下拉框操作
        builder.registerWriteHandler(new ExcelDownHandler());
        builder.doWrite(safeRows);
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os, boolean isMerge) {
        // 在这里获取字典传递给cover，减轻redis压力
        Map<String, List<DictVO>> dictmap = getDictList();
        List<T> safeRows = buildSafeExportRows(list, clazz);
        Set<String> excludedColumnFieldNames = collectExcludedColumnFieldNames(clazz);
        ExcelWriterSheetBuilder builder = FastExcelFactory.write(os, clazz).autoCloseStream(false)
                // 列宽自动适配
                .registerWriteHandler(new DefaultColumnWidthStyleStrategy())
                // 表格样式
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 自定义cover处理器
                .registerConverter(new CustomStringStringConvert(dictmap)).registerConverter(new CustomIntegerStringConvert(dictmap))
                .registerConverter(new CustomLongStringConvert(dictmap)).excludeColumnFieldNames(excludedColumnFieldNames).sheet(sheetName);
        registerEnumConverters(builder, clazz);
        if (isMerge) {
            builder.registerWriteHandler(new CellMergeStrategy(safeRows, 1));
        }

        // 添加下拉框操作
        builder.registerWriteHandler(new ExcelDownHandler());
        builder.doWrite(safeRows);
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
     * 将表达式转换为列表。
     * <p>
     * 该方法根据指定的分隔符，将字符串表达式转换为字符串列表。
     *
     * @param converterExp
     *            要转换的字符串表达式
     * @param separator
     *            用于分隔表达式的分隔符
     * @return 转换后的字符串列表
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

    /**
     * 根据 ImportDTO 的 {@link cn.idev.excel.annotation.ExcelProperty} 和
     * {@link com.sz.excel.annotation.DictFormat} 注解动态生成空白导入模板。
     * <p>
     * 与普通 {@link #exportExcel} 的区别：
     * <ul>
     * <li>表头样式：蓝色背景（#4472C4）+ 白色字体 + 加粗</li>
     * <li>{@link com.sz.excel.annotation.ImportColumn} required = true 字段：表头追加红色
     * {@code *} 前缀</li>
     * <li>列宽：兜底最小宽度 26，支持 {@code@ImportColumn(columnWidth=N)} 强制指定</li>
     * <li>冻结首行，方便用户填写大量数据时对照表头</li>
     * </ul>
     * </p>
     *
     * @param clazz
     *            带 {@link cn.idev.excel.annotation.ExcelProperty} 注解的 ImportDTO 类
     * @param sheetName
     *            Sheet 名称
     * @param os
     *            输出流（由调用方负责关闭）
     * @param <T>
     *            DTO 类型
     */
    public static <T> void generateTemplate(Class<T> clazz, String sheetName, OutputStream os) {
        Map<String, List<DictVO>> dictmap = getDictList();
        ExcelWriterSheetBuilder builder = FastExcelFactory.write(os, clazz).autoCloseStream(false)
                // 模板列宽策略（兜底最小宽度 +@ImportColumn(columnWidth=N) 自定义宽度）
                .registerWriteHandler(new TemplateColumnWidthStrategy())
                // 表头样式：灰色背景 + 宋体14号（与普通导出保持一致），
                // TemplateHeaderStyleStrategy 会在此基础上叠加必填列红色字体
                .registerWriteHandler(new DefaultCellStyleStrategy(Arrays.asList(0, 1), new WriteCellStyle(), new WriteCellStyle()))
                // 必填列：红色字体 + * 前缀（order=0，在灰色样式 merge 后、FillStyle 写入前执行）
                .registerWriteHandler(new TemplateHeaderStyleStrategy())
                // 字典转换器（下拉框数据来源）
                .registerConverter(new CustomStringStringConvert(dictmap)).registerConverter(new CustomIntegerStringConvert(dictmap))
                .registerConverter(new CustomLongStringConvert(dictmap)).sheet(sheetName);
        registerEnumConverters(builder, clazz);
        // 下拉框
        builder.registerWriteHandler(new ExcelDownHandler());
        // 冻结首行
        builder.registerWriteHandler(new FreezeHeaderHandler());
        // 必填字段 Excel 数据验证（非空提示 + 错误弹框）
        builder.registerWriteHandler(new TemplateRequiredValidationHandler());
        // 写入空数据（仅生成表头行）
        builder.doWrite(Collections.emptyList());
    }

    /**
     * 根据 ImportDTO 动态生成空白导入模板，Sheet 名称默认为 "导入模板"。
     *
     * @param clazz
     *            带 {@link cn.idev.excel.annotation.ExcelProperty} 注解的 ImportDTO 类
     * @param os
     *            输出流（由调用方负责关闭）
     * @param <T>
     *            DTO 类型
     */
    public static <T> void generateTemplate(Class<T> clazz, OutputStream os) {
        generateTemplate(clazz, "导入模板", os);
    }

    private static Map<String, List<DictVO>> getDictList() {
        DictService dictService = SpringApplicationContextUtils.getInstance().getBean(DictService.class);
        return dictService.getAllDict();
    }

    private static InputStream normalizeImportHeader(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet != null) {
                Row headerRow = sheet.getRow(sheet.getFirstRowNum());
                if (headerRow != null) {
                    for (Cell cell : headerRow) {
                        if (cell == null || cell.getCellType() == org.apache.poi.ss.usermodel.CellType.BLANK) {
                            continue;
                        }
                        String headerName = cell.getStringCellValue();
                        String normalizedHeaderName = normalizeHeaderName(headerName);
                        if (!normalizedHeaderName.equals(headerName)) {
                            cell.setCellValue(normalizedHeaderName);
                        }
                    }
                }
            }
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception ex) {
            throw new IllegalStateException("Excel 导入表头预处理失败", ex);
        }
    }

    public static String normalizeHeaderName(String headerName) {
        if (headerName == null) {
            return null;
        }
        return headerName.replaceFirst("^\\*\\s*", "").trim();
    }

    private static void registerEnumConverters(Object builder, Class<?> clazz) {
        Set<Class<? extends Enum<?>>> enumClasses = collectExcelEnumClasses(clazz);
        for (Class<? extends Enum<?>> enumClass : enumClasses) {
            if (builder instanceof cn.idev.excel.read.builder.ExcelReaderBuilder readerBuilder) {
                readerBuilder.registerConverter(new CustomEnumStringConvert(enumClass));
            } else if (builder instanceof ExcelWriterSheetBuilder sheetBuilder) {
                sheetBuilder.registerConverter(new CustomEnumStringConvert(enumClass));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<? extends Enum<?>>> collectExcelEnumClasses(Class<?> clazz) {
        return ENUM_CLASSES_CACHE.computeIfAbsent(clazz, ExcelUtils::doCollectExcelEnumClasses);
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<? extends Enum<?>>> doCollectExcelEnumClasses(Class<?> clazz) {
        Set<Class<? extends Enum<?>>> enumClasses = new LinkedHashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(DictFormat.class)) {
                continue;
            }
            if (!field.isAnnotationPresent(ExcelEnumFormat.class)) {
                continue;
            }
            if (!field.getType().isEnum()) {
                throw new IllegalStateException("@ExcelEnumFormat 仅允许标注在枚举字段上: " + clazz.getSimpleName() + "." + field.getName());
            }
            enumClasses.add((Class<? extends Enum<?>>) field.getType());
        }
        return enumClasses;
    }

    private static <T> List<T> buildSafeExportRows(List<T> list, Class<T> clazz) {
        List<Field> exportFields = collectExportFields(clazz);
        List<T> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            T rowObject = list.get(i);
            T safeRow = createSafeExportRow(rowObject, clazz);
            for (Field field : exportFields) {
                ExcelExportSafeValueResolver.ResolvedValue resolvedValue = ExcelExportSafeValueResolver.resolve(rowObject, field, i + 1);
                if (!resolvedValue.failed()) {
                    continue;
                }
                overwriteFieldValue(safeRow, field, resolveFallbackValue(field));
            }
            rows.add(safeRow);
        }
        return rows;
    }

    private static <T> T createSafeExportRow(T source, Class<T> clazz) {
        if (source == null) {
            return instantiate(clazz);
        }
        return BeanCopyUtils.copy(source, clazz);
    }

    private static <T> T instantiate(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Excel 导出安全副本创建失败: " + clazz.getSimpleName(), ex);
        }
    }

    private static Object resolveFallbackValue(Field field) {
        return String.class.equals(field.getType()) ? "" : null;
    }

    private static void overwriteFieldValue(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Excel 导出安全副本字段写入失败: " + field.getName(), ex);
        }
    }

    private static List<Field> collectExportFields(Class<?> clazz) {
        List<Field> exportFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!isIncludedExportField(field)) {
                continue;
            }
            exportFields.add(field);
        }
        return exportFields;
    }

    private static Set<String> collectExcludedColumnFieldNames(Class<?> clazz) {
        Set<String> excludedFields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (isIncludedExportField(field)) {
                continue;
            }
            excludedFields.add(field.getName());
            logExcludedField(clazz, field);
        }
        return excludedFields;
    }

    private static boolean isIncludedExportField(Field field) {
        if (field.isAnnotationPresent(cn.idev.excel.annotation.ExcelIgnore.class)) {
            return false;
        }
        if (!field.isAnnotationPresent(cn.idev.excel.annotation.ExcelProperty.class)) {
            return false;
        }
        return isSupportedExcelExportType(field);
    }

    private static boolean isSupportedExcelExportType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isPrimitive()) {
            return true;
        }
        if (CharSequence.class.isAssignableFrom(fieldType) || Number.class.isAssignableFrom(fieldType) || Boolean.class.isAssignableFrom(fieldType)
                || Enum.class.isAssignableFrom(fieldType) || Date.class.isAssignableFrom(fieldType) || LocalDateTime.class.isAssignableFrom(fieldType)
                || LocalDate.class.isAssignableFrom(fieldType) || LocalTime.class.isAssignableFrom(fieldType)) {
            return true;
        }
        return false;
    }

    private static void logExcludedField(Class<?> clazz, Field field) {
        if (!field.isAnnotationPresent(cn.idev.excel.annotation.ExcelProperty.class)) {
            return;
        }
        log.warn("[Excel export warn] class={}, field={}, type={}, msg=字段类型当前不支持安全导出，已自动跳过该列", clazz.getSimpleName(), field.getName(),
                field.getType().getSimpleName());
    }

}
