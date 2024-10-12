package com.sz.socket.cache;

import com.sz.core.common.entity.WsSession;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sz
 * @date 2023/9/6 17:01
 */
@Slf4j
public class SocketManagerCache {

    public static final String SEC_WEBSOCKET_PROTOCOL_HEADER = "sec-websocket-protocol";

    public static final String LOGIN_ID = "loginId";

    /**
     * sid <==> session 关系
     */
    public static ConcurrentHashMap<String, WsSession> onlineSessionMap = new ConcurrentHashMap<String, WsSession>();

    /**
     * loginId <==> sid 关系; 1对多,一个用户有可能在多个浏览器上登录
     */
    public static ConcurrentHashMap<String, List<String>> onlineUserSessionIdMap = new ConcurrentHashMap<String, List<String>>();

    public static void addOnlineSid(String loginId, String sid) {
        if (onlineUserSessionIdMap.containsKey(loginId)) {
            List<String> sidArray = onlineUserSessionIdMap.get(loginId);
            sidArray.add(sid);
            onlineUserSessionIdMap.put(loginId, sidArray);
        } else {
            List<String> sidArray = new ArrayList<>();
            sidArray.add(sid);
            onlineUserSessionIdMap.put(loginId, sidArray);
        }
    }

    /**
     * 清除断线的sid
     *
     * @param sid
     */
    public static void removeUserSession(String sid) {
        WsSession wsSession = onlineSessionMap.get(sid);
        String loginId = wsSession.getLoginId();
        if (onlineUserSessionIdMap.containsKey(loginId)) {
            List<String> sids = onlineUserSessionIdMap.get(loginId);
            sids.remove(sid);
            onlineUserSessionIdMap.put(loginId, sids);
        }
    }

}
