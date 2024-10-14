package com.sz.generator.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.generator.mapper.GeneratorTableColumnMapper;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import com.sz.generator.pojo.po.table.GeneratorTableColumnTableDef;
import com.sz.generator.pojo.po.table.GeneratorTableTableDef;
import com.sz.generator.service.GeneratorTableColumnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
@Service
public class GeneratorTableColumnServiceImpl extends ServiceImpl<GeneratorTableColumnMapper, GeneratorTableColumn> implements GeneratorTableColumnService {

    @Override
    public void batchInsert(List<GeneratorTableColumn> tableColumns) {
        saveBatch(tableColumns);
    }

    @Override
    public List<GeneratorTableColumn> getTableColumnsByTableId(Long tableId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(GeneratorTableColumn::getTableId, tableId).orderBy(GeneratorTableColumn::getSort).asc();
        List<GeneratorTableColumn> list = list(wrapper);
        return list;
    }

    @Override
    public List<GeneratorTableColumn> getTableColumnsByTableName(Long tableId) {
        List<GeneratorTableColumn> list = QueryChain.of(mapper).eq(GeneratorTableColumn::getTableId, tableId).orderBy(GeneratorTableColumn::getSort).asc()
                .list();
        return list;
    }

    @Override
    public void updateBatchTableColumns(List<GeneratorTableColumn> columns) {
        updateBatch(columns);
    }

    @Override
    public void remove(List tableNames) {
        this.updateChain().from(GeneratorTableTableDef.GENERATOR_TABLE, GeneratorTableColumnTableDef.GENERATOR_TABLE_COLUMN)
                .where(GeneratorTableColumnTableDef.GENERATOR_TABLE_COLUMN.TABLE_ID.eq(GeneratorTableTableDef.GENERATOR_TABLE.TABLE_ID))
                .where(GeneratorTableTableDef.GENERATOR_TABLE.TABLE_NAME.in(tableNames)).remove();
    }

}
