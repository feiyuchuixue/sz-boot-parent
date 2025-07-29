package com.sz.platform.loader.dynamic;

import com.sz.admin.system.pojo.vo.sysdept.DeptOptionsVO;
import com.sz.admin.system.service.SysDeptService;
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
 * 动态字典——部门loader
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/7/28
 */
@Component
@RequiredArgsConstructor
public class DeptOptionDictLoader implements DynamicDictLoader {

    private final RedisCache redisCache;

    private final SysDeptService sysDeptService;

    @Override
    public String getTypeCode() {
        return DynamicDictEnum.DYNAMIC_DEPT_OPTIONS.getTypeCode();
    }

    @Override
    public String getTypeName() {
        return DynamicDictEnum.DYNAMIC_DEPT_OPTIONS.getName();
    }

    @Override
    public Map<String, List<DictVO>> loadDict() {
        String key = getDynamicTypeCode();
        String name = getTypeName();

        if (redisCache.hasHashKey(key)) {
            return Map.of(key, redisCache.getDictByType(key));
        }

        DictVO dictVO;
        List<DictVO> list = new ArrayList<>();
        List<DeptOptionsVO> deptOptions = sysDeptService.getDeptOptions();
        for (int i = 0; i < deptOptions.size(); i++) {
            DeptOptionsVO option = deptOptions.get(i);
            dictVO = DictVO.builder().id(option.getId().toString()).codeName(option.getName()).alias("").sort(i + 1).sysDictTypeCode(key).sysDictTypeName(name)
                    .callbackShowStyle("info").isDynamic(true).isLock("F").isShow("T").build();
            list.add(dictVO);
            redisCache.setDict(key, list);
        }
        return Map.of(key, list);
    }

}
