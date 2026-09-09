package com.sz.admin.system.service.impl;

import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDictSourceMapper;
import com.sz.admin.system.mapper.SysDictTypeMapper;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.sz.admin.system.pojo.po.SysDictSource;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.vo.sysdict.SysDictTypeVO;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.core.common.dict.DictLoaderFactory;
import com.sz.core.common.dict.DictTypeService;
import com.sz.core.common.dict.DictTypeVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.platform.socket.SocketService;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.sz.admin.system.pojo.po.table.SysDictSourceTableDef.SYS_DICT_SOURCE;
import static com.sz.admin.system.pojo.po.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * <p>
 * 字典类型 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService, DictTypeService {

    private final DictLoaderFactory dictLoaderFactory;

    private final RedisCache redisCache;

    private final SocketService socketService;

    private final SysDictSourceMapper sysDictSourceMapper;

    @Override
    public void create(SysDictTypeAddDTO dto) {
        SysDictType sysDictType = BeanCopyUtils.copy(dto, SysDictType.class);
        SysDictSource sourceMeta = getSourceByCode(dto.getSourceCode());
        sysDictType.setId(allocateDictTypeId(sourceMeta));
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DICT_TYPE.TYPE_CODE.eq(dto.getTypeCode()));
        CommonResponseEnum.EXISTS.message("typeCode已存在").assertTrue(count(wrapper) > 0);
        save(sysDictType);
    }

    @Override
    public void update(SysDictTypeUpDTO dto) {
        SysDictType sysDictType = BeanCopyUtils.copy(dto, SysDictType.class);
        sysDictType.setId(dto.getId());
        SysDictType oldDetail = detail(dto.getId());
        if (Utils.isNotNull(dto.getSourceCode())) {
            SysDictSource sourceMeta = getSourceByCode(dto.getSourceCode());
            CommonResponseEnum.INVALID.message("字典来源不允许修改").assertTrue(!sourceMeta.getSourceCode().equals(oldDetail.getSourceCode()));
        }
        sysDictType.setSourceCode(oldDetail.getSourceCode());
        // 修改时的重复性效验需要排除本身
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DICT_TYPE.TYPE_CODE.eq(dto.getTypeCode())).where(SYS_DICT_TYPE.ID.ne(dto.getId()));
        CommonResponseEnum.EXISTS.message("typeCode已存在").assertTrue(count(wrapper) > 0);
        saveOrUpdate(sysDictType);
        redisCache.clearDict(oldDetail.getTypeCode()); // 清除redis缓存
        dictLoaderFactory.getDictByType(sysDictType.getTypeCode()); // 更新缓存
        socketService.syncDict();
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertNull(dto.getIds());
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DICT_TYPE.TYPE_CODE).where(SYS_DICT_TYPE.ID.in(dto.getIds()));
        List<String> typeCodes = listAs(wrapper, String.class);
        for (String typeCode : typeCodes) {
            redisCache.clearDict(typeCode); // 清除redis缓存
        }
        removeByIds(dto.getIds());
        socketService.syncDict();
    }

    @Override
    public SysDictType detail(Long id) {
        SysDictType dictType = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(dictType);
        return dictType;
    }

    @Override
    public PageResult<SysDictTypeVO> list(SysDictTypeListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getTypeName())) {
            wrapper.where(SYS_DICT_TYPE.TYPE_NAME.like(dto.getTypeName()));
        }
        if (Utils.isNotNull(dto.getTypeCode())) {
            wrapper.where(SYS_DICT_TYPE.TYPE_CODE.like(dto.getTypeCode()));
        }
        wrapper.orderBy(SYS_DICT_TYPE.CREATE_TIME.asc());
        Page<SysDictType> page = page(PageUtils.getPage(dto), wrapper);
        List<SysDictTypeVO> rows = page.getRecords().stream().map(this::toTypeVO).toList();
        return new PageResult<>(page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow(), rows);
    }

    @Override
    public List<DictTypeVO> findDictType() {
        QueryWrapper wrapper = QueryWrapper.create().orderBy(SYS_DICT_TYPE.CREATE_TIME.desc());
        return list(wrapper).stream().map(this::toCommonDictTypeVO).toList();
    }

    @Override
    public List<DictTypeVO> selectDictTypeOptions() {
        return dictLoaderFactory.getAllDictType();
    }

    private Long allocateDictTypeId(SysDictSource sourceMeta) {
        AtomicReference<Long> maxId = new AtomicReference<>(0L);
        LogicDeleteManager.execWithoutLogicDelete(() -> {
            Long count = QueryChain.of(SysDictType.class).select(QueryMethods.max(SYS_DICT_TYPE.ID)).from(SYS_DICT_TYPE)
                    .where(SYS_DICT_TYPE.ID.ge(sourceMeta.getStartId())).and(SYS_DICT_TYPE.ID.le(sourceMeta.getEndId())).oneAs(Long.class);
            maxId.set(count);
        });
        long nextId = Utils.isNotNull(maxId.get()) ? maxId.get() + 1 : sourceMeta.getStartId();
        CommonResponseEnum.INVALID.message("字典来源号段已满").assertTrue(nextId > sourceMeta.getEndId());
        return nextId;
    }

    private SysDictSource resolveSourceMeta(SysDictType dictType) {
        return getSourceByCode(dictType.getSourceCode());
    }

    private SysDictTypeVO toTypeVO(SysDictType dictType) {
        SysDictSource sourceMeta = resolveSourceMeta(dictType);
        SysDictTypeVO vo = BeanCopyUtils.copy(dictType, SysDictTypeVO.class);
        if (Utils.isNotNull(dictType.getIsLock())) {
            vo.setIsLock(dictType.getIsLock().getCode());
        }
        if (Utils.isNotNull(dictType.getIsShow())) {
            vo.setIsShow(dictType.getIsShow().getCode());
        }
        vo.setSourceCode(sourceMeta.getSourceCode());
        vo.setSourceName(sourceMeta.getSourceName());
        vo.setSourceRange(sourceMeta.getStartId() + " - " + sourceMeta.getEndId());
        return vo;
    }

    private DictTypeVO toCommonDictTypeVO(SysDictType dictType) {
        SysDictSource sourceMeta = resolveSourceMeta(dictType);
        return DictTypeVO.builder().typeCode(dictType.getTypeCode()).typeName(dictType.getTypeName()).sourceCode(sourceMeta.getSourceCode())
                .sourceName(sourceMeta.getSourceName()).isDynamic(false).build();
    }

    private SysDictSource getSourceByCode(String sourceCode) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DICT_SOURCE.SOURCE_CODE.eq(sourceCode));
        SysDictSource source = sysDictSourceMapper.selectOneByQuery(wrapper);
        CommonResponseEnum.INVALID.message("无效的字典来源").assertNull(source);
        return source;
    }

}
