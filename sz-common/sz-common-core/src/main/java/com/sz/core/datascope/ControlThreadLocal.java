package com.sz.core.datascope;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.sz.core.common.entity.ControlPermissions;

/**
 * @author sz
 * @since 2024/6/19 8:37
 */
public class ControlThreadLocal {

    private ControlThreadLocal() {
        throw new IllegalStateException("Utility class");
    }

    protected static final TransmittableThreadLocal<ControlPermissions> LOCAL_CONTROLLER = new TransmittableThreadLocal<>();

    public static void set(ControlPermissions permission) {
        LOCAL_CONTROLLER.set(permission);
    }

    public static ControlPermissions get() {
        return LOCAL_CONTROLLER.get();
    }

    public static boolean hasLocal() {
        return LOCAL_CONTROLLER.get() != null;
    }

    public static void clearDataScope() {
        LOCAL_CONTROLLER.remove();
    }

}
