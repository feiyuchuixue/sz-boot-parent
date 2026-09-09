package com.sz.admin.system.business;

import com.sz.admin.system.pojo.dto.sysmessage.PayloadBody;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.platform.socket.SocketService;
import com.sz.redis.WebsocketRedisService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketBusinessAcceptanceIT {

    @Test
    void frontendConfigAndDictSyncAreBroadcastToAllSocketClients() {
        CapturingWebsocketRedisService redisService = new CapturingWebsocketRedisService();
        SocketService socketService = new SocketService(redisService);

        socketService.syncFrontendConfig();
        socketService.syncDict();

        assertMessage(redisService.messages.get(0), true, SocketChannelEnum.SYNC_FRONTEND_CONF.name(), List.of(), null, null);
        assertMessage(redisService.messages.get(1), true, SocketChannelEnum.SYNC_DICT.name(), List.of(), null, null);
    }

    @Test
    void permissionAndKickOffEventsAreDirectedToOneUser() {
        CapturingWebsocketRedisService redisService = new CapturingWebsocketRedisService();
        SocketService socketService = new SocketService(redisService);

        socketService.syncPermission(1001L);
        socketService.kickOff(1002L);

        assertMessage(redisService.messages.get(0), false, SocketChannelEnum.SYNC_PERMISSIONS.name(), List.of("1001"), null, null);
        assertMessage(redisService.messages.get(1), false, SocketChannelEnum.KICK_OFF.name(), List.of("1002"), null, null);
    }

    @Test
    void messageEventKeepsPayloadSenderAndReceivers() {
        CapturingWebsocketRedisService redisService = new CapturingWebsocketRedisService();
        SocketService socketService = new SocketService(redisService);
        PayloadBody body = new PayloadBody();
        body.setTitle("测试");
        body.setContent("水电费三");

        socketService.sendMessage(body, 1L, List.of(2L, 3L));

        assertMessage(redisService.lastMessage(), false, SocketChannelEnum.MESSAGE.name(), List.of("2", "3"), "1", payload("测试", "水电费三"));
    }

    @Test
    void nullReceiverListIsNormalizedToEmptyDirectedMessage() {
        CapturingWebsocketRedisService redisService = new CapturingWebsocketRedisService();
        SocketService socketService = new SocketService(redisService);

        socketService.sendMessage(null, 1L, null);
        socketService.readMessage(2L, null);

        assertMessage(redisService.messages.get(0), false, SocketChannelEnum.MESSAGE.name(), List.of(), "1", null);
        assertMessage(redisService.messages.get(1), false, SocketChannelEnum.READ.name(), List.of(), "2", null);
    }

    @Test
    void nullUserTargetsAreNotSentAsLiteralNullUsers() {
        CapturingWebsocketRedisService redisService = new CapturingWebsocketRedisService();
        SocketService socketService = new SocketService(redisService);
        PayloadBody body = new PayloadBody();

        socketService.syncPermission(null);
        socketService.kickOff(null);
        socketService.sendMessage(body, null, Arrays.asList(2L, null, 3L));
        socketService.readMessage(null, Arrays.asList(4L, null));

        assertThat(redisService.messages).hasSize(2);
        assertMessage(redisService.messages.get(0), false, SocketChannelEnum.MESSAGE.name(), List.of("2", "3"), null, payload(null, null));
        assertMessage(redisService.messages.get(1), false, SocketChannelEnum.READ.name(), List.of("4"), null, null);
    }

    private static void assertMessage(TransferMessage message, boolean pushAll, String channel, List<String> users, String fromUser, Object payload) {
        assertThat(message.getScope()).isEqualTo(MessageTransferScopeEnum.SOCKET_CLIENT);
        assertThat(message.isToPushAll()).isEqualTo(pushAll);
        assertThat(message.getMessage().getChannel()).isEqualTo(channel);
        assertThat(message.getToUsers()).containsExactlyElementsOf(users);
        assertThat(message.getFromUser()).isEqualTo(fromUser);
        if (payload != null) {
            assertThat(message.getMessage().getData()).isEqualTo(payload);
        }
    }

    private static Map<String, Object> payload(String title, String content) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", title);
        payload.put("content", content);
        return payload;
    }

    private static class CapturingWebsocketRedisService extends WebsocketRedisService {

        private final List<TransferMessage> messages = new ArrayList<>();

        CapturingWebsocketRedisService() {
            super(null);
        }

        @Override
        public void sendServiceToWs(TransferMessage transferMessage) {
            messages.add(transferMessage);
        }

        private TransferMessage lastMessage() {
            return messages.get(messages.size() - 1);
        }
    }
}
