package com.sz.applet.miniBusiness.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.applet.miniBusiness.mapper.ArticleMapper;
import com.sz.applet.miniBusiness.pojo.bo.ArticleBO;
import com.sz.applet.miniBusiness.pojo.bo.ArticleListBO;
import com.sz.applet.miniBusiness.pojo.po.Article;
import com.sz.applet.miniBusiness.pojo.vo.ArticleVO;
import com.sz.applet.miniBusiness.service.ArticleService;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.utils.MapstructUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sz.applet.miniBusiness.pojo.po.table.ArticleTableDef.ARTICLE;

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
    @Transactional(rollbackFor = Exception.class)
    public void create(ArticleBO bo) {
        save(MapstructUtils.convert(bo, Article.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ArticleBO bo) {
        Article article = getById(bo.getId());
        CommonResponseEnum.NOT_FOUND.assertNull(article, "文章不存在或已被删除");
        article.setType(bo.getType());
        article.setTitle(bo.getTitle());
        article.setAvatar(bo.getAvatar());
        article.setSummary(bo.getSummary());
        article.setTime(bo.getTime());
        article.setLabel(bo.getLabel());
        article.setAuthor(bo.getAuthor());
        article.setStatus(bo.getStatus());
        article.setContentType(bo.getContentType());
        article.setContent(bo.getContent());
        article.setIsTop(bo.getIsTop());
        article.setSort(bo.getSort());
        article.setPublishId(bo.getPublishId());
        updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().in(SysUser::getId, dto.getIds());
        // 检查用户是否存在
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) < 1);
        removeByIds(dto.getIds());
    }

    @Override
    public ArticleVO detail(Long id) {
        Article article = getById(id);
        CommonResponseEnum.NOT_FOUND.assertNull(article, "文章不存在或已被删除");
        return convertToVO(article);
    }

    @Override
    public Page<ArticleVO> page(ArticleListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        Page<ArticleVO> pageResult = this.pageAs(PageUtils.getPage(bo), queryWrapper, ArticleVO.class);
        return pageResult;
    }

    @Override
    public List<ArticleVO> list(ArticleListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        List<Article> list = list(queryWrapper);
        return list.stream().map(this::convertToVO).toList();
    }

    /**
     * 构建查询条件
     *
     * @param bo 查询参数
     * @return QueryWrapper
     */
    private QueryWrapper buildQueryWrapper(ArticleListBO bo) {
        return QueryWrapper.create()
                .select()
                .from(ARTICLE)
                .where(ARTICLE.TYPE.like(bo.getType()))
                .and(ARTICLE.TITLE.like(bo.getTitle()))
                .and(ARTICLE.LABEL.like(bo.getLabel()))
                .and(ARTICLE.AUTHOR.like(bo.getAuthor()))
                .and(ARTICLE.STATUS.eq(bo.getStatus()))
                .and(ARTICLE.DEL_FLAG.eq("0"))
                .orderBy(ARTICLE.SORT.desc(), ARTICLE.CREATE_TIME.desc());
    }

    /**
     * 转换为VO对象
     *
     * @param article 实体对象
     * @return VO对象
     */
    private ArticleVO convertToVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setType(article.getType());
        vo.setTitle(article.getTitle());
        vo.setAvatar(article.getAvatar());
        vo.setSummary(article.getSummary());
        vo.setTime(article.getTime());
        vo.setLabel(article.getLabel());
        vo.setAuthor(article.getAuthor());
        vo.setStatus(article.getStatus());
        vo.setContentType(article.getContentType());
        vo.setContent(article.getContent());
        vo.setIsTop(article.getIsTop());
        vo.setViewCount(article.getViewCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setSort(article.getSort());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        vo.setCreateId(article.getCreateId());
        vo.setUpdateId(article.getUpdateId());
        return vo;
    }
}