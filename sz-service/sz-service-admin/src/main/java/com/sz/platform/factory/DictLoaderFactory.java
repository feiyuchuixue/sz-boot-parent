package com.sz.platform.factory;

import com.sz.core.common.entity.DictCustomVO;
import com.sz.platform.loader.DictLoader;
import com.sz.core.util.Utils;
import com.sz.platform.loader.DefaultStaticDictLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictLoaderFactory {
    // 动态字典加载器
    private final List<DictLoader> dynamicLoaders = new ArrayList<>();
    // 静态字典加载器
    private final DictLoader defaultLoader;

    @Autowired
    public DictLoaderFactory(DefaultStaticDictLoader defaultStaticDictLoader, List<DictLoader> loaders) {
        this.defaultLoader = defaultStaticDictLoader;

        // 将动态加载器注册到工厂中
        for (DictLoader loader : loaders) {
            if (Utils.isNotNull(loader.getDynamicTypeCode())) {
                dynamicLoaders.add(loader);
            }
        }
    }

    public Map<String, List<DictCustomVO>> loadAllDict() {

        // 加载静态字典
        Map<String, List<DictCustomVO>> result = new HashMap<>(defaultLoader.loadDict());

        // 加载所有动态字典
        for (DictLoader loader : dynamicLoaders) {
            result.putAll(loader.loadDict());
        }
        return result;
    }

}
