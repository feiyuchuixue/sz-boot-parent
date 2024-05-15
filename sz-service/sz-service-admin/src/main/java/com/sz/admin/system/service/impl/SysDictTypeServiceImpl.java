package com.sz.admin.system.service.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDictTypeMapper;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.po.table.SysDictTypeTableDef;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 字典类型 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {


    @Override
    public void create(SysDictTypeAddDTO dto) {
        SysDictType sysDictType = BeanCopyUtils.springCopy(dto, SysDictType.class);
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SysDictTypeTableDef.SYS_DICT_TYPE.TYPE_CODE.eq(dto.getTypeCode()));
        CommonResponseEnum.EXISTS.message("typeCode已存在").assertTrue(count(wrapper) > 0);
        save(sysDictType);
    }

    @Override
    public void update(SysDictTypeUpDTO dto) {
        SysDictType sysDictType = BeanCopyUtils.springCopy(dto, SysDictType.class);
        sysDictType.setId(dto.getId());
        // 修改时的重复性效验需要排除本身
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SysDictTypeTableDef.SYS_DICT_TYPE.TYPE_CODE.eq(dto.getTypeCode()))
                .where(SysDictTypeTableDef.SYS_DICT_TYPE.ID.ne(dto.getId()));
        CommonResponseEnum.EXISTS.message("typeCode已存在").assertTrue(count(wrapper) > 0);
        saveOrUpdate(sysDictType);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        removeById((Serializable) dto.getIds());
    }

    @Override
    public SysDictType detail(Long id) {
        SysDictType dictType = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(dictType);
        return dictType;
    }

    @Override
    public PageResult<SysDictType> list(SysDictTypeListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getTypeName())) {
            wrapper.where(SysDictTypeTableDef.SYS_DICT_TYPE.TYPE_NAME.like(dto.getTypeName()));
        }
        if (Utils.isNotNull(dto.getTypeCode())) {
            wrapper.where(SysDictTypeTableDef.SYS_DICT_TYPE.TYPE_CODE.like(dto.getTypeCode()));
        }
        wrapper.orderBy(SysDictTypeTableDef.SYS_DICT_TYPE.CREATE_TIME.asc());
        Page<SysDictType> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysDictType> selectOptionsType() {
        QueryWrapper wrapper = QueryWrapper.create().orderBy(SysDictTypeTableDef.SYS_DICT_TYPE.CREATE_TIME.desc());
        return list(wrapper);
    }


}
