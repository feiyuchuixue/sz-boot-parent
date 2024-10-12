package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysdict.SysDictCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpdateDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.core.common.entity.DictVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
public interface SysDictService extends IService<SysDict> {

    void create(SysDictCreateDTO dto);

    void update(SysDictUpdateDTO dto);

    void remove(SelectIdsDTO dto);

    PageResult<SysDict> list(SysDictListDTO dto);

    Map<String, List<DictVO>> dictList(String typeCode);

    Map<String, List<DictVO>> dictAll();

    List<DictVO> getDictByType(String typeCode);

    String exportDictSql(SelectIdsDTO dto);
}
