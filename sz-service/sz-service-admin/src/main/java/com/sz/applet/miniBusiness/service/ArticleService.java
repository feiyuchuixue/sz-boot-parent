package com.sz.applet.miniBusiness.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.sz.applet.miniBusiness.pojo.bo.ArticleBO;
import com.sz.applet.miniBusiness.pojo.bo.ArticleListBO;
import com.sz.applet.miniBusiness.pojo.po.Article;
import com.sz.applet.miniBusiness.pojo.vo.ArticleVO;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * <p>
 * 小程序文章表 Service
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
public interface ArticleService extends IService<Article> {

    /**
     * 创建文章
     * @param bo 文章创建信息
     */
    void create(ArticleBO bo);

    /**
     * 更新文章
     * @param bo 文章更新信息
     */
    void update(ArticleBO bo);

    /**
     * 删除文章
     * @param dto 要删除的文章ID列表
     */
    void remove(SelectIdsDTO dto);

    /**
     * 文章详情
     * @param id 文章ID
     * @return 文章详情信息
     */
    ArticleVO detail(Long id);

    /**
     * 分页查询文章列表
     * @param bo 查询条件
     * @return 文章分页列表
     */
    Page<ArticleVO> page(ArticleListBO bo);

    /**
     * 查询文章列表(全部)
     * @param bo 查询条件
     * @return 文章列表
     */
    List<ArticleVO> list(ArticleListBO bo);
}