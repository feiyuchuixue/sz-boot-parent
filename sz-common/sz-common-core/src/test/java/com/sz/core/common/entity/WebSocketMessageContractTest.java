package com.sz.core.common.entity;

import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.SocketUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketMessageContractTest {

    @Test
    void socketPushMessageUsesEnumConstantNameAsChannel() {
        SocketPushMessage message = SocketPushMessage.of(SocketChannelEnum.SYNC_DICT, Map.of("version", 2));

        assertThat(message.getChannel()).isEqualTo("SYNC_DICT");
        assertThat(message.getChannel()).isNotEqualTo(SocketChannelEnum.SYNC_DICT.getName());
        assertThat(message.getData()).isEqualTo(Map.of("version", 2));
    }

    @Test
    void transferMessageFactoriesKeepRoutingScopeExplicit() {
        SocketPushMessage message = SocketPushMessage.of(SocketChannelEnum.MESSAGE, "content");

        TransferMessage broadcast = SocketUtil.broadcast(message);
        assertThat(broadcast.isToPushAll()).isTrue();
        assertThat(broadcast.getToUsers()).isEmpty();
        assertThat(broadcast.getScope()).isEqualTo(MessageTransferScopeEnum.SOCKET_CLIENT);

        TransferMessage toUsers = SocketUtil.toUsers(message, List.of("1", "2"), "admin");
        assertThat(toUsers.isToPushAll()).isFalse();
        assertThat(toUsers.getToUsers()).containsExactly("1", "2");
        assertThat(toUsers.getFromUser()).isEqualTo("admin");
        assertThat(toUsers.getScope()).isEqualTo(MessageTransferScopeEnum.SOCKET_CLIENT);
    }

    @Test
    void websocketRedisChannelsRemainStable() {
        assertThat(GlobalConstant.SERVICE_TO_WS).isEqualTo("channel:service_to_ws");
        assertThat(GlobalConstant.WS_TO_SERVICE).isEqualTo("channel:ws_to_service");
    }
}
