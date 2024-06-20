package com.sz.mysql;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @ClassName DataScopeHelper
 * @Author sz
 * @Date 2024/6/19 8:37
 * @Version 1.0
 */
public class DataScopeHelper {
    //protected static ThreadLocal<DataScope[]> LOCAL_DATA_SCOPE = new ThreadLocal<>();
    protected static TransmittableThreadLocal<DataScope[]> LOCAL_DATA_SCOPE = new TransmittableThreadLocal<>();

    public static void startDataScope(DataScopeEnum scope, Class<?> clazz) {
        LOCAL_DATA_SCOPE.set(new DataScope[]{new DataScope(scope, clazz)});
    }

    public static void startDataScope(DataScope... dataScope) {
        LOCAL_DATA_SCOPE.set(dataScope);
    }

    public static DataScope[] getDataScope() {
        return LOCAL_DATA_SCOPE.get();
    }

    public static boolean isDataScope() {
        return LOCAL_DATA_SCOPE.get() != null && LOCAL_DATA_SCOPE.get().length > 0;
    }

    public static void clearDataScope() {
        LOCAL_DATA_SCOPE.remove();
    }

}
