package com.sz.core.common.enums;

/**
 * @author sz
 * @date 2023/9/6 10:24
 */
public enum SocketChannelEnum {

    // @formatter:off
    DEFAULTS("default", "默认channel", "push_or_receive"),
    AUTH("auth", "鉴权", "push_or_receive"),
    CLOSE("close", "断开连接", "push"),
    KICK_OFF("kick_off", "踢下线", "push"),
    SYNC_MENU("sync_menu", "同步菜单", "push"),
    SYNC_DICT("sync_dict", "同步字典", "push"),
    SYNC_PERMISSIONS("sync_permissions", "同步permission权限", "push"),
    UPGRADE_CHANNEL("upgrade_channel", "升级通告", "push");
    // @formatter:on

    /**
     * 字符串标识
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：push 发送、1 receive、2 push_or_receive
     */
    private String status;

    SocketChannelEnum(String name, String remark, String status) {
        this.name = name;
        this.remark = remark;
        this.status = status;
    }
}
