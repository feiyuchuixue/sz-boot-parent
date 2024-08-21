package com.sz.platform.loader;

import com.sz.core.common.entity.DictCustomVO;

import java.util.List;
import java.util.Map;


public interface DictLoader {

    /**
     * 返回带有前缀的动态 typeCode
     *
     * @return 带有前缀的 typeCode
     */
    default String getPrefixedDynamicTypeCode() {
        return "dynamic_" + getDynamicTypeCode();
    }

    /**
     * 动态字典的key值
     */
    String getDynamicTypeCode();

    /**
     * 加载所有字典数据，以 Map 结构返回。
     *
     * @return 字典数据的 Map，key 是 typeCode，value 是字典列表 DictCustomVO
     */
    Map<String, List<DictCustomVO>> loadDict();

    List<DictCustomVO> getDict(String typeCode);

}
