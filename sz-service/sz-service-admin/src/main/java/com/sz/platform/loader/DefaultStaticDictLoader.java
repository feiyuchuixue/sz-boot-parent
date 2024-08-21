package com.sz.platform.loader;

import com.sz.admin.system.mapper.SysDictMapper;
import com.sz.core.common.entity.DictCustomVO;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 静态字典loader
 *
 * @ClassName StaticDictLoader
 * @Author sz
 * @Date 2024/8/21 9:06
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
public class DefaultStaticDictLoader implements DictLoader {

    private final RedisCache redisCache;

    private final SysDictMapper sysDictMapper;

    @Override
    public String getDynamicTypeCode() {
        return "";
    }

    @Override
    public Map<String, List<DictCustomVO>> loadDict() {
        if (redisCache.hasKey()) {
            return redisCache.getAllDict();
        }

        // 查询所有字典
        List<DictCustomVO> dictVOS = sysDictMapper.listDict("");
        if (dictVOS.isEmpty()) {
            return Map.of();
        }
        Map<String, List<DictCustomVO>> result = dictVOS.stream()
                .collect(Collectors.groupingBy(DictCustomVO::getSysDictTypeCode,
                        LinkedHashMap::new, // 使用 LinkedHashMap 作为分组的容器,有序解决乱序问题
                        Collectors.toList()));
        redisCache.putAllDict(result);
        return result;
    }

    @Override
    public List<DictCustomVO> getDict(String typeCode) {
        if (redisCache.hasHashKey(typeCode)) {
            return redisCache.getDictByType(getPrefixedDynamicTypeCode());
        }

        List<DictCustomVO> list = sysDictMapper.listDict(typeCode);
        if (list.isEmpty()) {
            return List.of();
        }
        redisCache.setDict(typeCode, list);
        return list;
    }
}
