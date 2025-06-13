package com.sz.www.test.controller;

import com.sz.admin.system.pojo.dto.sysmessage.Message;
import com.sz.admin.system.service.SysMessageService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.redis.WebsocketRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
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
    private  String UCenter;
    private final SysMessageService sysMessageService;

    @PostMapping("push/all")
    @Operation(summary = "全体推送-升级公告（socket）")
    public ApiResult<Void> sendUpgradeMsg() {
        SocketMessage bean = new SocketMessage();
        bean.setData("【全体推送】 系统即将进行升级，预计需要几分钟时间。请您稍等片刻，感谢您的耐心等待");
        bean.setChannel(SocketChannelEnum.UPGRADE_CHANNEL);
        bean.setScope(MessageTransferScopeEnum.SOCKET_CLIENT);
        TransferMessage msg = new TransferMessage();
        msg.setMessage(bean);
        msg.setFromUser("system");
        msg.setToPushAll(true);
        websocketRedisService.sendServiceToWs(msg);
        return ApiResult.success();
    }

    @PostMapping("push/user")
    @Operation(summary = "定向推送-升级公告（socket）")
    public ApiResult<Void> sendMsg() {
        SocketMessage bean = new SocketMessage();
        bean.setData("【定向推送】 系统即将进行升级，预计需要几分钟时间。请您稍等片刻，感谢您的耐心等待");
        bean.setChannel(SocketChannelEnum.UPGRADE_CHANNEL);
        bean.setScope(MessageTransferScopeEnum.SOCKET_CLIENT);

        TransferMessage msg = new TransferMessage();
        msg.setMessage(bean);
        msg.setFromUser("system");
        msg.setToPushAll(false);
        List<String> toUsers = new ArrayList<>();
        toUsers.add("1"); // 向 loginId = 1 的用户推送消息。既 admin 账号
        msg.setToUsers(toUsers);
        websocketRedisService.sendServiceToWs(msg);
        return ApiResult.success();
    }

    @Operation(summary = "测试socket踢下线")
    @PostMapping("kick")
    public ApiResult<Void> testKickOff() {
        TransferMessage tm = new TransferMessage();
        tm.setToPushAll(true);
        SocketMessage sb = new SocketMessage();
        sb.setChannel(SocketChannelEnum.KICK_OFF);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
        return ApiResult.success();
    }

    @Operation(summary = "测试消息发送")
    @PostMapping("message/send")
    public ApiResult<Void> sendMessage(@RequestBody Message msg) {
        sysMessageService.create(msg);
        return ApiResult.success();
    }

}
