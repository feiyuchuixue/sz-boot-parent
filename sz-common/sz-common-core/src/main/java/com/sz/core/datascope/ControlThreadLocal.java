package com.sz.core.datascope;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.sz.core.common.entity.ControlPermissions;

/**
 * @author sz
 * @since 2024/6/19 8:37
 */
public class ControlThreadLocal {

    protected static TransmittableThreadLocal<ControlPermissions> LOCAL = new TransmittableThreadLocal<>();

    public static void set(ControlPermissions permission) {
        LOCAL.set(permission);
    }

    public static ControlPermissions get() {
        return LOCAL.get();
    }

    public static boolean hasLocal() {
        return LOCAL.get() != null;
    }

    public static void clearDataScope() {
        if (LOCAL != null) {
            LOCAL.remove();
        }
    }

}
