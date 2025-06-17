package com.sz.platform.loader;

import com.sz.core.common.entity.DictVO;
import com.sz.platform.enums.DynamicDictEnum;

import java.util.List;
import java.util.Map;

public interface DictLoader {

    /**
     * 返回带有前缀的动态 typeCode
     *
     * @return 带有前缀的 typeCode
     */
    DynamicDictEnum getDynamicTypeCode();

    /**
     * 加载所有字典数据，以 Map 结构返回。
     *
     * @return 字典数据的 Map，key 是 typeCode，value 是字典列表 DictCustomVO
     */
    Map<String, List<DictVO>> loadDict();

    /**
     * 根据typeCode获取Dict
     * 
     * @param typeCode
     *            类型
     * @return DictCustomVO集合
     */
    default List<DictVO> getDict(String typeCode) {
        return loadDict().get(typeCode);
    }

}
