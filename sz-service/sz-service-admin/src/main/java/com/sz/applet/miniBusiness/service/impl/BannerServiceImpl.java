package com.sz.applet.miniBusiness.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniBusiness.mapper.BannerMapper;
import com.sz.applet.miniBusiness.pojo.dto.BannerCreateDTO;
import com.sz.applet.miniBusiness.pojo.dto.BannerListDTO;
import com.sz.applet.miniBusiness.pojo.dto.BannerUpdateDTO;
import com.sz.applet.miniBusiness.pojo.po.Banner;
import com.sz.applet.miniBusiness.pojo.vo.BannerVO;
import com.sz.applet.miniBusiness.service.BannerService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 小程序Banner表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-09-27
 */
@Service
@RequiredArgsConstructor
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public void create(BannerCreateDTO dto) {
        // TODO: 实现创建Banner逻辑
    }

    @Override
    public void update(BannerUpdateDTO dto) {
        // TODO: 实现更新Banner逻辑
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        // TODO: 实现删除Banner逻辑
    }

    @Override
    public BannerVO detail(Object id) {
        // TODO: 实现获取Banner详情逻辑
        return null;
    }

    @Override
    public PageResult<BannerVO> page(BannerListDTO dto) {
        // TODO: 实现分页查询Banner列表逻辑
        return null;
    }

    @Override
    public List<BannerVO> list(BannerListDTO dto) {
        // TODO: 实现查询Banner列表逻辑(全部)
        return null;
    }
}