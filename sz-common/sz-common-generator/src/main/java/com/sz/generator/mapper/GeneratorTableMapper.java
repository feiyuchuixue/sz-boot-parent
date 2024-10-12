package com.sz.generator.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.generator.pojo.dto.DbTableQueryDTO;
import com.sz.generator.pojo.dto.MenuCreateDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.result.SysMenuResult;
import com.sz.generator.pojo.result.TableColumResult;
import com.sz.generator.pojo.result.TableResult;
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

    List<TableResult> selectDbTableListByNames(@Param("tableNames") List<String> tableNames);

    /**
     * 查询指定table的column列
     *
     * @param tableName
     * @return
     */
    List<TableColumResult> selectDbTableColumnsByName(@Param("tableName") String tableName);

    /**
     * 根据tableName清空表记录
     *
     * @param tableNames
     */
    void cleanTableRecordByTableName(@Param("tableNames") List<String> tableNames);

    /**
     * 根据tableName清空字段表
     *
     * @param tableNames
     */
    void cleanTableColumnByTableName(@Param("tableNames") List<String> tableNames);

    /**
     * 查询未导入的表
     *
     * @return
     */
    List<GeneratorTable> selectDbTableNotInImport(@Param("queryDTO") DbTableQueryDTO queryDTO);

    /**
     * 查询已经导入的表
     *
     * @param queryDTO
     * @return
     */
    List<GeneratorTable> selectDbTableByImport(@Param("queryDTO") DbTableQueryDTO queryDTO);

    /**
     * 根据pid 查询上级菜单
     *
     * @param pid
     * @return
     */
    SysMenuResult selectSysMenuByPid(@Param("pid") String pid);

    void insertMenu(@Param("createDTO") MenuCreateDTO createDTO);

    int selectMenuCount(@Param("pid") String pid);

    void syncTreeHasChildren();

    void syncTreeDeep();

    /**
     * 菜单唯一性校验
     *
     * @param name
     * @param path
     * @param component
     * @return
     */
    int countMenu(@Param("name") String name, @Param("path") String path, @Param("component") String component, @Param("pid") String pid);

    /**
     * 按钮唯一性校验
     *
     * @param permissions
     * @return
     */
    int countMenuBtn(@Param("permissions") String permissions);

}
