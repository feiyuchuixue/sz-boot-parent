package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysdict.SysDictAddDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictQueryDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.core.common.entity.DictCustomVO;
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

    void create(SysDictAddDTO dto);

    void update(SysDictUpDTO dto);

    void remove(SelectIdsDTO dto);

    PageResult<SysDict> list(SysDictQueryDTO dto);

    Map<String, List<DictCustomVO>> dictList(String typeCode);

    Map<String, List<DictCustomVO>> dictAll();

    List<DictCustomVO> getDictByType(String typeCode);
}
