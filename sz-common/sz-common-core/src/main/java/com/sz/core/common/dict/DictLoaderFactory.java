package com.sz.core.common.dict;

import com.sz.core.common.entity.DictVO;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictLoaderFactory {

    // 动态字典加载器
    private final List<DynamicDictLoader> dynamicLoaders = new ArrayList<>();

    // 唯一的静态字典加载器
    private final DictLoader defaultLoader;

    @Autowired
    public DictLoaderFactory(List<DictLoader> allLoaders) {
        DictLoader foundDefault = null;
        for (DictLoader loader : allLoaders) {
            if (loader instanceof DynamicDictLoader dynamicLoader) {
                if (Utils.isNotNull(dynamicLoader.getDynamicTypeCode())) {
                    dynamicLoaders.add(dynamicLoader);
                }
            } else {
                if (foundDefault == null) {
                    foundDefault = loader;
                }
            }
        }
        this.defaultLoader = foundDefault;
    }

    /**
     * 获取所有字典（静态 + 动态）
     *
     * @return Map
     */
    public Map<String, List<DictVO>> loadAllDict() {
        Map<String, List<DictVO>> result = defaultLoader != null ? new HashMap<>(defaultLoader.loadDict()) : new HashMap<>();
        for (DynamicDictLoader loader : dynamicLoaders) {
            result.putAll(loader.loadDict());
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
        Map<String, List<DictVO>> allDict = loadAllDict();
        if (!allDict.containsKey(typeCode)) {
            if (typeCode.contains("dynamic_")) {
                return List.of();
            }
            return defaultLoader != null ? defaultLoader.getDict(typeCode) : List.of();
        }
        return allDict.get(typeCode);
    }

    /**
     * 查询所有字典类型（静态 + 动态）
     *
     * @return DictTypeVO 集合
     */
    public List<DictTypeVO> getAllDictType() {
        DictTypeService dictTypeService = SpringApplicationContextUtils.getInstance().getBean(DictTypeService.class);
        List<DictTypeVO> result = new ArrayList<>(dictTypeService.findDictType());

        for (DynamicDictLoader loader : dynamicLoaders) {
            String typeCode = loader.getDynamicTypeCode();
            String name = loader.getTypeName();
            DictTypeVO dictTypeVO = DictTypeVO.builder().typeName(name).typeCode(typeCode).isDynamic(true).build();
            result.add(dictTypeVO);
        }
        return result;
    }
}
