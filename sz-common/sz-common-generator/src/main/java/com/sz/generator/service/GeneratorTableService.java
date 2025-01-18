package com.sz.generator.service;

import com.mybatisflex.core.service.IService;
import com.sz.core.common.entity.PageResult;
import com.sz.generator.pojo.dto.DbTableQueryDTO;
import com.sz.generator.pojo.dto.ImportTableDTO;
import com.sz.generator.pojo.dto.SelectTablesDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.vo.GenCheckedInfoVO;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import com.sz.generator.pojo.vo.GeneratorPreviewVO;
import freemarker.template.Template;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 代码生成业务表 服务类
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
public interface GeneratorTableService extends IService<GeneratorTable> {

    void importTable(ImportTableDTO dto);

    PageResult<GeneratorTable> selectDbTableNotInImport(DbTableQueryDTO dto);

    PageResult<GeneratorTable> selectDbTableByImport(DbTableQueryDTO dto);

    GeneratorDetailVO detail(String tableName);

    void updateGeneratorSetting(GeneratorDetailVO generatorDetailVO);

    List<String> generator(String tableName) throws IOException;

    GenCheckedInfoVO checkDist(String tableName);

    byte[] downloadZip(SelectTablesDTO dto) throws IOException;

    List<GeneratorPreviewVO> preview(String tableName) throws IOException;

    Template getMenuSqlTemplate() throws IOException;

    @Transactional
    void remove(SelectTablesDTO dto);

    Template getDictSqlTemplate() throws IOException;
}
