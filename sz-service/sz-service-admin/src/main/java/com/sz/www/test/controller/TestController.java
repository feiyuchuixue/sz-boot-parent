package com.sz.www.test.controller;

import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SocketBean;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.redis.WebsocketRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName 网站测试
 * @Author sz
 * @Date 2024/5/24
 * @Version 1.0
 */
@Tag(name = "测试网站")
@RestController
@RequestMapping("www")
@RequiredArgsConstructor
public class TestController {

    private final WebsocketRedisService websocketRedisService;

    @Operation(summary = "测试")
    @GetMapping
    public ApiResult<String> test() {
        return ApiPageResult.success("这是一条测试信息");
    }


    @PostMapping
    @Operation(summary = "发送升级公告（socket）")
    public ApiResult sendUpgradeMsg() {
        SocketBean bean = new SocketBean<>();
        bean.setData("系统即将进行升级，预计需要几分钟时间。请您稍等片刻，感谢您的耐心等待");
        bean.setChannel(SocketChannelEnum.UPGRADE_CHANNEL);
        bean.setScope(MessageTransferScopeEnum.SOCKET_CLIENT);

        TransferMessage msg = new TransferMessage();
        msg.setMessage(bean);
        msg.setFromUser("system");
        msg.setToPushAll(true);
        websocketRedisService.sendServiceToWs(msg);

        return ApiResult.success();
    }

}
