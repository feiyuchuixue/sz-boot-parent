package com.sz.core.common.dict;

import com.sz.core.common.entity.DictVO;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictLoaderFactory {

    // 动态字典加载器，按完整 typeCode 精准命中
    private final Map<String, DynamicDictLoader> dynamicLoaderMap = new LinkedHashMap<>();

    // 唯一的静态字典加载器
    private final DictLoader defaultLoader;

    @Autowired
    public DictLoaderFactory(List<DictLoader> allLoaders) {
        DictLoader foundDefault = null;
        for (DictLoader loader : allLoaders) {
            if (loader instanceof DynamicDictLoader dynamicLoader) {
                registerDynamicLoader(dynamicLoader);
            } else {
                if (foundDefault == null) {
                    foundDefault = loader;
                }
            }
        }
        this.defaultLoader = foundDefault;
    }

    private void registerDynamicLoader(DynamicDictLoader dynamicLoader) {
        String rawTypeCode = dynamicLoader.getTypeCode();
        if (!Utils.isNotNull(rawTypeCode)) {
            throw new IllegalStateException("动态字典加载器 typeCode 不能为空: " + dynamicLoader.getClass().getName());
        }
        String typeCode = dynamicLoader.getDynamicTypeCode();
        if (!Utils.isNotNull(typeCode)) {
            throw new IllegalStateException("动态字典加载器 full typeCode 不能为空: " + dynamicLoader.getClass().getName());
        }
        DynamicDictLoader exists = dynamicLoaderMap.putIfAbsent(typeCode, dynamicLoader);
        if (exists != null) {
            String message = "动态字典 typeCode 重复: " + typeCode + ", loaders: " + exists.getClass().getName() + ", "
                    + dynamicLoader.getClass().getName();
            throw new IllegalStateException(message);
        }
    }

    /**
     * 获取所有静态字典
     *
     * @return Map
     */
    public Map<String, List<DictVO>> loadStaticDict() {
        return defaultLoader != null ? defaultLoader.loadDict() : Map.of();
    }

    /**
     * 获取所有字典（静态 + 动态）
     *
     * @return Map
     */
    public Map<String, List<DictVO>> loadAllDict() {
        Map<String, List<DictVO>> result = new HashMap<>(loadStaticDict());
        for (DynamicDictLoader loader : dynamicLoaderMap.values()) {
            Map<String, List<DictVO>> dictMap = loader.loadDict();
            if (dictMap != null) {
                result.putAll(dictMap);
            }
        }
        return result;
    }

    /**
     * 获取指定类型的字典
     *
     * @param typeCode
     *            类型
     * @return DictVO集合
     */
    public List<DictVO> getDictByType(String typeCode) {
        if (!Utils.isNotNull(typeCode)) {
            return List.of();
        }
        DynamicDictLoader dynamicLoader = dynamicLoaderMap.get(typeCode);
        if (dynamicLoader != null) {
            List<DictVO> dictList = dynamicLoader.getDict(typeCode);
            return dictList != null ? dictList : List.of();
        }
        return defaultLoader != null ? defaultLoader.getDict(typeCode) : List.of();
    }

    /**
     * 查询所有字典类型（静态 + 动态）
     *
     * @return DictTypeVO 集合
     */
    public List<DictTypeVO> getAllDictType() {
        DictTypeService dictTypeService = SpringApplicationContextUtils.getInstance().getBean(DictTypeService.class);
        List<DictTypeVO> result = new ArrayList<>(dictTypeService.findDictType());

        for (DynamicDictLoader loader : dynamicLoaderMap.values()) {
            String typeCode = loader.getDynamicTypeCode();
            String name = loader.getTypeName();
            DictTypeVO dictTypeVO = DictTypeVO.builder().typeName(name).typeCode(typeCode).isDynamic(true).build();
            result.add(dictTypeVO);
        }
        return result;
    }
}
