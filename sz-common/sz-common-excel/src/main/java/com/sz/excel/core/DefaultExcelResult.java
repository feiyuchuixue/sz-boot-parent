package com.sz.excel.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DefaultExcelResult
 * @Author sz
 * @Date 2023/12/26 14:37
 * @Version 1.0
 */
@Data
public class DefaultExcelResult<T> implements ExcelResult<T> {

    // 数据list
    private List<T> list;

    // 错误信息列表
    private List<String> errorList;

    public DefaultExcelResult() {
        this.list = new ArrayList<>();
        this.errorList = new ArrayList<>();
    }

    public DefaultExcelResult(List<T> list, List<String> errorList) {
        this.list = list;
        this.errorList = errorList;
    }

    public DefaultExcelResult(ExcelResult<T> excelResult) {
        this.list = excelResult.getList();
        this.errorList = excelResult.getErrorList();
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
