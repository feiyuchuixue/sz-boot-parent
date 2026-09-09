package com.sz.excel.core;

import cn.idev.excel.metadata.FieldCache;
import cn.idev.excel.metadata.FieldWrapper;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import com.sz.excel.annotation.ExcelTemplate;
import com.sz.excel.annotation.ImportColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 导入模板必填校验 Handler。
 * <p>
 * 对所有标注了 {@link ImportColumn}（且 {@code required=true}）的字段列，在 Excel Sheet
 * 上添加数据验证规则：
 * <ul>
 * <li>验证公式：文本长度 &ge; 1，即非空</li>
 * <li>验证行范围：第 2 行 ~ 第 (1 + {@link ExcelTemplate#validRows()}) 行</li>
 * <li>错误提示：弹出 STOP 级别对话框，阻止用户提交空值</li>
 * <li>输入提示：鼠标选中该列时显示"此列为必填字段"说明</li>
 * </ul>
 * </p>
 *
 * <p>
 * 注意：Excel 数据验证仅在 Excel / WPS 客户端生效，用户可通过粘贴等方式绕过， 因此<b>后端代码中仍需做非空校验</b>，此
 * Handler 仅作前端友好提示。
 * </p>
 *
 * @author sz
 * @since 2026/03/25
 */
@Slf4j
public class TemplateRequiredValidationHandler implements SheetWriteHandler {

    /**
     * 带 @ImportColumn(required=true) 注解的字段列索引集合（保留字段顺序）
     */
    private final Set<Integer> requiredColumnIndexes = new LinkedHashSet<>();

    /**
     * 数据验证覆盖行数，由 DTO 类上的 {@link ExcelTemplate#validRows()} 决定， 未标注时使用默认值 1000。
     */
    private int validRows = 1000;

    private boolean initialized = false;

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (!initialized) {
            initialized = true;
            // 读取 DTO 类上的 @ExcelTemplate.validRows()，确定验证行数
            Class<?> clazz = writeWorkbookHolder.getClazz();
            ExcelTemplate excelTemplate = clazz.getAnnotation(ExcelTemplate.class);
            if (excelTemplate != null && excelTemplate.validRows() > 0) {
                validRows = excelTemplate.validRows();
            }
            // 扫描 @ImportColumn(required=true) 字段列索引
            try {
                FieldCache fieldCache = ClassUtils.declaredFields(clazz, writeWorkbookHolder);
                for (Map.Entry<Integer, FieldWrapper> entry : fieldCache.getSortedFieldMap().entrySet()) {
                    Field field = entry.getValue().getField();
                    ImportColumn ann = field.getAnnotation(ImportColumn.class);
                    if (ann != null && ann.required()) {
                        requiredColumnIndexes.add(entry.getKey());
                    }
                }
            } catch (Exception e) {
                log.warn("TemplateRequiredValidationHandler 扫描 @ImportColumn 字段失败", e);
                return;
            }
        }

        if (requiredColumnIndexes.isEmpty()) {
            return;
        }

        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();

        for (int colIndex : requiredColumnIndexes) {
            addRequiredValidation(helper, sheet, colIndex, validRows);
        }
    }

    /**
     * 为指定列添加非空数据验证（文本长度 ≥ 1）。
     * <p>
     * 使用文本长度约束（{@code >= 1}）代替自定义公式，兼容 Excel 和 WPS。
     * </p>
     *
     * @param helper
     *            DataValidationHelper
     * @param sheet
     *            目标 Sheet
     * @param colIndex
     *            列索引（0-based）
     * @param validRows
     *            验证行数（从第 2 行起）
     */
    private void addRequiredValidation(DataValidationHelper helper, Sheet sheet, int colIndex, int validRows) {
        // 验证范围：第 2 行（rowIndex=1）到第 (1+validRows) 行
        CellRangeAddressList addressList = new CellRangeAddressList(1, validRows, colIndex, colIndex);

        // 文本长度 >= 1，即非空；比自定义公式兼容性更好，Excel / WPS 均支持
        DataValidationConstraint constraint = helper.createTextLengthConstraint(DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "1", null);

        DataValidation validation = helper.createValidation(constraint, addressList);

        // DataValidation 接口方法，SXSSFWorkbook / XSSFWorkbook 均支持，无需强转
        validation.setSuppressDropDownArrow(true);
        // 错误框：STOP 级别，阻止提交
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("必填项不能为空", "此字段为必填项，请填写后再继续。");
        validation.setShowErrorBox(true);
        // 输入提示：选中单元格时显示
        validation.createPromptBox("填写说明", "此列为必填字段，不能为空。");
        validation.setShowPromptBox(true);

        sheet.addValidationData(validation);
    }
}
