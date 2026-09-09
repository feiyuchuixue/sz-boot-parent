package com.sz.excel.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sz
 * @since 2023/12/26 14:37
 */
@Data
public class DefaultExcelResult<T> implements ExcelResult<T> {

    // 数据list
    private List<T> list;

    // 错误信息列表
    private List<String> errorList;

    // 结构化失败行列表
    private List<ExcelFailRow<T>> failRowList;

    public DefaultExcelResult() {
        this.list = new ArrayList<>();
        this.errorList = new ArrayList<>();
        this.failRowList = new ArrayList<>();
    }

    public DefaultExcelResult(List<T> list, List<String> errorList, List<ExcelFailRow<T>> failRowList) {
        this.list = list;
        this.errorList = errorList;
        this.failRowList = failRowList;
    }

    public DefaultExcelResult(ExcelResult<T> excelResult) {
        this.list = excelResult.getList();
        this.errorList = excelResult.getErrorList();
        this.failRowList = excelResult.getFailRowList();
    }

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public List<String> getErrorList() {
        return errorList;
    }

    @Override
    public List<ExcelFailRow<T>> getFailRowList() {
        return failRowList;
    }

    @Override
    public String getAnalysis() {
        int successCount = list.size();
        int errorCount = errorList.size();
        if (successCount == 0) {
            return "读取失败，未解析到数据";
        } else {
            if (errorCount == 0) {
                return String.format("恭喜您，全部读取成功！共%d条", successCount);
            } else {
                return "";
            }
        }
    }
}
