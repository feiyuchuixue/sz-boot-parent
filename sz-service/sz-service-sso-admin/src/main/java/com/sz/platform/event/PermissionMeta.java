package com.sz.platform.event;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionChangeMeta
 * 
 * @author sz
 * @since 2024/2/29 15:44
 * @version 1.0
 */
@Data
public class PermissionMeta {

    public PermissionMeta() {
    }

    public PermissionMeta(List<?> userIds) {
        this.userIds = userIds;
    }

    private List<?> userIds = new ArrayList<>();

}
