package com.sz.admin.system.service.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDictMapper;
import com.sz.admin.system.mapper.SysDictTypeMapper;
import com.sz.admin.system.pojo.dto.sysdict.SysDictAddDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictQueryDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.po.table.SysDictTableDef;
import com.sz.admin.system.pojo.vo.sysdict.DictVO;
import com.sz.admin.system.service.SysDictService;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.core.common.entity.DictCustomVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.service.DictService;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.StreamUtils;
import com.sz.core.util.Utils;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService, DictService {

    private final SysDictTypeMapper sysDictTypeMapper;

    private final SysDictTypeService sysDictTypeService;

    private final RedisCache redisCache;

    private static Long generateCustomId(Long firstPart, int secondPart) {
        secondPart += 1;
        String paddedFirstPart = String.format("%04d", firstPart); // 格式化为四位数字，不足补零
        String paddedSecondPart = String.format("%03d", secondPart); // 格式化为三位数字，不足补零
        String part = paddedFirstPart + paddedSecondPart;
        return Long.valueOf(part);
    }

    @Override
    public void create(SysDictAddDTO dto) {
        SysDict sysDict = BeanCopyUtils.springCopy(dto, SysDict.class);
        QueryWrapper wrapper;
        long count = QueryChain.of(sysDictTypeMapper)
                .eq(SysDictType::getId, dto.getSysDictTypeId())
                .count();
        CommonResponseEnum.INVALID_ID.message("SYS_DICT_TYPE不存在").assertTrue(count < 1);

        wrapper = QueryWrapper.create()
                .where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()))
                .where(SysDictTableDef.SYS_DICT.CODE_NAME.eq(dto.getCodeName()));
        CommonResponseEnum.EXISTS.assertTrue(count(wrapper) > 0);
        SysDictType sysDictType = sysDictTypeService.detail(dto.getSysDictTypeId());
        String typeCode = sysDictType.getTypeCode();
        wrapper = QueryWrapper.create()
                .where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()));
        Long dictCount = count(wrapper);
        Long generateCustomId = generateCustomId(dto.getSysDictTypeId(), dictCount.intValue());
        sysDict.setId(generateCustomId);
        save(sysDict);
        redisCache.clearDict(typeCode); // 清除redis缓存
    }

    @Override
    public void update(SysDictUpDTO dto) {
        SysDict sysDict = BeanCopyUtils.springCopy(dto, SysDict.class);
        long count = QueryChain.of(this.mapper)
                .select()
                .from(SysDictTableDef.SYS_DICT)
                .where(SysDictTableDef.SYS_DICT.ID.ne(dto.getId()))
                .and(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()))
                .and(SysDictTableDef.SYS_DICT.CODE_NAME.eq(dto.getCodeName()))
                .count();
        CommonResponseEnum.EXISTS.message(SysDictTableDef.SYS_DICT.CODE_NAME.getName() + "已存在").assertTrue(count > 0);
        sysDict.setId(dto.getId());
        saveOrUpdate(sysDict);

        SysDictType sysDictType = sysDictTypeService.detail(dto.getSysDictTypeId());
        String typeCode = sysDictType.getTypeCode();
        redisCache.clearDict(typeCode); // 清除redis缓存
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SysDictTableDef.SYS_DICT.ID.in(dto.getIds()));
        List<SysDict> list = list(wrapper);
        for (SysDict sysDict : list) {
            SysDictType sysDictType = sysDictTypeService.detail(sysDict.getSysDictTypeId());
            redisCache.clearDict(sysDictType.getTypeCode()); // 清除redis缓存
        }
        removeById((Serializable) dto.getIds());
    }

    @Override
    public PageResult<SysDict> list(SysDictQueryDTO dto) {
        QueryChain<SysDict> chain = QueryChain.of(this.mapper)
                .select()
                .from(SysDictTableDef.SYS_DICT)
                .where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()));
        if (Utils.isNotNull(dto.getCodeName())) {
            chain.and(SysDictTableDef.SYS_DICT.CODE_NAME.like(dto.getCodeName()));
        }
        Page<SysDict> page = chain.orderBy(SysDictTableDef.SYS_DICT.SORT.asc()).page(PageUtils.getPage(dto));
        return PageUtils.getPageResult(page);
    }

    @Override
    public Map<String, List<DictCustomVO>> dictList(String typeCode) {
        Map<String, List<DictCustomVO>> dictMap = new HashMap<>();
        if (!Utils.isNotNull(typeCode)) {
            return dictMap;
        }
        if (redisCache.hasKey() && redisCache.hasHashKey(typeCode)) {
            List<DictCustomVO> dictVOs = redisCache.getDictByType(typeCode);
            dictMap = dictVOs.stream()
                    .collect(Collectors.groupingBy(
                            DictCustomVO::getSysDictTypeCode,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));
        } else {
            List<DictVO> dictVOS = this.mapper.listDict(typeCode);
            List<DictCustomVO> dictCustomVOS = BeanCopyUtils.copyList(dictVOS, DictCustomVO.class);
            if (dictVOS.size() > 0) {
                redisCache.setDict(typeCode, dictCustomVOS);
            }
            dictMap = dictCustomVOS.stream()
                    .collect(Collectors.groupingBy(
                            DictCustomVO::getSysDictTypeCode,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));
        }
        return dictMap;
    }

    @Override
    public Map<String, List<DictCustomVO>> dictAll() {
        List<DictVO> dictVOS = this.mapper.listDict("");
        Map<String, List<DictCustomVO>> dictMap = dictVOS.stream()
                .collect(Collectors.groupingBy(DictVO::getSysDictTypeCode,
                        LinkedHashMap::new, // 使用 LinkedHashMap 作为分组的容器,有序解决乱序问题
                        Collectors.mapping(dictVO -> BeanCopyUtils.copy(dictVO, DictCustomVO.class), Collectors.toList())));
        redisCache.putAllDict(dictMap);
        return dictMap;
    }

    @Override
    public List<DictCustomVO> getDictByType(String typeCode) {
        List<DictCustomVO> dictVOS = new ArrayList<>();
        if (redisCache.hasHashKey(typeCode)) {
            dictVOS = redisCache.getDictByType(typeCode);
        } else {
            List<DictVO> voList = this.mapper.listDict(typeCode);
            dictVOS = BeanCopyUtils.copyList(voList, DictCustomVO.class);
            if (Utils.isNotNull(dictVOS)) {
                redisCache.setDict(typeCode, dictVOS);
            }
        }
        return dictVOS;
    }

    @Override
    public String getDictLabel(String dictType, String dictValue, String separator) {
        Map<String, List<DictCustomVO>> dictMap = dictList(dictType);
        if (dictMap.containsKey(dictType)) {
            List<DictCustomVO> dictLists = dictMap.get(dictType);
            Map<String, String> map = StreamUtils.toMap(dictLists, DictCustomVO::getId, DictCustomVO::getCodeName); // {"1000003":"禁言","1000002":"禁用","1000001":"正常"}
            return map.getOrDefault(dictValue, "");
        }
        return "";
    }

    @Override
    public String getDictValue(String dictType, String dictLabel, String separator) {
        Map<String, List<DictCustomVO>> dictMap = dictList(dictType);
        if (dictMap.containsKey(dictType)) {
            List<DictCustomVO> dictLists = dictMap.get(dictType);
            Map<String, String> map = StreamUtils.toMap(dictLists, DictCustomVO::getCodeName, DictCustomVO::getId); // {"禁言":"1000003","禁用":"1000002","正常":"1000001"}
            return map.getOrDefault(dictLabel, "");
        }
        return "";
    }

    @Override
    public Map<String, String> getAllDict(String dictType) {
        Map<String, List<DictCustomVO>> dictMap = dictList(dictType);
        if (dictMap.containsKey(dictType)) {
            List<DictCustomVO> dictLists = dictMap.get(dictType);
            return StreamUtils.toMap(dictLists, DictCustomVO::getCodeName, DictCustomVO::getId);
        }
        return new HashMap<>();
    }
}
