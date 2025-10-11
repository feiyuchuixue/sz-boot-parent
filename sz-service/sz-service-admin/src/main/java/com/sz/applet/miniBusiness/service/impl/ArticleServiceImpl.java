package com.sz.applet.miniBusiness.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniBusiness.mapper.ArticleMapper;
import com.sz.applet.miniBusiness.pojo.dto.ArticleCreateDTO;
import com.sz.applet.miniBusiness.pojo.dto.ArticleListDTO;
import com.sz.applet.miniBusiness.pojo.dto.ArticleUpdateDTO;
import com.sz.applet.miniBusiness.pojo.po.Article;
import com.sz.applet.miniBusiness.pojo.vo.ArticleVO;
import com.sz.applet.miniBusiness.service.ArticleService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 小程序文章表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public void create(ArticleCreateDTO dto) {
        // TODO: 实现创建文章逻辑
    }

    @Override
    public void update(ArticleUpdateDTO dto) {
        // TODO: 实现更新文章逻辑
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        // TODO: 实现删除文章逻辑
    }

    @Override
    public ArticleVO detail(Object id) {
        // TODO: 实现获取文章详情逻辑
        return null;
    }

    @Override
    public PageResult<ArticleVO> page(ArticleListDTO dto) {
        // TODO: 实现分页查询文章列表逻辑
        return null;
    }

    @Override
    public List<ArticleVO> list(ArticleListDTO dto) {
        // TODO: 实现查询文章列表逻辑(全部)
        return null;
    }
}