package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysConfigMapper;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigCreateDTO;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigListDTO;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigUpdateDTO;
import com.sz.admin.system.pojo.po.SysConfig;
import com.sz.admin.system.pojo.po.table.SysConfigTableDef;
import com.sz.admin.system.service.SysConfigService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.service.ConfService;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-11-23
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService, ConfService {

    private final RedisCache redisCache;

    @Override
    public void create(SysConfigCreateDTO dto) {
        SysConfig sysConfig = BeanCopyUtils.springCopy(dto, SysConfig.class);
        QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.eq(sysConfig.getConfigKey()));
        CommonResponseEnum.EXISTS.message("key已存在").assertTrue(count(wrapper) > 0);
        save(sysConfig);
    }

    @Override
    public void update(SysConfigUpdateDTO dto) {
        SysConfig sysConfig = BeanCopyUtils.springCopy(dto, SysConfig.class);
        QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.ID.ne(dto.getId()))
                .where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.eq(dto.getConfigKey()));
        CommonResponseEnum.EXISTS.message(2015, "key已存在").assertTrue(count(wrapper) > 0);
        saveOrUpdate(sysConfig);
        redisCache.clearConf(sysConfig.getConfigKey()); // 清除conf key
    }

    @Override
    public PageResult<SysConfig> list(SysConfigListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getConfigName())) {
            wrapper.where(SysConfigTableDef.SYS_CONFIG.CONFIG_NAME.like(dto.getConfigName()));
        }
        if (Utils.isNotNull(dto.getConfigKey())) {
            wrapper.where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.like(dto.getConfigKey()));
        }
        wrapper.orderBy(SysConfigTableDef.SYS_CONFIG.CREATE_TIME.asc());
        return PageUtils.getPageResult(page(PageUtils.getPage(dto), wrapper));
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.ID.in(dto.getIds()));
        List<SysConfig> list = list(wrapper);
        for (SysConfig sysConfig : list) {
            redisCache.clearConf(sysConfig.getConfigKey()); // 清除conf key
        }
        removeByIds(dto.getIds());
    }

    @Override
    public SysConfig detail(Object id) {
        return getById((Serializable) id);
    }

    @Override
    public boolean hasConfKey(String key) {
        if (redisCache.hasConfKey(key)) {
            return true;
        }
        return false;
    }

    @Override
    public String getConfValue(String key) {
        if (hasConfKey(key)) {
            return redisCache.getConfValue(key);
        } else {
            QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.eq(key));
            SysConfig sysConfig = getOne(wrapper);
            if (sysConfig != null) {
                String value = sysConfig.getConfigValue();
                redisCache.putConf(key, value);
                return value;
            }
        }
        return "";
    }
}
