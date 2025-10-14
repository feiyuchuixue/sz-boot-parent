package com.sz.applet.miniBusiness.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.sz.applet.miniBusiness.pojo.bo.BannerBo;
import com.sz.applet.miniBusiness.pojo.bo.BannerListBO;
import com.sz.applet.miniBusiness.pojo.po.Banner;
import com.sz.applet.miniBusiness.pojo.vo.BannerVO;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * <p>
 * 小程序Banner表 Service
 * </p>
 *
 * @author sz
 * @since 2024-09-27
 */
public interface BannerService extends IService<Banner> {

    /**
     * 创建Banner
     * @param bo Banner创建信息
     */
    void create(BannerBo bo);

    /**
     * 更新Banner
     * @param bo Banner更新信息
     */
    void update(BannerBo bo);

    /**
     * 删除Banner
     * @param dto 要删除的BannerID列表
     */
    void remove(SelectIdsDTO dto);

    /**
     * Banner详情
     * @param id BannerID
     * @return Banner详情信息
     */
    BannerVO detail(Long id);

    /**
     * 分页查询Banner列表
     * @param bo 查询条件
     * @return Banner分页列表
     */
    Page<BannerVO> page(BannerListBO bo);

    /**
     * 查询Banner列表(全部)
     * @param bo 查询条件
     * @return Banner列表
     */
    List<BannerVO> list(BannerListBO bo);
}