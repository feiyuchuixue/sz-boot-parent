package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDictSourceMapper;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceUpdateDTO;
import com.sz.admin.system.pojo.po.SysDictSource;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.vo.sysdict.DictSourceVO;
import com.sz.admin.system.pojo.vo.sysdict.SysDictSourceOptionVO;
import com.sz.admin.system.service.SysDictSourceService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.sz.admin.system.pojo.po.table.SysDictSourceTableDef.SYS_DICT_SOURCE;

@Service
@RequiredArgsConstructor
public class SysDictSourceServiceImpl extends ServiceImpl<SysDictSourceMapper, SysDictSource> implements SysDictSourceService {

    @Override
    public PageResult<DictSourceVO> page(SysDictSourceListDTO dto) {
        Page<DictSourceVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), DictSourceVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysDictSourceOptionVO> listOptions() {
        return listAs(QueryWrapper.create(), SysDictSourceOptionVO.class);
    }

    @Override
    public void create(SysDictSourceCreateDTO dto) {
        CommonResponseEnum.INVALID.message("起始ID不能大于结束ID").assertTrue(dto.getStartId() > dto.getEndId());
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DICT_SOURCE.SOURCE_CODE.eq(dto.getSourceCode()));
        CommonResponseEnum.EXISTS.message("来源编码已存在").assertTrue(count(wrapper) > 0);
        validateRange(dto.getStartId(), dto.getEndId(), null);
        validateTypeOccupy(dto.getStartId(), dto.getEndId(), null);
        save(BeanCopyUtils.copy(dto, SysDictSource.class));
    }

    @Override
    public void update(SysDictSourceUpdateDTO dto) {
        SysDictSource old = getById(dto.getId());
        CommonResponseEnum.INVALID_ID.assertNull(old);
        CommonResponseEnum.INVALID.message("起始ID不能大于结束ID").assertTrue(dto.getStartId() > dto.getEndId());
        validateRange(dto.getStartId(), dto.getEndId(), dto.getId());
        validateTypeOccupy(dto.getStartId(), dto.getEndId(), dto.getId());
        SysDictSource source = BeanCopyUtils.copy(dto, SysDictSource.class);
        source.setSourceCode(old.getSourceCode());
        saveOrUpdate(source);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        List<SysDictSource> sources = listByIds(dto.getIds());
        for (SysDictSource source : sources) {
            long dictTypeCount = QueryChain.of(SysDictType.class).where(SysDictType::getSourceCode).eq(source.getSourceCode()).count();
            CommonResponseEnum.INVALID.message(String.format("来源「%s」下存在 %d 个字典类型，无法自动删除，请手动处理后再删除", source.getSourceName(), dictTypeCount))
                    .assertTrue(dictTypeCount > 0);
        }
        removeByIds(dto.getIds());
    }

    private static QueryWrapper buildQueryWrapper(SysDictSourceListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getSourceCode())) {
            wrapper.where(SYS_DICT_SOURCE.SOURCE_CODE.like(dto.getSourceCode()));
        }
        if (Utils.isNotNull(dto.getSourceName())) {
            wrapper.where(SYS_DICT_SOURCE.SOURCE_NAME.like(dto.getSourceName()));
        }
        wrapper.orderBy(SYS_DICT_SOURCE.CREATE_TIME.asc());
        return wrapper;
    }

    private void validateRange(Long startId, Long endId, Long excludeId) {
        List<SysDictSource> sources = list();
        for (SysDictSource source : sources) {
            if (excludeId != null && excludeId.equals(source.getId())) {
                continue;
            }
            boolean overlap = !(endId < source.getStartId() || startId > source.getEndId());
            CommonResponseEnum.INVALID.message("来源区间与现有来源区间重叠").assertTrue(overlap);
        }
    }

    private void validateTypeOccupy(Long startId, Long endId, Long excludeSourceId) {
        if (excludeSourceId == null) {
            long occupiedCount = QueryChain.of(SysDictType.class).list().stream().filter(item -> item.getId() >= startId && item.getId() <= endId).count();
            CommonResponseEnum.INVALID.message("来源区间已被现有字典类型占用").assertTrue(occupiedCount > 0);
            return;
        }

        SysDictSource old = getById(excludeSourceId);
        CommonResponseEnum.INVALID_ID.assertNull(old);
        long outOfRangeCount = QueryChain.of(SysDictType.class).list().stream().filter(item -> Objects.equals(old.getSourceCode(), item.getSourceCode()))
                .filter(item -> item.getId() < startId || item.getId() > endId).count();
        CommonResponseEnum.INVALID.message("来源区间外仍存在已绑定的字典类型，无法缩小或移动该区间").assertTrue(outOfRangeCount > 0);
    }
}
