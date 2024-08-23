package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.vo.sysdict.DictTypeVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * <p>
 * 字典类型 服务类
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
public interface SysDictTypeService extends IService<SysDictType> {

    void create(SysDictTypeAddDTO dto);

    void update(SysDictTypeUpDTO dto);

    void remove(SelectIdsDTO dto);

    SysDictType detail(Long id);

    PageResult<SysDictType> list(SysDictTypeListDTO dto);

    List<DictTypeVO> findDictType();

    List<DictTypeVO> selectDictTypeOptions();
}
