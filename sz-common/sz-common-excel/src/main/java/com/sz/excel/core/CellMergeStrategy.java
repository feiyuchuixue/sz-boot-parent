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
        hasTitleBool = rowIndex > 0;
        this.hasTitle = hasTitleBool;
        this.rowIndex = rowIndex;
        this.cellList = handle(list, hasTitle);
    }

    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // 如果 cellList 不为空，并且当前单元格是第一列的第一行，则合并单元格
        if (!cellList.isEmpty() && cell.getRowIndex() == rowIndex && cell.getColumnIndex() == 0) {
            for (CellRangeAddress item : cellList) {
                sheet.addMergedRegion(item);
            }
        }
    }

    @SneakyThrows
    private List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        List<CellRangeAddress> localCellList = new ArrayList<>(); // Local variables should not shadow class fields
        if (list.isEmpty()) {
            return localCellList;
        }

        // 获取字段信息并初始化合并字段
        List<Field> mergeFields = new ArrayList<>();
        List<Integer> mergeFieldsIndex = new ArrayList<>();
        int startRowIndex = initializeMergeFields(list.getFirst().getClass(), mergeFields, mergeFieldsIndex, hasTitle);

        // 处理合并逻辑
        Map<Field, Object> prevRowValues = new HashMap<>();
        for (int i = 1; i < list.size(); i++) {
            boolean merged = false;
            for (int j = 0; j < mergeFields.size(); j++) {
                Field field = mergeFields.get(j);
                Object currentValue = Utils.invokeGetter(list.get(i), field.getName());
                Object prevValue = prevRowValues.get(field);

                if (prevValue != null && prevValue.equals(currentValue)) {
                    if (!merged) {
                        int colNum = mergeFieldsIndex.get(j) - 1;
                        localCellList.add(new CellRangeAddress(i - 1 + startRowIndex, i + startRowIndex, colNum, colNum));
                        merged = true;
                    }
                } else {
                    prevRowValues.put(field, currentValue);
                }
            }
        }

        return localCellList;
    }

    private int initializeMergeFields(Class<?> clazz, List<Field> mergeFields, List<Integer> mergeFieldsIndex, boolean hasTitle) {
        Field[] fields = Utils.getFields(clazz);
        int startRowIndex = 0;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(CellMerge.class)) {
                CellMerge cm = field.getAnnotation(CellMerge.class);
                mergeFields.add(field);
                mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());

                if (hasTitle) {
                    ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                    startRowIndex = Math.max(startRowIndex, property.value().length);
                }
            }
        }

        return startRowIndex;
    }

    @Data
    @AllArgsConstructor
    static class RepeatCell {

        private Object value;

        private int current;

    }

}
