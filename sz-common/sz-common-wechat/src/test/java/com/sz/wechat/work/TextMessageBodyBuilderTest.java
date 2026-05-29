package com.sz.wechat.work;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextMessageBodyBuilderTest {

    @Test
    void builderShouldKeepWechatTextMessageDefaults() {
        TextMessageBody body = TextMessageBody.builder().build();

        assertThat(body.getMsgtype()).isEqualTo("text");
        assertThat(body.getSafe()).isZero();
    }
}
