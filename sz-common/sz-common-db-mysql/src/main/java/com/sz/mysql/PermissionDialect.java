package com.sz.mysql;

import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * @ClassName PermissionDialect
 * @Author sz
 * @Date 2024/6/17 10:36
 * @Version 1.0
 */
public class PermissionDialect extends CommonsDialectImpl {

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        //System.out.println("queryWrapper ==" + queryWrapper.toString());
        //获取当前用户信息，为 queryWrapper 添加额外的条件
        // queryWrapper.and(" 1=1 ");
        System.out.println(" before flex print 1111 ... ");
/*        Map<String, Object> context = CPI.getContext(queryWrapper);
        System.out.println("context ==" + context.toString());
        List<QueryTable> tables = CPI.getQueryTables(queryWrapper);
        System.out.println("tables ==" + tables.toString());*/
        return super.buildSelectSql(queryWrapper);
    }
}
