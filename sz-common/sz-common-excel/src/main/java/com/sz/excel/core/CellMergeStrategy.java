package com.sz.excel.core;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import com.sz.core.util.Utils;
import com.sz.excel.annotation.CellMerge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列值重复合并策略
 *
 * @author sz
 */
@Slf4j
public class CellMergeStrategy extends AbstractMergeStrategy {

    private final List<CellRangeAddress> cellList;

    /**
     * 是否有标题
     */
    private final boolean hasTitle;

    /**
     * 开始行号，从0开始（第一行），如果有要忽略的（标题行）需要设置非0
     */
    private int rowIndex;

    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.hasTitle = hasTitle;
        // 行合并开始下标
        this.rowIndex = hasTitle ? 1 : 0;
        this.cellList = handle(list, hasTitle);
    }

    public CellMergeStrategy(List<?> list, int rowIndex) {
        boolean hasTitleBool;
        hasTitleBool = false;
        if (rowIndex > 0) {
            hasTitleBool = true;
        }
        this.hasTitle = hasTitleBool;
        this.rowIndex = rowIndex;
        this.cellList = handle(list, hasTitle);
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // judge the list is not null
        if (!cellList.isEmpty()) {
            // the judge is necessary
            if (cell.getRowIndex() == rowIndex && cell.getColumnIndex() == 0) {
                for (CellRangeAddress item : cellList) {
                    sheet.addMergedRegion(item);
                }
            }
        }
    }

    @SneakyThrows
    private List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        List<CellRangeAddress> cellList = new ArrayList<>();
        if (list.isEmpty()) {
            return cellList;
        }

        Field[] fields = Utils.getFields(list.get(0).getClass());

        // 有注解的字段
        List<Field> mergeFields = new ArrayList<>();
        List<Integer> mergeFieldsIndex = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(CellMerge.class)) {
                CellMerge cm = field.getAnnotation(CellMerge.class);
                mergeFields.add(field);
                mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());
                if (hasTitle) {
                    ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                    rowIndex = Math.max(rowIndex, property.value().length);
                }
            }
        }

        // 生成两两合并单元格
        for (int i = 0; i < list.size(); i++) {
            boolean merged = false;
            if (i > 0) {
                // 存储前一行的字段值
                Map<Field, Object> prevRowValues = new HashMap<>();
                for (int j = 0; j < mergeFields.size(); j++) {
                    Field field = mergeFields.get(j);
                    Object prevVal = Utils.invokeGetter(list.get(i - 1), field.getName());
                    prevRowValues.put(field, prevVal);
                }

                // 检查当前行与前一行的字段值是否相同
                for (int j = 0; j < mergeFields.size(); j++) {
                    Field field = mergeFields.get(j);
                    Object val = Utils.invokeGetter(list.get(i), field.getName());
                    Object prevVal = prevRowValues.get(field);
                    if (prevVal != null && prevVal.equals(val)) {
                        // 合并逻辑
                        if (!merged) {
                            for (int k = i - 1; k < i; k++) {
                                int colNum = mergeFieldsIndex.get(j) - 1; // 注意下标（要-1）
                                cellList.add(new CellRangeAddress(k + rowIndex, i + rowIndex, colNum, colNum));
                            }
                            merged = true;
                        }
                    }
                }
            }
        }
        return cellList;
    }

    @Data
    @AllArgsConstructor
    static class RepeatCell {

        private Object value;

        private int current;

    }

}
