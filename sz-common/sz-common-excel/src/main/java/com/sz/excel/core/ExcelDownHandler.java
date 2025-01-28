package com.sz.excel.core;

import cn.idev.excel.metadata.FieldCache;
import cn.idev.excel.metadata.FieldWrapper;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import com.sz.core.common.service.DictService;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.StringUtils;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * excel下拉项
 *
 * @author sz
 * @since 2023/12/28 13:32
 */
@Slf4j
public class ExcelDownHandler implements SheetWriteHandler {

    /**
     * Excel表格中的列名英文 仅为了解析列英文，禁止修改
     */
    private static final String EXCEL_COLUMN_NAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 单选数据Sheet名
     */
    private static final String OPTIONS_SHEET_NAME = "options";

    /**
     * 当前单选进度
     */
    private int currentOptionsColumnIndex;

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        final Sheet sheet = writeSheetHolder.getSheet();
        final DataValidationHelper helper = sheet.getDataValidationHelper();
        final Workbook workbook = writeWorkbookHolder.getWorkbook();
        final FieldCache fieldCache = ClassUtils.declaredFields(writeWorkbookHolder.getClazz(), writeWorkbookHolder);

        for (Map.Entry<Integer, FieldWrapper> entry : fieldCache.getSortedFieldMap().entrySet()) {
            final Integer index = entry.getKey();
            final FieldWrapper wrapper = entry.getValue();
            final Field field = wrapper.getField();

            if (field.isAnnotationPresent(DictFormat.class)) {
                final DictFormat dictFormat = field.getDeclaredAnnotation(DictFormat.class);
                if (dictFormat.isSelected()) {
                    processDictFormat(helper, workbook, sheet, index, dictFormat);
                }
            }
        }

        SheetWriteHandler.super.afterSheetCreate(writeWorkbookHolder, writeSheetHolder);
    }

    private void processDictFormat(DataValidationHelper helper, Workbook workbook, Sheet sheet, Integer index, DictFormat dictFormat) {
        try {
            final List<String> options = getOptions(dictFormat);
            if (!options.isEmpty()) {
                dropDownWithSheet(helper, workbook, sheet, index, options);
            }
        } catch (Exception e) {
            log.error("处理 DictFormat 失败", e);
        }
    }

    private List<String> getOptions(DictFormat dictFormat)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<String> options = new ArrayList<>();
        final String dictType = dictFormat.dictType();
        final String converterExp = dictFormat.readConverterExp();
        final String separator = dictFormat.separator();
        final Class<? extends ExcelDynamicSelect>[] classes = dictFormat.sourceClass();

        if (classes.length > 0) {
            options = getDynamicSelectOptions(classes);
        } else if (isNotBlank(dictType)) {
            options = getDictTypeOptions(dictType);
        } else if (isNotBlank(converterExp)) {
            options = ExcelUtils.listByExp(converterExp, separator);
        }

        return options;
    }

    private List<String> getDynamicSelectOptions(Class<? extends ExcelDynamicSelect>[] classes)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<String> options = new ArrayList<>();
        final ExcelDynamicSelect excelDynamicSelect = classes[0].getDeclaredConstructor().newInstance();
        final List<String> dynamicSelectSource = excelDynamicSelect.getSource();
        if (dynamicSelectSource != null && !dynamicSelectSource.isEmpty()) {
            options.addAll(dynamicSelectSource);
        }
        return options;
    }

    private List<String> getDictTypeOptions(String dictType) {
        List<String> options = new ArrayList<>();
        final DictService dictService = SpringApplicationContextUtils.getInstance().getBean(DictService.class);
        final Map<String, String> allDictByDictType = dictService.getAllDictByType(dictType);
        if (allDictByDictType != null && !allDictByDictType.isEmpty()) {
            options.addAll(allDictByDictType.keySet());
        }
        return options;
    }

    private void dropDownWithSheet(DataValidationHelper helper, Workbook workbook, Sheet sheet, Integer celIndex, List<String> value) {
        // 创建下拉数据表
        Sheet simpleDataSheet = Optional.ofNullable(workbook.getSheet(WorkbookUtil.createSafeSheetName(OPTIONS_SHEET_NAME)))
                .orElseGet(() -> workbook.createSheet(WorkbookUtil.createSafeSheetName(OPTIONS_SHEET_NAME)));
        // 将下拉表隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(simpleDataSheet), true);
        // 完善纵向的一级选项数据表
        for (int i = 0; i < value.size(); i++) {
            int finalI = i;
            // 获取每一选项行，如果没有则创建
            Row row = Optional.ofNullable(simpleDataSheet.getRow(i)).orElseGet(() -> simpleDataSheet.createRow(finalI));
            // 获取本级选项对应的选项列，如果没有则创建
            Cell cell = Optional.ofNullable(row.getCell(currentOptionsColumnIndex)).orElseGet(() -> row.createCell(currentOptionsColumnIndex));
            // 设置值
            cell.setCellValue(value.get(i));
        }

        // 创建名称管理器
        Name name = workbook.createName();
        // 设置名称管理器的别名
        String nameName = String.format("%s_%d", OPTIONS_SHEET_NAME, celIndex);
        name.setNameName(nameName);
        // 以纵向第一列创建一级下拉拼接引用位置
        String function = String.format("%s!$%s$1:$%s$%d", OPTIONS_SHEET_NAME, getExcelColumnName(currentOptionsColumnIndex),
                getExcelColumnName(currentOptionsColumnIndex), value.size());
        // 设置名称管理器的引用位置
        name.setRefersToFormula(function);
        // 设置数据校验为序列模式，引用的是名称管理器中的别名
        this.markOptionsToSheet(helper, sheet, celIndex, helper.createFormulaListConstraint(nameName));
        currentOptionsColumnIndex++;
    }

    /**
     * 挂载下拉的列，仅限一级选项
     */
    private void markOptionsToSheet(DataValidationHelper helper, Sheet sheet, Integer celIndex, DataValidationConstraint constraint) {
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, celIndex, celIndex);
        markDataValidationToSheet(helper, sheet, constraint, addressList);
    }

    /**
     * 应用数据校验
     */
    private void markDataValidationToSheet(DataValidationHelper helper, Sheet sheet, DataValidationConstraint constraint, CellRangeAddressList addressList) {
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            // 数据校验
            dataValidation.setSuppressDropDownArrow(true);
            // 错误提示
            dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            dataValidation.createErrorBox("提示", "此值与单元格定义数据不一致");
            dataValidation.setShowErrorBox(true);
            // 选定提示
            dataValidation.createPromptBox("填写说明：", "填写内容只能为下拉中数据，其他数据将导致导入失败");
            dataValidation.setShowPromptBox(true);
            sheet.addValidationData(dataValidation);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * <h2>依据列index获取列名英文</h2> 依据列index转换为Excel中的列名英文
     * <p>
     * 例如第1列，index为0，解析出来为A列
     * </p>
     * 第27列，index为26，解析为AA列
     * <p>
     * 第28列，index为27，解析为AB列
     * </p>
     *
     * @param columnIndex
     *            列index
     * @return 列index所在得英文名
     */
    private String getExcelColumnName(int columnIndex) {
        // 26一循环的次数
        int columnCircleCount = columnIndex / 26;
        // 26一循环内的位置
        int thisCircleColumnIndex = columnIndex % 26;
        // 26一循环的次数大于0，则视为栏名至少两位
        String columnPrefix = columnCircleCount == 0 ? "" : StringUtils.subWithLength(EXCEL_COLUMN_NAME, columnCircleCount - 1, 1);
        // 从26一循环内取对应的栏位名
        String columnNext = StringUtils.subWithLength(EXCEL_COLUMN_NAME, thisCircleColumnIndex, 1);
        // 将二者拼接即为最终的栏位名
        return columnPrefix + columnNext;
    }

}
