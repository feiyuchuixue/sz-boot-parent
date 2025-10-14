package com.sz.applet.miniBusiness.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.applet.miniBusiness.pojo.po.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 小程序文章表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}