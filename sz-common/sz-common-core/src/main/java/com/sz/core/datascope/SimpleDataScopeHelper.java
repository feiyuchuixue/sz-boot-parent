package com.sz.core.datascope;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author sz
 * @since 2024/6/19 8:37
 */
public class SimpleDataScopeHelper {

    private SimpleDataScopeHelper() {
        throw new IllegalStateException("SimpleDataScopeHelper class Illegal");
    }

    protected static final TransmittableThreadLocal<Class<?>> LOCAL_DATA_SCOPE = new TransmittableThreadLocal<>();

    public static void start(Class<?> clazz) {
        LOCAL_DATA_SCOPE.set(clazz);
    }

    public static Class<?> get() {
        return LOCAL_DATA_SCOPE.get();
    }

    public static boolean isDataScope() {
        return LOCAL_DATA_SCOPE.get() != null;
    }

    public static void clearDataScope() {
        LOCAL_DATA_SCOPE.remove();
    }

}
