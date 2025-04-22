package com.sz.core.util;

import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;

import java.util.List;

/**
 * @author sz
 * @since 2023/9/6 17:27
 */
public class SocketUtil {

    public static String transferMessage(SocketMessage bean) {
        return JsonUtils.toJsonString(bean);
    }

    public static TransferMessage pubTransferMessage(SocketMessage sb, List<?> usernames) {
        TransferMessage transferMessage = new TransferMessage();
        transferMessage.setMessage(sb);
        transferMessage.setToUsers(usernames);
        return transferMessage;
    }

}
