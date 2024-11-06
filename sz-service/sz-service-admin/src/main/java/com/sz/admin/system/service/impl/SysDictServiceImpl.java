package com.sz.admin.system.service.impl;

import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDictMapper;
import com.sz.admin.system.mapper.SysDictTypeMapper;
import com.sz.admin.system.pojo.dto.sysdict.SysDictCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpdateDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.po.table.SysDictTableDef;
import com.sz.admin.system.service.SysDictService;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.core.common.entity.DictVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.service.DictService;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.StreamUtils;
import com.sz.core.util.Utils;
import com.sz.generator.service.GeneratorTableService;
import com.sz.platform.factory.DictLoaderFactory;
import com.sz.redis.RedisCache;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysDictTypeTableDef.SYS_DICT_TYPE;

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

    private final GeneratorTableService generatorTableService;

    private final DictLoaderFactory dictLoaderFactory;

    private static Long generateCustomId(Long firstPart, int secondPart) {
        secondPart += 1;
        String paddedFirstPart = String.format("%04d", firstPart); // 格式化为四位数字，不足补零
        String paddedSecondPart = String.format("%03d", secondPart); // 格式化为三位数字，不足补零
        String part = paddedFirstPart + paddedSecondPart;
        return Long.valueOf(part);
    }

    @Override
    public void create(SysDictCreateDTO dto) {
        SysDict sysDict = BeanCopyUtils.springCopy(dto, SysDict.class);
        QueryWrapper wrapper;
        long count = QueryChain.of(sysDictTypeMapper).eq(SysDictType::getId, dto.getSysDictTypeId()).count();
        CommonResponseEnum.NOT_EXISTS.message("SYS_DICT_TYPE不存在").assertTrue(count < 1);

        wrapper = QueryWrapper.create().where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()))
                .where(SysDictTableDef.SYS_DICT.CODE_NAME.eq(dto.getCodeName()));
        CommonResponseEnum.EXISTS.message("字典已存在").assertTrue(count(wrapper) > 0);

        wrapper = QueryWrapper.create().where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()));
        AtomicReference<Long> dictCount = new AtomicReference<>(0L);
        QueryWrapper finalWrapper = wrapper;
        // 跳过逻辑删除，查询真实count数
        LogicDeleteManager.execWithoutLogicDelete(() -> {
            dictCount.set(count(finalWrapper));
        });
        Long generateCustomId = generateCustomId(dto.getSysDictTypeId(), dictCount.get().intValue());
        sysDict.setId(generateCustomId);
        save(sysDict);
        upCache(dto.getSysDictTypeId());
    }

    @Override
    public void update(SysDictUpdateDTO dto) {
        SysDict sysDict = BeanCopyUtils.springCopy(dto, SysDict.class);
        long count = QueryChain.of(this.mapper).select().from(SysDictTableDef.SYS_DICT).where(SysDictTableDef.SYS_DICT.ID.ne(dto.getId()))
                .and(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId())).and(SysDictTableDef.SYS_DICT.CODE_NAME.eq(dto.getCodeName()))
                .count();
        CommonResponseEnum.EXISTS.message(SysDictTableDef.SYS_DICT.CODE_NAME.getName() + "已存在").assertTrue(count > 0);
        sysDict.setId(dto.getId());
        saveOrUpdate(sysDict);
        upCache(dto.getSysDictTypeId());
    }

    private void upCache(Long dictTypeId) {
        SysDictType sysDictType = sysDictTypeService.detail(dictTypeId);
        String typeCode = sysDictType.getTypeCode();
        redisCache.clearDict(typeCode); // 清除redis缓存
        dictLoaderFactory.getDictByType(typeCode); // 更新缓存
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SysDictTableDef.SYS_DICT.ID.in(dto.getIds()));
        List<SysDict> list = list(wrapper);
        for (SysDict sysDict : list) {
            SysDictType sysDictType = sysDictTypeService.detail(sysDict.getSysDictTypeId());
            redisCache.clearDict(sysDictType.getTypeCode()); // 清除redis缓存
        }
        removeByIds(dto.getIds());
    }

    @Override
    public PageResult<SysDict> list(SysDictListDTO dto) {
        QueryChain<SysDict> chain = QueryChain.of(this.mapper).select().from(SysDictTableDef.SYS_DICT)
                .where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()));
        if (Utils.isNotNull(dto.getCodeName())) {
            chain.and(SysDictTableDef.SYS_DICT.CODE_NAME.like(dto.getCodeName()));
        }
        Page<SysDict> page = chain.orderBy(SysDictTableDef.SYS_DICT.SORT.asc()).page(PageUtils.getPage(dto));
        return PageUtils.getPageResult(page);
    }

    @Override
    public Map<String, List<DictVO>> dictList(String typeCode) {
        Map<String, List<DictVO>> result = new HashMap<>();
        Map<String, List<DictVO>> allDict = dictLoaderFactory.loadAllDict();
        if (allDict.containsKey(typeCode)) {
            return dictLoaderFactory.loadAllDict();
        }
        // 如果查询不到，从数据库中获取，并赋值
        List<DictVO> dictVOS = this.mapper.listDict(typeCode);
        if (!dictVOS.isEmpty()) {
            redisCache.setDict(typeCode, dictVOS);
            result = dictVOS.stream().collect(Collectors.groupingBy(DictVO::getSysDictTypeCode, LinkedHashMap::new, Collectors.toList()));
        }
        return result;
    }

    @Override
    public Map<String, List<DictVO>> dictAll() {
        return dictLoaderFactory.loadAllDict();
    }

    @Override
    public List<DictVO> getDictByType(String typeCode) {
        List<DictVO> dictVOS = new ArrayList<>();
        if (redisCache.hasHashKey(typeCode)) {
            dictVOS = redisCache.getDictByType(typeCode);
        } else {
            dictVOS = this.mapper.listDict(typeCode);
            if (Utils.isNotNull(dictVOS)) {
                redisCache.setDict(typeCode, dictVOS);
            }
        }
        return dictVOS;
    }

    @Override
    public String getDictLabel(String dictType, String dictValue, String separator) {
        List<DictVO> dictLists = dictLoaderFactory.getDictByType(dictType);
        Map<String, String> map = StreamUtils.toMap(dictLists, DictVO::getId, vo -> vo.getCodeName() != null ? vo.getCodeName() : ""); // {"1000003":"禁言","1000002":"禁用","1000001":"正常"}
        return map.getOrDefault(dictValue, "");
    }

    @Override
    public String getDictValue(String dictType, String dictLabel, String separator) {
        List<DictVO> dictLists = dictLoaderFactory.getDictByType(dictType);
        Map<String, String> map = StreamUtils.toMap(dictLists, DictVO::getCodeName, DictVO::getId); // {"禁言":"1000003","禁用":"1000002","正常":"1000001"}
        return map.getOrDefault(dictLabel, "");
    }

    @Override
    public Map<String, String> getAllDictByType(String dictType) {
        List<DictVO> dictLists = dictLoaderFactory.getDictByType(dictType);
        if (dictLists.isEmpty()) {
            return new HashMap<>();
        }
        return StreamUtils.toMap(dictLists, DictVO::getCodeName, DictVO::getId);
    }

    @Override
    public Map<String, List<DictVO>> getAllDict() {
        return dictLoaderFactory.loadAllDict();
    }

    @SneakyThrows
    @Override
    public String exportDictSql(SelectIdsDTO dto) {
        String generatedContent = "";
        if (Utils.isNotNull(dto.getIds())) {
            List<SysDictType> dictTypeList = QueryChain.of(SysDictType.class).where(SYS_DICT_TYPE.ID.in(dto.getIds())).list();

            QueryWrapper queryWrapper = QueryWrapper.create().in(SysDict::getSysDictTypeId, dto.getIds()).orderBy(SysDict::getSysDictTypeId).asc()
                    .orderBy(SysDict::getSort).asc();
            List<SysDict> dictList = list(queryWrapper);
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("dictTypeList", dictTypeList);
            dataModel.put("dictList", dictList);
            Template template = generatorTableService.getDictSqlTemplate();
            try (StringWriter writer = new StringWriter()) {
                template.process(dataModel, writer);
                generatedContent = writer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return generatedContent;
    }

}
