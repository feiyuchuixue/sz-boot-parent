package com.sz.core.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mybatisflex.core.paginate.Page;
import com.sz.core.common.entity.PageQuery;
import com.sz.core.common.entity.PageResult;
import com.sz.core.datascope.SimpleDataScopeHelper;

import java.util.List;

/**
 * @author: sz
 * @date: 2022/8/25 10:32
 * @description: (根据list)获取PageResult对象
 */
public class PageUtils {

    private PageUtils() {
        throw new IllegalStateException("PageUtils class Illegal");
    }

    /**
     * pagehelper获取PageResult对象
     *
     * @param list
     * @return PageResult<T>
     * @author sz
     * @date 2022-08-25 10:52:32
     */
    public static <T> PageResult<T> getPageResult(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages(), pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * pagehelper获取PageResult对象并将list替换成replaceList
     *
     * @param list
     * @param replaceList
     * @return PageResult<M>
     * @author sz
     * @date 2022-08-25 10:52:32
     */
    public static <T, M> PageResult<M> getPageResult(List<T> list, List<M> replaceList) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages(), pageInfo.getTotal(), replaceList);
    }

    /**
     * 根据list和count结果构建PageResult对象
     *
     * @param current
     *            当前页
     * @param limit
     *            limit,每页最大展示条数
     * @param list
     *            list结果
     * @param total
     *            总数
     * @return PageResult<T>
     * @author sz
     * @date 2021-12-21 10:44:34
     */
    public static <T> PageResult<T> getPageResult(int current, int limit, List<T> list, int total) {
        int totalPage = getTotalPage(total, limit);
        return new PageResult<>(current, limit, totalPage, total, list);
    }

    public static Page getPage(PageQuery query) {
        Page<Object> page = Page.of(query.getPage(), query.getLimit());
        if (SimpleDataScopeHelper.isDataScope())
            page.setOptimizeCountQuery(false); // 数据权限时，关闭优化查询。
        return page;
    }

    public static <T> PageResult<T> getPageResult(Page<T> page) {
        return new PageResult<>(page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow(), page.getRecords());
    }

    /**
     * 页数查询
     *
     * @param total
     * @param limit
     * @return
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

    public static void toPage(PageQuery dto) {
        PageHelper.startPage(dto.getPage(), dto.getLimit());
    }

}
