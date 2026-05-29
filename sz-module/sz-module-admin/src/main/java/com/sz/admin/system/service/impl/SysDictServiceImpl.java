package com.sz.admin.system.service.impl;

import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDictMapper;
import com.sz.admin.system.mapper.SysDictTypeMapper;
import com.sz.admin.system.pojo.dto.scriptexport.ScriptExportDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpdateDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.po.table.SysDictTableDef;
import com.sz.admin.system.pojo.vo.scriptexport.ScriptExportVO;
import com.sz.admin.system.script.AdminScriptExportService;
import com.sz.admin.system.service.SysDictService;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.core.common.dict.DictLoaderFactory;
import com.sz.core.common.dict.DictService;
import com.sz.core.common.entity.DictVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.StreamUtils;
import com.sz.core.util.Utils;
import com.sz.platform.socket.SocketService;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService, DictService {

    private final SysDictTypeMapper sysDictTypeMapper;

    private final SysDictTypeService sysDictTypeService;

    private final RedisCache redisCache;

    private final AdminScriptExportService scriptExportService;

    private final DictLoaderFactory dictLoaderFactory;

    private final SocketService socketService;

    private static Long generateCustomId(Long firstPart, int nextSuffix) {
        String paddedFirstPart = String.format("%04d", firstPart);
        String paddedSecondPart = String.format("%03d", nextSuffix);
        String part = paddedFirstPart + paddedSecondPart;
        return Long.valueOf(part);
    }

    @Override
    public void create(SysDictCreateDTO dto) {
        SysDict sysDict = BeanCopyUtils.copy(dto, SysDict.class);
        QueryWrapper wrapper;
        long count = QueryChain.of(sysDictTypeMapper).eq(SysDictType::getId, dto.getSysDictTypeId()).count();
        CommonResponseEnum.NOT_EXISTS.message("SYS_DICT_TYPE不存在").assertTrue(count < 1);

        wrapper = QueryWrapper.create().where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()))
                .where(SysDictTableDef.SYS_DICT.CODE_NAME.eq(dto.getCodeName()));
        CommonResponseEnum.EXISTS.message("字典已存在").assertTrue(count(wrapper) > 0);

        if (Utils.isNotNull(dto.getAlias())) {
            wrapper = QueryWrapper.create().where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId()))
                    .where(SysDictTableDef.SYS_DICT.ALIAS.eq(dto.getAlias()));
            CommonResponseEnum.EXISTS.message("字典别名已存在").assertTrue(count(wrapper) > 0);
        }

        Long nextSuffix = getNextSuffix(dto.getSysDictTypeId());
        CommonResponseEnum.INVALID.message("当前字典类型下字典项数量已超过三位流水上限").assertTrue(nextSuffix > 999);
        Long generateCustomId = generateCustomId(dto.getSysDictTypeId(), nextSuffix.intValue());
        sysDict.setId(generateCustomId);
        save(sysDict);
        upCache(dto.getSysDictTypeId());
    }

    @Override
    public void update(SysDictUpdateDTO dto) {
        SysDict sysDict = BeanCopyUtils.copy(dto, SysDict.class);
        long count = QueryChain.of(this.mapper).select().from(SysDictTableDef.SYS_DICT).where(SysDictTableDef.SYS_DICT.ID.ne(dto.getId()))
                .and(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId())).and(SysDictTableDef.SYS_DICT.CODE_NAME.eq(dto.getCodeName()))
                .count();
        CommonResponseEnum.EXISTS.message(SysDictTableDef.SYS_DICT.CODE_NAME.getName() + "已存在").assertTrue(count > 0);
        if (Utils.isNotNull(dto.getAlias())) {
            long aliasCount = QueryChain.of(this.mapper).select().from(SysDictTableDef.SYS_DICT).where(SysDictTableDef.SYS_DICT.ID.ne(dto.getId()))
                    .and(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dto.getSysDictTypeId())).and(SysDictTableDef.SYS_DICT.ALIAS.eq(dto.getAlias())).count();
            CommonResponseEnum.EXISTS.message(SysDictTableDef.SYS_DICT.ALIAS.getName() + "已存在").assertTrue(aliasCount > 0);
        }
        sysDict.setId(dto.getId());
        saveOrUpdate(sysDict);
        upCache(dto.getSysDictTypeId());
    }

    private void upCache(Long dictTypeId) {
        SysDictType sysDictType = sysDictTypeService.detail(dictTypeId);
        String typeCode = sysDictType.getTypeCode();
        redisCache.clearDict(typeCode); // 清除redis缓存
        dictLoaderFactory.getDictByType(typeCode); // 更新缓存
        socketService.syncDict();
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
        socketService.syncDict();
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
    public Map<String, List<DictVO>> dictStatic() {
        return dictLoaderFactory.loadStaticDict();
    }

    @Override
    public List<DictVO> getDictByType(String typeCode) {
        return dictLoaderFactory.getDictByType(typeCode);
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

    @Override
    public String exportDictSql(SelectIdsDTO dto) {
        try {
            return scriptExportService.renderDictSql(buildDictScriptModel(dto), null);
        } catch (IOException e) {
            log.error("exportDictSql err", e);
            return "";
        }
    }

    @Override
    public ScriptExportVO exportDictScript(ScriptExportDTO dto) {
        try {
            return scriptExportService.renderDictExport(buildDictScriptModel(dto), dto.getSqlDialect());
        } catch (IOException e) {
            log.error("exportDictScript err", e);
            return new ScriptExportVO();
        }
    }

    private Map<String, Object> buildDictScriptModel(SelectIdsDTO dto) {
        Map<String, Object> dataModel = new HashMap<>();
        List<SysDictType> dictTypeList = List.of();
        List<SysDict> dictList = List.of();
        if (Utils.isNotNull(dto.getIds())) {
            dictTypeList = QueryChain.of(SysDictType.class).where(SYS_DICT_TYPE.ID.in(dto.getIds())).list();

            QueryWrapper queryWrapper = QueryWrapper.create().in(SysDict::getSysDictTypeId, dto.getIds()).orderBy(SysDict::getSysDictTypeId).asc()
                    .orderBy(SysDict::getSort).asc();
            dictList = list(queryWrapper);
        }
        dataModel.put("dictTypeList", dictTypeList);
        dataModel.put("dictList", dictList);
        return dataModel;
    }

    @Override
    public Map<String, List<DictVO>> getDictByCode(List<String> typeCode) {
        return typeCode.stream().collect(Collectors.toMap(code -> code, this::getDictByType));
    }

    private Long getNextSuffix(Long dictTypeId) {
        final Long[] maxIdHolder = {null};
        LogicDeleteManager.execWithoutLogicDelete(() -> maxIdHolder[0] = QueryChain.of(this.mapper).select(QueryMethods.max(SysDictTableDef.SYS_DICT.ID))
                .from(SysDictTableDef.SYS_DICT).where(SysDictTableDef.SYS_DICT.SYS_DICT_TYPE_ID.eq(dictTypeId)).oneAs(Long.class));
        Long maxId = maxIdHolder[0];
        if (maxId == null) {
            return 1L;
        }
        return maxId % 1000 + 1;
    }

}
