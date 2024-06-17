package com.sz.mysql;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName SzServiceImpl
 * @Author sz
 * @Date 2024/6/17 16:44
 * @Version 1.0
 */
public class SzServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Autowired
    protected M mapper;

    @Override
    public BaseMapper<T> getMapper() {
        return mapper;
    }

    @Override
    public List<T> list(QueryWrapper query) {
        //获取当前用户信息，为 queryWrapper 添加额外的条件
        return IService.super.list(query);
    }

    @Override
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        System.out.println(" page as ...");
        return IService.super.pageAs(page, query, asType);
    }

    public <R> Page<R> pageAsScope(Page<R> page, QueryWrapper query, Class<R> asType) {
        System.out.println(" page as scope...");
        List<QueryTable> queryTables = CPI.getQueryTables(query);
        System.out.println("queryTables ==" + queryTables);
        return IService.super.pageAs(page, query, asType);
    }

}
