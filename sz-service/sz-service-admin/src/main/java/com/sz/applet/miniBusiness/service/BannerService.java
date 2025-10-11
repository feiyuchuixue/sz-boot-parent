package com.sz.applet.miniBusiness.service;

import com.mybatisflex.core.service.IService;
import com.sz.applet.miniBusiness.pojo.dto.BannerCreateDTO;
import com.sz.applet.miniBusiness.pojo.dto.BannerListDTO;
import com.sz.applet.miniBusiness.pojo.dto.BannerUpdateDTO;
import com.sz.applet.miniBusiness.pojo.po.Banner;
import com.sz.applet.miniBusiness.pojo.vo.BannerVO;
import com.sz.core.common.entity.PageResult;
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
     * @param dto Banner创建信息
     */
    void create(BannerCreateDTO dto);

    /**
     * 更新Banner
     * @param dto Banner更新信息
     */
    void update(BannerUpdateDTO dto);

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
    BannerVO detail(Object id);

    /**
     * 分页查询Banner列表
     * @param dto 查询条件
     * @return Banner分页列表
     */
    PageResult<BannerVO> page(BannerListDTO dto);

    /**
     * 查询Banner列表(全部)
     * @param dto 查询条件
     * @return Banner列表
     */
    List<BannerVO> list(BannerListDTO dto);
}