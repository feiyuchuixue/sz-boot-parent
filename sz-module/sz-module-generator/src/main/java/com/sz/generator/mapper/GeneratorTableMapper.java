package com.sz.generator.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.generator.pojo.dto.DbTableQueryDTO;
import com.sz.generator.pojo.dto.MenuCreateDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.result.SysMenuResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代码生成业务表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
public interface GeneratorTableMapper extends BaseMapper<GeneratorTable> {

    /**
     * 根据 tableName 清空表记录
     *
     * @param tableNames
     *            表名集合
     */
    void cleanTableRecordByTableName(@Param("tableNames") List<String> tableNames);

    /**
     * 根据 tableName 清空字段表
     *
     * @param tableNames
     *            表名集合
     */
    void cleanTableColumnByTableName(@Param("tableNames") List<String> tableNames);

    /**
     * 查询已经导入的表
     *
     * @param queryDTO
     *            查询条件
     * @return 表信息
     */
    List<GeneratorTable> selectDbTableByImport(@Param("queryDTO") DbTableQueryDTO queryDTO);

    /**
     * 根据 pid 查询上级菜单
     *
     * @param pid
     *            上级菜单 id
     * @return 菜单信息
     */
    SysMenuResult selectSysMenuByPid(@Param("pid") Long pid);

    void insertMenu(@Param("createDTO") MenuCreateDTO createDTO);

    int selectMenuCount(@Param("pid") Long pid);

    List<Long> selectEnabledMenuIds();

    List<Long> selectEnabledMenuParentIds();

    void updateMenuHasChildren(@Param("id") Long id, @Param("hasChildren") String hasChildren);

    int countMenu(@Param("name") String name, @Param("path") String path, @Param("component") String component, @Param("pid") Long pid);

    int countMenuBtn(@Param("permissions") String permissions);

}
