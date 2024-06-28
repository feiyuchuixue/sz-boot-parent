package com.sz.core.datascope;
import java.util.List;

/**
 * @ClassName DataScopeService
 * @Author sz
 * @Date 2024/6/20 16:01
 * @Version 1.0
 */
public interface DataAccessService {
    /**
     * 根据数据权限获取当前用户具有的查询范围ID
     * @param scope
     * @return
     */
    List<?> getAccessibleIds(String scope);
}
