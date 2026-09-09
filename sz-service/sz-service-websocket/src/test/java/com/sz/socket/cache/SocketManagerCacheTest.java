package com.sz.socket.cache;

import com.sz.core.common.enums.SocketChannelEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SocketManagerCacheTest {

    @Test
    void heartbeatChannelsArePlainStringChannelsOutsideBusinessEnum() {
        assertThat(SocketManagerCache.CHANNEL_PING).isEqualTo("PING");
        assertThat(SocketManagerCache.CHANNEL_PONG).isEqualTo("PONG");
        assertThat(Arrays.stream(SocketChannelEnum.values()).map(Enum::name)).doesNotContain(SocketManagerCache.CHANNEL_PING, SocketManagerCache.CHANNEL_PONG);
    }

    @Test
    void authenticationExpiredCloseCodeMatchesFrontendContract() {
        assertThat(SocketManagerCache.CLOSE_CODE_AUTH_EXPIRED).isEqualTo(4401);
    }
}
