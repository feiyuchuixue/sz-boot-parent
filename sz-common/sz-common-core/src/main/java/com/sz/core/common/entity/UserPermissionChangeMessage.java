package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPermissionChangeMessage {

    @Schema(description = "username")
    private String username;

    @Schema(description = "用户id")
    private Long userId;

    /**
     * 是否是对所有用户进行permission更新
     */
    private boolean toChangeAll;

    public UserPermissionChangeMessage() {
    }

    public UserPermissionChangeMessage(String username, Long userId, boolean toChangeAll) {
        this.username = username;
        this.userId = userId;
        this.toChangeAll = toChangeAll;
    }

    public UserPermissionChangeMessage(String username, boolean toChangeAll) {
        this.username = username;
        this.toChangeAll = toChangeAll;
    }

}
