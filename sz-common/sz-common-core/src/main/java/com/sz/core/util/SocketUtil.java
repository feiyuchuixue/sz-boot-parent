package com.sz.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sz.core.common.entity.SocketBean;
import com.sz.core.common.entity.SocketResult;
import com.sz.core.common.entity.TransferMessage;

import java.util.List;

/**
 * @author sz
 * @date 2023/9/6 17:27
 */
public class SocketUtil {

    public static SocketBean formatSocketMessage(String message) {
        return JSON.parseObject(message, SocketResult.class);
    }

    public static String transferMessage(SocketBean bean) {
        return JSON.toJSONString(bean, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 发送消息转换
     *
     * @param sb
     * @param usernames
     * @return
     */
    public static TransferMessage pubTransferMessage(SocketBean sb, List<String> usernames) {
        TransferMessage transferMessage = new TransferMessage();
        transferMessage.setMessage(sb);
        transferMessage.setToUsers(usernames);
        return transferMessage;
    }


}
