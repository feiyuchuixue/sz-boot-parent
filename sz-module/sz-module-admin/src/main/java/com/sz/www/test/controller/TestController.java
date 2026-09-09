package com.sz.www.test.controller;

import com.sz.admin.system.pojo.dto.sysmessage.Message;
import com.sz.admin.system.service.SysMessageService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SocketPushMessage;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.SocketUtil;
import com.sz.redis.WebsocketRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ！！！ 【以下为功能演示内容，生产环境请删除】
 *
 * 网站测试
 *
 * @author sz
 * @since 2024/5/24
 * @version 1.0
 */
@Tag(name = "测试网站")
@RestController
@RequestMapping("www")
@RequiredArgsConstructor
@Profile({"dev", "local", "preview"})
public class TestController {

    private final WebsocketRedisService websocketRedisService;

    private final SysMessageService sysMessageService;

    @PostMapping("push/all")
    @Operation(summary = "全体推送-升级公告（socket）")
    public ApiResult<Void> sendUpgradeMsg() {
        websocketRedisService
                .sendServiceToWs(SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.UPGRADE_CHANNEL, "【全体推送】 系统即将进行升级，预计需要几分钟时间。请您稍等片刻，感谢您的耐心等待")));
        return ApiResult.success();
    }

    @PostMapping("push/user")
    @Operation(summary = "定向推送-升级公告（socket）")
    public ApiResult<Void> sendMsg() {
        // 向 loginId = 1 的用户（admin 账号）推送消息
        websocketRedisService.sendServiceToWs(SocketUtil
                .toUsers(SocketPushMessage.of(SocketChannelEnum.UPGRADE_CHANNEL, "【定向推送】 系统即将进行升级，预计需要几分钟时间。请您稍等片刻，感谢您的耐心等待"), List.of("1"), "system"));
        return ApiResult.success();
    }

    @Operation(summary = "测试socket踢下线")
    @PostMapping("kick")
    public ApiResult<Void> testKickOff() {
        websocketRedisService.sendServiceToWs(SocketUtil.broadcast(SocketPushMessage.of(SocketChannelEnum.KICK_OFF)));
        return ApiResult.success();
    }

    @Operation(summary = "测试消息发送")
    @PostMapping("message/send")
    public ApiResult<Void> sendMessage(@RequestBody Message msg) {
        sysMessageService.create(msg);
        return ApiResult.success();
    }

}
