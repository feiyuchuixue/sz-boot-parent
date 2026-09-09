package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.core.common.entity.DictVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
public interface SysDictMapper extends BaseMapper<SysDict> {

    List<DictVO> listDict(@Param("typeCode") String typeCode);

}
