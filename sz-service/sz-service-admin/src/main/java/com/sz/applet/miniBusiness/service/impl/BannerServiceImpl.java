package com.sz.applet.miniBusiness.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniBusiness.mapper.BannerMapper;
import com.sz.applet.miniBusiness.pojo.bo.BannerBo;
import com.sz.applet.miniBusiness.pojo.bo.BannerListBO;
import com.sz.applet.miniBusiness.pojo.po.Banner;
import com.sz.applet.miniBusiness.pojo.vo.BannerVO;
import com.sz.applet.miniBusiness.service.BannerService;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.utils.MapstructUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sz.applet.miniBusiness.pojo.po.table.BannerTableDef.BANNER;

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
    @Transactional(rollbackFor = Exception.class)
    public void create(BannerBo bo) {
        save(MapstructUtils.convert(bo, Banner.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BannerBo bo) {
        Banner banner = getById(bo.getId());
        CommonResponseEnum.NOT_FOUND.assertNull(banner, "Banner不存在或已被删除");
        banner.setLink(bo.getLink());
        banner.setType(bo.getType());
        banner.setNames(bo.getNames());
        banner.setPicture(bo.getPicture());
        banner.setStatus(bo.getStatus());
        banner.setSort(bo.getSort());
        banner.setContentType(bo.getContentType());
        banner.setContent(bo.getContent());
        updateById(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(SelectIdsDTO dto) {
        removeByIds(dto.getIds());
    }

    @Override
    public BannerVO detail(Long id) {
        Banner banner = getById(id);
        CommonResponseEnum.NOT_FOUND.assertNull(banner, "Banner不存在或已被删除");
        return MapstructUtils.convert(banner, BannerVO.class);
    }

    @Override
    public Page<BannerVO> page(BannerListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        return this.pageAs(PageUtils.getPage(bo), queryWrapper, BannerVO.class);
    }

    @Override
    public List<BannerVO> list(BannerListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        return listAs(queryWrapper, BannerVO.class);
    }

    /**
     * 构建查询条件
     *
     * @param bo 查询参数
     * @return QueryWrapper
     */
    private QueryWrapper buildQueryWrapper(BannerListBO bo) {
        return QueryWrapper.create()
                .select()
                .from(BANNER)
                .where(BANNER.TYPE.like(bo.getType()))
                .and(BANNER.NAMES.like(bo.getNames()))
                .and(BANNER.STATUS.eq(bo.getStatus()))
                .and(BANNER.DEL_FLAG.eq("0"))
                .orderBy(BANNER.SORT.desc(), BANNER.CREATE_TIME.desc());
    }
}