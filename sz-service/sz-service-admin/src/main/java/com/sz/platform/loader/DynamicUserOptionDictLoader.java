package com.sz.platform.loader;

import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.DictCustomVO;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态字典——系统用户loader
 *
 * @ClassName DynamicUserOptionDictLoader
 * @Author sz
 * @Date 2024/8/21 13:17
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
public class DynamicUserOptionDictLoader implements DictLoader {

    private final RedisCache redisCache;

    private final SysUserService sysUserService;

    @Override
    public String getDynamicTypeCode() {
        return "user_options";
    }

    @Override
    public Map<String, List<DictCustomVO>> loadDict() {
        if (redisCache.hasHashKey(getPrefixedDynamicTypeCode())) {
            return Map.of(getPrefixedDynamicTypeCode(), redisCache.getDictByType(getPrefixedDynamicTypeCode()));
        }

        DictCustomVO dictCustomVO;
        List<DictCustomVO> list = new ArrayList<>();
        List<UserOptionVO> userOptions = sysUserService.getUserOptions();
        for (int i = 0; i < userOptions.size(); i++) {
            UserOptionVO option = userOptions.get(i);
            dictCustomVO = DictCustomVO.builder()
                    .id(option.getId().toString())
                    .codeName(option.getNickname())
                    .alias(option.getUsername())
                    .sort(i + 1)
                    .sysDictTypeCode(getPrefixedDynamicTypeCode())
                    .sysDictTypeName("用户信息")
                    .isDynamic(true)
                    .build();
            list.add(dictCustomVO);
        }
        redisCache.setDict(getPrefixedDynamicTypeCode(), list);
        return Map.of(getPrefixedDynamicTypeCode(), list);
    }

    @Override
    public List<DictCustomVO> getDict(String typeCode) {
        return loadDict().get(typeCode);
    }

}
