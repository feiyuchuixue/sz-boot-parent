package com.sz.core.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mybatisflex.core.paginate.Page;
import com.sz.core.common.entity.PageQuery;
import com.sz.core.common.entity.PageResult;
import com.sz.core.datascope.SimpleDataScopeHelper;

import java.util.List;

/**
 * 工具类：提供基于列表构建 PageResult 对象的各种方法。 用于分页处理，兼容不同的数据源和需求。
 *
 * @author sz
 * @since 2022/8/25
 */
public class PageUtils {

    // 私有构造函数防止实例化
    private PageUtils() {
        throw new IllegalStateException("PageUtils class Illegal");
    }

    /**
     * 使用 PageHelper 构建 PageResult 对象。
     *
     * @param list
     *            数据列表
     * @param <T>
     *            列表中元素的类型
     * @return 包含分页信息的 PageResult 对象
     */
    public static <T> PageResult<T> getPageResult(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages(), pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 使用 PageHelper 构建 PageResult 对象，并替换结果列表。
     *
     * @param list
     *            原始数据列表
     * @param replaceList
     *            替换后的数据列表
     * @param <T>
     *            原始列表中元素的类型
     * @param <M>
     *            替换列表中元素的类型
     * @return 包含分页信息的 PageResult 对象，列表被替换为 replaceList
     */
    public static <T, M> PageResult<M> getPageResult(List<T> list, List<M> replaceList) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages(), pageInfo.getTotal(), replaceList);
    }

    /**
     * 根据当前页、每页条数、数据列表和总记录数构建 PageResult 对象。
     *
     * @param current
     *            当前页
     * @param limit
     *            每页展示的最大条数
     * @param list
     *            数据列表
     * @param total
     *            总记录数
     * @param <T>
     *            列表中元素的类型
     * @return 包含分页信息的 PageResult 对象
     */
    public static <T> PageResult<T> getPageResult(int current, int limit, List<T> list, int total) {
        int totalPage = getTotalPage(total, limit);
        return new PageResult<>(current, limit, totalPage, total, list);
    }

    /**
     * 根据分页查询对象构建 Page 对象。
     *
     * @param query
     *            分页查询对象
     * @param <T>
     *            分页中记录的类型
     * @return Page 对象，包含分页信息
     */
    public static <T> Page<T> getPage(PageQuery query) {
        Page<T> page = Page.of(query.getPage(), query.getLimit());
        if (SimpleDataScopeHelper.isDataScope())
            page.setOptimizeCountQuery(false); // 数据权限时，关闭优化查询。
        return page;
    }

    /**
     * 将 Page 对象转换为 PageResult 对象。
     *
     * @param page
     *            Page 对象
     * @param <T>
     *            页中记录的类型
     * @return 包含分页信息的 PageResult 对象
     */
    public static <T> PageResult<T> getPageResult(Page<T> page) {
        return new PageResult<>(page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow(), page.getRecords());
    }

    /**
     * 计算总页数。
     *
     * @param total
     *            总记录数
     * @param limit
     *            每页记录数
     * @return 总页数
     */
    public static int getTotalPage(int total, int limit) {
        int page = 0;
        if (total > 0) {
            page = total / limit;
            if (total % limit != 0) {
                page += 1;
            }
        }
        return page;
    }

    /**
     * 设置分页参数，用于分页查询。
     *
     * @param dto
     *            包含分页信息的查询对象
     */
    public static void toPage(PageQuery dto) {
        PageHelper.startPage(dto.getPage(), dto.getLimit());
    }

}
