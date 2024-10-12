package com.sz.platform.loader.dynamic;

import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.DictVO;
import com.sz.platform.enums.DynamicDictEnum;
import com.sz.platform.loader.DictLoader;
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
public class UserOptionDictLoader implements DictLoader {

    private final RedisCache redisCache;

    private final SysUserService sysUserService;

    @Override
    public DynamicDictEnum getDynamicTypeCode() {
        return DynamicDictEnum.DYNAMIC_USER_OPTIONS;
    }

    @Override
    public Map<String, List<DictVO>> loadDict() {
        String key = getDynamicTypeCode().getTypeCode();
        String name = getDynamicTypeCode().getName();
        if (redisCache.hasHashKey(key)) {
            return Map.of(key, redisCache.getDictByType(key));
        }

        DictVO dictVO;
        List<DictVO> list = new ArrayList<>();
        List<UserOptionVO> userOptions = sysUserService.getUserOptions();
        for (int i = 0; i < userOptions.size(); i++) {
            UserOptionVO option = userOptions.get(i);
            dictVO = DictVO.builder().id(option.getId().toString()).codeName(option.getNickname()).alias(option.getUsername()).sort(i + 1).sysDictTypeCode(key)
                    .sysDictTypeName(name).callbackShowStyle("primary").isDynamic(true).isLock("F").isShow("T").build();
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
