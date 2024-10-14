package com.sz.generator.service;

import com.mybatisflex.core.service.IService;
import com.sz.generator.pojo.po.GeneratorTableColumn;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 服务类
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
public interface GeneratorTableColumnService extends IService<GeneratorTableColumn> {

    void batchInsert(List<GeneratorTableColumn> tableColumns);

    List<GeneratorTableColumn> getTableColumnsByTableId(Long tableId);

    List<GeneratorTableColumn> getTableColumnsByTableName(Long tableId);

    void updateBatchTableColumns(List<GeneratorTableColumn> columns);

    void remove(List tableNames);
}
