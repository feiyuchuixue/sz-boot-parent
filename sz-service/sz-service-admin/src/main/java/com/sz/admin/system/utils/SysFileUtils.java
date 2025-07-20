package com.sz.admin.system.utils;

import com.sz.admin.system.service.SysFileService;
import com.sz.core.util.SpringApplicationContextUtils;

/**
 * @author qcqcqc
 */
public class SysFileUtils {

    @FunctionalInterface
    public interface ValueSetter {
        void set(String value);
    }

    @FunctionalInterface
    public interface IdGetter {
        Long get();
    }

    static private SysFileService getService() {
        return SpringApplicationContextUtils.getInstance().getBean(SysFileService.class);
    }

    static public void setUrl(IdGetter getter, ValueSetter setter) {
        Long l = getter.get();
        if (l == null) {
            return;
        }
        String imageUrl = getService().getFileUrl(l);
        if (imageUrl != null) {
            setter.set(imageUrl);
        }
    }
}
