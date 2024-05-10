package com.sz.platform.event;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PermissionChangeMeta
 * @Author sz
 * @Date 2024/2/29 15:44
 * @Version 1.0
 */
@Data
public class PermissionMeta {

    {
        userIds = new ArrayList<>();
    }

    public PermissionMeta() {
    }

    public PermissionMeta(List<?> userIds) {
        this.userIds = userIds;
    }

    private List<?> userIds;

}
