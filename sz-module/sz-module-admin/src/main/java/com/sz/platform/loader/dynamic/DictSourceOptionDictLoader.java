package com.sz.platform.loader.dynamic;

import com.sz.admin.system.service.SysDictSourceService;
import com.sz.admin.system.pojo.vo.sysdict.SysDictSourceOptionVO;
import com.sz.core.common.dict.DynamicDictLoader;
import com.sz.core.common.entity.DictVO;
import com.sz.platform.enums.DynamicDictEnum;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态字典——字典来源loader
 * <p>
 * DictSourceOptionDictLoader
 *
 * @author sz-admin
 * @version 1.0
 * @since 2026-05-21
 */
@Component
@RequiredArgsConstructor
public class DictSourceOptionDictLoader implements DynamicDictLoader {

    private final RedisCache redisCache;

    private final SysDictSourceService sysDictSourceService;

    @Override
    public String getTypeCode() {
        return DynamicDictEnum.DYNAMIC_DICT_SOURCE_OPTIONS.getTypeCode();
    }

    @Override
    public String getTypeName() {
        return DynamicDictEnum.DYNAMIC_DICT_SOURCE_OPTIONS.getName();
    }

    @Override
    public Map<String, List<DictVO>> loadDict() {
        String key = getDynamicTypeCode();
        String name = getTypeName();
        if (redisCache.hasHashKey(key)) {
            return Map.of(key, redisCache.getDictByType(key));
        }
        List<SysDictSourceOptionVO> sources = sysDictSourceService.listOptions();
        List<DictVO> list = new ArrayList<>();
        for (int i = 0; i < sources.size(); i++) {
            SysDictSourceOptionVO source = sources.get(i);
            DictVO dictVO = DictVO.builder().id(source.getSourceCode()).codeName(source.getSourceName()).sort(i + 1).sysDictTypeCode(key).sysDictTypeName(name)
                    .callbackShowStyle("primary").isDynamic(true).isLock("F").isShow("T").build();
            list.add(dictVO);
        }
        redisCache.setDict(key, list);
        return Map.of(key, list);
    }

    @Override
    public List<DictVO> getDict(String typeCode) {
        return loadDict().get(typeCode);
    }

}
