package com.sz.redis;

import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.TransferMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * websocket 用户管理工具
 *
 * @author sz
 * @date 2023/9/5 14:05
 */
@Service
@RequiredArgsConstructor
public class WebsocketRedisService {

    public final static String WEBSOCKET_ONLINE_SID = "socket:online:sid";

    public final static String WEBSOCKET_ONLINE_USER = "socket:online:user";

    private final RedisTemplate redisTemplate;

    /**
     * 用户上线
     *
     * @param loginId
     */
    public void addUserToOnlineChat(String sessionId, String loginId) {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;
        String onlineSidKey = WEBSOCKET_ONLINE_SID;

        // 检查用户名是否已存在于在线用户哈希表中
        if (redisTemplate.opsForHash().hasKey(onlineUserKey, loginId)) {
            // 如果存在，获取sid列表并更新
            List<String> sids = (List<String>) redisTemplate.opsForHash().get(onlineUserKey, loginId);
            sids.add(sessionId);
            redisTemplate.opsForHash().put(onlineUserKey, loginId, sids);
        } else {
            // 如果不存在，创建一个包含当前sid的新列表
            List<String> sids = new ArrayList<>(1);
            sids.add(sessionId);
            redisTemplate.opsForHash().put(onlineUserKey, loginId, sids);
        }

        // 更新sid映射
        redisTemplate.opsForHash().put(onlineSidKey, sessionId, loginId);
    }

    /**
     * 用户下线
     *
     * @param sessionId
     * @param loginId
     */
    public void removeUser(String sessionId, String loginId) {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;
        String onlineSidKey = WEBSOCKET_ONLINE_SID;

        // 检查用户名是否存在于在线用户哈希表中
        if (redisTemplate.opsForHash().hasKey(onlineUserKey, loginId)) {
            // 如果存在，获取sid列表并移除当前sessionId
            List<String> sids = (List<String>) redisTemplate.opsForHash().get(onlineUserKey, loginId);
            sids.remove(sessionId);

            // 如果用户不再有任何会话，则从在线用户列表中删除
            if (sids.isEmpty()) {
                redisTemplate.opsForHash().delete(onlineUserKey, loginId);
            } else {
                redisTemplate.opsForHash().put(onlineUserKey, loginId, sids);
            }
        }

        // 删除sessionId映射
        redisTemplate.opsForHash().delete(onlineSidKey, sessionId);
    }

    /**
     * 用户下线 - 根据sessionId
     *
     * @param sessionId
     */
    public void removeUserBySessionId(String sessionId) {
        // 查找sessionId对应的用户名
        String loginId = (String) redisTemplate.opsForHash().get(WEBSOCKET_ONLINE_SID, sessionId);
        if (loginId != null) {
            // 从在线用户列表中删除sessionId
            if (redisTemplate.opsForHash().hasKey(WEBSOCKET_ONLINE_USER, loginId)) {
                List<String> sids = (List<String>) redisTemplate.opsForHash().get(WEBSOCKET_ONLINE_USER, loginId);
                System.out.println("sids ==" + sids);
                sids.remove(sessionId);

                // 如果用户不再有任何会话，则从在线用户列表中删除该用户
                if (sids.isEmpty()) {
                    redisTemplate.opsForHash().delete(WEBSOCKET_ONLINE_USER, loginId);
                } else {
                    redisTemplate.opsForHash().put(WEBSOCKET_ONLINE_USER, loginId, sids);
                }
            }

            // 删除sessionId映射
            redisTemplate.opsForHash().delete(WEBSOCKET_ONLINE_SID, sessionId);
        }
    }

    /**
     * 用户下线 - 根据sessionId
     *
     * @param loginId
     */
    public void removeUserByUsername(String loginId) {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;
        String onlineSidKey = WEBSOCKET_ONLINE_SID;

        // 检查用户名是否存在于在线用户哈希表中
        if (redisTemplate.opsForHash().hasKey(onlineUserKey, loginId)) {
            // 获取该用户的所有连接sessionId
            List<String> sids = (List<String>) redisTemplate.opsForHash().get(onlineUserKey, loginId);

            // 从在线用户列表中删除该用户
            redisTemplate.opsForHash().delete(onlineUserKey, loginId);

            // 删除与该用户相关的所有sessionId映射
            for (String sid : sids) {
                redisTemplate.opsForHash().delete(onlineSidKey, sid);
            }
        }
    }

    /**
     * 根据sessionId查询loginId
     *
     * @param sessionId
     */
    public String getUserBySessionId(String sessionId) {
        String onlineSidKey = WEBSOCKET_ONLINE_SID;
        // 查找sessionId对应的用户名
        String loginId = (String) redisTemplate.opsForHash().get(onlineSidKey, sessionId);
        if (loginId != null) {
            return loginId;
        } else {
            return null;
        }
    }

    /**
     * 查询在线用户数
     *
     * @return
     */
    public long getOnlineUserCount() {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;

        // 获取在线用户列表的大小，即在线用户数
        Long onlineUserCount = redisTemplate.opsForHash().size(onlineUserKey);

        return onlineUserCount != null ? onlineUserCount : 0;
    }

    /**
     * 查询websocket连接数
     *
     * @return
     */
    public long getConnectionCount() {
        String onlineSidKey = WEBSOCKET_ONLINE_SID;

        // 获取在线连接数，即sessionId映射的数量
        Long connectionCount = redisTemplate.opsForHash().size(onlineSidKey);

        return connectionCount != null ? connectionCount : 0;
    }

    /**
     * 查询某个用户的连接数（在几处登陆）
     *
     * @param loginId
     * @return
     */
    public long getUserConnectionCount(String loginId) {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;

        // 检查用户名是否存在于在线用户哈希表中
        if (redisTemplate.opsForHash().hasKey(onlineUserKey, loginId)) {
            // 如果存在，获取该用户的连接数
            List<String> sids = (List<String>) redisTemplate.opsForHash().get(onlineUserKey, loginId);
            return sids.size();
        } else {
            // 如果用户不存在或没有连接，返回 0
            return 0;
        }
    }

    /**
     * 获取所有在线用户名列表
     *
     * @return
     */
    public List<String> getAllOnlineUsernames() {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;
        Set<Object> loginIds = redisTemplate.opsForHash().keys(onlineUserKey);
        return new ArrayList<>(loginIds.stream().map(Object::toString).collect(Collectors.toList()));
    }

    /**
     * 获取用户的所有连接 sessionId 列表
     *
     * @param loginId
     * @return
     */
    public List<String> getUserSessionIds(String loginId) {
        String onlineUserKey = WEBSOCKET_ONLINE_USER;

        if (redisTemplate.opsForHash().hasKey(onlineUserKey, loginId)) {
            List<String> sids = (List<String>) redisTemplate.opsForHash().get(onlineUserKey, loginId);
            return new ArrayList<>(sids);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 透传 websocket 给 service 服务
     *
     * @param transferMessage
     */
    public void sendWsToService(TransferMessage transferMessage) {
        redisTemplate.convertAndSend(GlobalConstant.WS_TO_SERVICE, transferMessage);
    }

    /**
     * 透传 service 给 websocket服务
     *
     * @param transferMessage
     */
    public void sendServiceToWs(TransferMessage transferMessage) {
        redisTemplate.convertAndSend(GlobalConstant.SERVICE_TO_WS, transferMessage);
    }

}
