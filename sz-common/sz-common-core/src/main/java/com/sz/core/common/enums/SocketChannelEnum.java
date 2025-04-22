package com.sz.core.common.enums;

import lombok.Getter;

/**
 * @author sz
 * @since 2023/9/6 10:24
 */
@Getter
public enum SocketChannelEnum {

    // @formatter:off
    DEFAULTS("default", "默认channel", "push_or_receive"),
    AUTH("auth", "鉴权", "push_or_receive"),
    CLOSE("close", "断开连接", "push"),
    KICK_OFF("kick_off", "踢下线", "push"),
    SYNC_MENU("sync_menu", "同步菜单", "push"),
    SYNC_DICT("sync_dict", "同步字典", "push"),
    SYNC_PERMISSIONS("sync_permissions", "同步permission权限", "push"),
    UPGRADE_CHANNEL("upgrade_channel", "升级通告", "push"),
    MESSAGE("message", "系统消息", "push"),
    READ("read","消息已读刷新数量","push");
    // @formatter:on

    /**
     * 字符串标识
     */
    private final String name;

    /**
     * 备注
     */
    private final String remark;

    /**
     * 状态：push 发送、1 receive、2 push_or_receive
     */
    private final String status;

    SocketChannelEnum(String name, String remark, String status) {
        this.name = name;
        this.remark = remark;
        this.status = status;
    }
}
