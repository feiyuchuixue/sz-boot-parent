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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * excel下拉项
 *
 * @ClassName ExcelDownHandler
 * @Author sz
 * @Date 2023/12/28 13:32
 * @Version 1.0
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
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        FieldCache fieldCache = ClassUtils.declaredFields(writeWorkbookHolder.getClazz(), writeWorkbookHolder);
        for (Map.Entry<Integer, FieldWrapper> entry : fieldCache.getSortedFieldMap().entrySet()) {
            Integer index = entry.getKey();
            FieldWrapper wrapper = entry.getValue();
            Field field = wrapper.getField();
            // 循环实体中的每个属性
            // 可选的下拉值
            List<String> options = new ArrayList<>();
            // 下拉操作优先级：动态下拉sourceClass > dictType > readCoverExp
            if (field.isAnnotationPresent(DictFormat.class)) {
                DictFormat dictFormat = field.getDeclaredAnnotation(DictFormat.class);
                if (dictFormat.isSelected()) { // 如果标识了某个字段作为select下拉，那么做下拉处理
                    String dictType = dictFormat.dictType();
                    String converterExp = dictFormat.readConverterExp();
                    String separator = dictFormat.separator();
                    Class<? extends ExcelDynamicSelect>[] classes = dictFormat.sourceClass();
                    if (classes.length > 0) { // 根据
                        try {
                            ExcelDynamicSelect excelDynamicSelect = classes[0].newInstance();
                            List<String> dynamicSelectSource = excelDynamicSelect.getSource();
                            if (dynamicSelectSource != null && !dynamicSelectSource.isEmpty()) {
                                options = dynamicSelectSource;
                            }
                        } catch (InstantiationException | IllegalAccessException e) {
                            log.error("解析动态下拉框数据异常", e);
                        }
                    } else if (StringUtils.isNotBlank(dictType)) { // 根据dictType渲染下拉
                        DictService dictService = SpringApplicationContextUtils.getBean(DictService.class);
                        Map<String, String> allDictByDictType = dictService.getAllDictByType(dictType);
                        if (allDictByDictType != null && !allDictByDictType.isEmpty()) {
                            options = new ArrayList<>(allDictByDictType.keySet());
                        }
                    } else if (StringUtils.isNotBlank(converterExp)) { // 根据converterExp渲染下拉
                        options = ExcelUtils.listByExp(converterExp, separator);
                    }
                    if (!options.isEmpty()) {
                        dropDownWithSheet(helper, workbook, sheet, index, options);
                    }
                }
            }
        }
        SheetWriteHandler.super.afterSheetCreate(writeWorkbookHolder, writeSheetHolder);
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
