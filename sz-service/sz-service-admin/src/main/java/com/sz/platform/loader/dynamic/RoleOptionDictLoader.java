package com.sz.platform.loader.dynamic;

import com.sz.admin.system.pojo.vo.sysrole.RoleOptionsVO;
import com.sz.admin.system.service.SysRoleService;
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
 * 动态字典——角色loader
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
public class RoleOptionDictLoader implements DynamicDictLoader {

    private final RedisCache redisCache;

    private final SysRoleService sysRoleService;

    @Override
    public String getTypeCode() {
        return DynamicDictEnum.DYNAMIC_ROLE_OPTIONS.getTypeCode();
    }

    @Override
    public String getTypeName() {
        return DynamicDictEnum.DYNAMIC_ROLE_OPTIONS.getName();
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
        List<RoleOptionsVO> roleOptions = sysRoleService.getRoleOptions();
        for (int i = 0; i < roleOptions.size(); i++) {
            RoleOptionsVO option = roleOptions.get(i);
            dictVO = DictVO.builder().id(option.getId().toString()).codeName(option.getRoleName()).alias(option.getPermissions()).sort(i + 1)
                    .sysDictTypeCode(key).sysDictTypeName(name).callbackShowStyle("info").isDynamic(true).isLock("F").isShow("T").build();
            list.add(dictVO);
            redisCache.setDict(key, list);
        }
        return Map.of(key, list);
    }

}
