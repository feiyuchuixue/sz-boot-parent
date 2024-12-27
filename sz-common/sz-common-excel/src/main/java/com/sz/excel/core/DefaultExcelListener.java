package com.sz.excel.core;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelAnalysisException;
import cn.idev.excel.exception.ExcelDataConvertException;
import cn.idev.excel.metadata.CellExtra;
import com.sz.core.util.JsonUtils;
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
 * @ClassName DefaultExcelListener
 * @Author sz
 * @Date 2023/12/26 14:43
 * @Version 1.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {

    {
        validateHeader = Boolean.TRUE;
    }

    /**
     * 是否Validator检验，默认为是
     */
    private Boolean validateHeader;

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
            for (Field field : fields) {
                // 检查字段是否有@ExcelProperty注解
                if (field.isAnnotationPresent(ExcelProperty.class)) {
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    expectedHeadMap.put(expectedHeadMap.size(), excelProperty.value()[0]);
                }
            }
            if (headMap.isEmpty()) {
                throw new ExcelAnalysisException("无效的表头");
            } else if (!headMap.equals(expectedHeadMap)) {
                String expectedHeaders = String.join(", ", expectedHeadMap.values());
                String actualHeaders = String.join(", ", headMap.values());
                String errMsg = String.format("表头校验失败:<br/><br/>期望:<br/> [%s];<br/><br/>实际:<br/> [%s];", expectedHeaders, actualHeaders);
                throw new ExcelAnalysisException(errMsg);
            } else {
                log.debug("表头一致");
            }
        }
    }

    /**
     * 异常处理
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        String errMsg = null;
        if (exception instanceof ExcelDataConvertException excelDataConvertException) {
            // 如果是某一个单元格的转换异常 能获取到具体行号
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errMsg = String.format("第%d行-第%d列-表头 [%s]: 解析异常<br/>", rowIndex + 1, columnIndex + 1, headMap.get(columnIndex));
            log.error(errMsg);
        } else if (exception instanceof ConstraintViolationException constraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            String constraintViolationsMsg = "";
            if (constraintViolations != null && !constraintViolations.isEmpty()) {
                constraintViolationsMsg = constraintViolations.stream().map(ConstraintViolation::getMessage).filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
            }
            errMsg = String.format("第%d行数据校验异常: %s", context.readRowHolder().getRowIndex() + 1, constraintViolationsMsg);
            log.error(errMsg);
        } else {
            errMsg = exception.getMessage();
        }
        excelResult.getErrorList().add(errMsg);
        throw new ExcelAnalysisException(errMsg);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        super.extra(extra, context);
    }

    /**
     * 数据解析完毕
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return super.hasNext(context);
    }

}
