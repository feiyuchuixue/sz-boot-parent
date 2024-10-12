package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: sz
 * @date: 2022/8/25 10:25
 * @description: 分页对象
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页")
    private long current;

    @Schema(description = "每页条数")
    private long limit;

    @Schema(description = "总页数")
    private long totalPage;

    @Schema(description = "总条数")
    private long total;

    @Schema(description = "结果集")
    private List<T> rows;

    @Schema(description = "额外参数")
    private Object param;

    public PageResult(long current, long limit, long totalPage, long total, List<T> rows, Object param) {
        this.current = current;
        this.limit = limit;
        this.totalPage = totalPage;
        this.total = total;
        this.rows = rows;
        this.param = param;
    }
}
