package com.sz.core.util;

import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.SocketResult;
import com.sz.core.common.entity.TransferMessage;

import java.util.List;

/**
 * @author sz
 * @since 2023/9/6 17:27
 */
public class SocketUtil {

    public static SocketMessage formatSocketMessage(String message) {
        return JsonUtils.parseObject(message, SocketResult.class);
    }

    public static <T> String transferMessage(SocketMessage<T> bean) {
        return JsonUtils.toJsonString(bean);
    }

    public static <T> TransferMessage<T> pubTransferMessage(SocketMessage<T> sb, List<String> usernames) {
        TransferMessage<T> transferMessage = new TransferMessage<>();
        transferMessage.setMessage(sb);
        transferMessage.setToUsers(usernames);
        return transferMessage;
    }

}
