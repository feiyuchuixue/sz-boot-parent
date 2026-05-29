package com.sz.excel.core;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelAnalysisException;
import cn.idev.excel.exception.ExcelDataConvertException;
import cn.idev.excel.metadata.CellExtra;
import com.sz.core.util.JsonUtils;
import com.sz.excel.utils.ExcelUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author sz
 * @since 2023/12/26 14:43
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {

    /**
     * 是否Validator检验，默认为是
     */
    private boolean validateHeader = true;

    /**
     * excel 表头数据
     */
    private Map<Integer, String> headMap;

    private ParameterizedType type;

    private Class<T> clazz;

    /**
     * 导入回执
     */
    private ExcelResult<T> excelResult;

    public DefaultExcelListener(boolean validateHeader, Class<T> clazz) {
        this.excelResult = new DefaultExcelResult<>();
        this.validateHeader = validateHeader;
        this.clazz = clazz;
    }

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        excelResult.getList().add(data);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        log.debug("解析表头数据: {}", JsonUtils.toJsonString(headMap));
        // 校验表头
        if (validateHeader) {
            // 获取所有字段
            Field[] fields = clazz.getDeclaredFields();
            Map<Integer, String> expectedHeadMap = new TreeMap<>();
            Map<Integer, String> normalizedActualHeadMap = new TreeMap<>();
            for (Field field : fields) {
                // 检查字段是否有@ExcelProperty注解
                if (field.isAnnotationPresent(ExcelProperty.class)) {
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    expectedHeadMap.put(expectedHeadMap.size(), excelProperty.value()[0]);
                }
            }
            headMap.forEach((index, header) -> normalizedActualHeadMap.put(index, ExcelUtils.normalizeHeaderName(header)));
            if (headMap.isEmpty()) {
                throw new ExcelAnalysisException("无效的表头");
            } else if (!normalizedActualHeadMap.equals(expectedHeadMap)) {
                int mismatchIndex = findFirstHeaderMismatchIndex(expectedHeadMap, normalizedActualHeadMap);
                String errMsg = buildHeaderMismatchMessage(expectedHeadMap, headMap, normalizedActualHeadMap, mismatchIndex);
                throw new ExcelAnalysisException(errMsg);
            } else {
                log.debug("表头一致");
            }
        }
    }

    /**
     * 处理发生的异常。
     *
     * 该方法用于处理在处理过程中出现的异常，根据异常类型和上下文信息提供适当的处理逻辑。
     *
     * @param exception
     *            发生的异常
     * @param context
     *            异常发生时的上下文信息，提供额外的处理依据
     */

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        String errMsg;
        Integer rowNo = context.readRowHolder() == null ? null : context.readRowHolder().getRowIndex() + 1;
        boolean continueRead = false;
        if (exception instanceof ExcelDataConvertException excelDataConvertException) {
            // 如果是某一个单元格的转换异常 能获取到具体行号
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errMsg = String.format("第%d行-第%d列-表头 [%s]: 解析异常<br/>", rowIndex + 1, columnIndex + 1, headMap.get(columnIndex));
            rowNo = rowIndex + 1;
            continueRead = true;
            log.error(errMsg);
        } else if (exception instanceof ConstraintViolationException constraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            String constraintViolationsMsg = "";
            if (constraintViolations != null && !constraintViolations.isEmpty()) {
                constraintViolationsMsg = constraintViolations.stream().map(ConstraintViolation::getMessage).filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
            }
            errMsg = String.format("第%d行数据校验异常: %s", context.readRowHolder().getRowIndex() + 1, constraintViolationsMsg);
            continueRead = true;
            log.error(errMsg);
        } else {
            errMsg = exception.getMessage();
        }
        excelResult.getErrorList().add(errMsg);
        T currentRowData = null;
        if (context.readRowHolder() != null && context.readRowHolder().getCurrentRowAnalysisResult() != null) {
            try {
                Object rowData = context.readRowHolder().getCurrentRowAnalysisResult();
                if (clazz.isInstance(rowData)) {
                    currentRowData = clazz.cast(rowData);
                } else {
                    log.debug("当前失败行数据类型不匹配，忽略 rowData 采集: {}", rowData.getClass().getName());
                }
            } catch (RuntimeException e) {
                log.debug("当前失败行数据采集失败，忽略 rowData 采集", e);
            }
        }
        excelResult.getFailRowList().add(new ExcelFailRow<>(rowNo, errMsg, currentRowData));
        if (!continueRead) {
            throw new ExcelAnalysisException(errMsg);
        }
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        super.extra(extra, context);
    }

    /**
     * 数据解析完毕
     *
     * @param analysisContext
     *            解析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return super.hasNext(context);
    }

    private int findFirstHeaderMismatchIndex(Map<Integer, String> expectedHeadMap, Map<Integer, String> actualHeadMap) {
        int maxSize = Math.max(expectedHeadMap.size(), actualHeadMap.size());
        for (int index = 0; index < maxSize; index++) {
            if (!Objects.equals(expectedHeadMap.get(index), actualHeadMap.get(index))) {
                return index;
            }
        }
        return -1;
    }

    private String buildHeaderMismatchMessage(Map<Integer, String> expectedHeadMap, Map<Integer, String> actualHeadMap,
            Map<Integer, String> normalizedActualHeadMap, int mismatchIndex) {
        String expectedHeader = mismatchIndex >= 0 ? expectedHeadMap.get(mismatchIndex) : null;
        String actualHeader = mismatchIndex >= 0 ? actualHeadMap.get(mismatchIndex) : null;
        String normalizedActualHeader = mismatchIndex >= 0 ? normalizedActualHeadMap.get(mismatchIndex) : null;

        StringBuilder message = new StringBuilder("表头校验失败");
        if (mismatchIndex >= 0) {
            message.append(String.format("：第%d列表头不匹配。", mismatchIndex + 1));
        } else {
            message.append("：表头与导入模板不一致。");
        }

        message.append("<br/><br/>").append("期望：").append(formatHeaderValue(expectedHeader)).append("<br/>").append("实际：")
                .append(formatHeaderValue(actualHeader));

        if (!Objects.equals(actualHeader, normalizedActualHeader)) {
            message.append("<br/>").append("系统识别后：").append(formatHeaderValue(normalizedActualHeader));
        }

        message.append("<br/><br/>").append(String.format("期望列数：%d，实际列数：%d。", expectedHeadMap.size(), actualHeadMap.size())).append("<br/>")
                .append("请使用系统下载的导入模板，避免使用导出文件或手动修改表头。").append("<br/>").append("若看起来一致，请重点检查空格、全角/半角字符，或是否使用了导出文件回导。");
        return message.toString();
    }

    private String formatHeaderValue(String headerValue) {
        return headerValue == null ? "[空]" : String.format("[%s]", headerValue);
    }

}
