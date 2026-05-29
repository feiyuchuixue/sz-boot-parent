package com.sz.admin.system.service;

import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceUpdateDTO;
import com.sz.admin.system.pojo.vo.sysdict.DictSourceVO;
import com.sz.admin.system.pojo.vo.sysdict.SysDictSourceOptionVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * 字典来源服务
 */
public interface SysDictSourceService {

    PageResult<DictSourceVO> page(SysDictSourceListDTO dto);

    List<SysDictSourceOptionVO> listOptions();

    void create(SysDictSourceCreateDTO dto);

    void update(SysDictSourceUpdateDTO dto);

    void remove(SelectIdsDTO dto);
}
