package com.sz.redis;

import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.TransferMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * websocket 在线状态管理（Redis）
 * <p>
 * 数据结构（v2.0 重构后）：
 * 
 * <pre>
 * socket:online:sid:{sid}        String   value=loginId               每连接 1 key，带 TTL
 * socket:online:user:{loginId}   Set      members=sid 列表             用户维度，{@code
 * SADD
 * }/{@code
 * SREM
 * } 原子
 * socket:online:node:{nodeId}    Set      members=本节点持有的 sid 列表 节点维度，进程启停时清理
 * </pre>
 * <p>
 * 设计要点：
 * <ul>
 * <li><b>原子性</b>：所有读-改-写场景改用 Redis 单命令（SADD/SREM/DEL），消除旧 Hash+List 结构的
 * check-then-act 并发缺陷</li>
 * <li><b>可清理性</b>：sid String 与 user Set 均设 TTL；启动时按 nodeId 主动清理本节点上次残留</li>
 * <li><b>双维度定位</b>：sid → loginId（GET）、loginId → sid 集合（SMEMBERS），均
 * O(1)/O(N)</li>
 * <li><b>不兼容</b>：与旧 Hash 结构数据不互通；升级期需要清空旧 key 或滚动重启全部节点</li>
 * </ul>
 *
 * @author sz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebsocketRedisService {

    /** sid → loginId 主索引前缀 */
    public static final String WEBSOCKET_ONLINE_SID_PREFIX = "socket:online:sid:";

    /** loginId → sid Set 前缀 */
    public static final String WEBSOCKET_ONLINE_USER_PREFIX = "socket:online:user:";

    /** nodeId → 本节点 sid Set 前缀 */
    public static final String WEBSOCKET_ONLINE_NODE_PREFIX = "socket:online:node:";

    /** 在线状态 key 的 TTL（7 天），防止异常退出后无限残留 */
    private static final long ONLINE_TTL_SECONDS = 7 * 24 * 60 * 60L;

    private final RedisTemplate<Object, Object> redisTemplate;

    @Value("${spring.application.name:sz-service-websocket}")
    private String applicationName;

    /** 当前节点标识：appName@host#pid，进程级唯一 */
    private String nodeId;

    @PostConstruct
    public void init() {
        this.nodeId = buildNodeId();
        // 启动时清理本节点上次残留（极端情况：kill -9 后重启）
        try {
            cleanupNode(nodeId);
            log.info("【websocket】节点启动清理完成，nodeId=[{}]", nodeId);
        } catch (Exception e) {
            log.warn("【websocket】节点启动清理异常，nodeId=[{}], err={}", nodeId, e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            cleanupNode(nodeId);
            log.info("【websocket】节点停机清理完成，nodeId=[{}]", nodeId);
        } catch (Exception e) {
            log.warn("【websocket】节点停机清理异常，nodeId=[{}], err={}", nodeId, e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 写操作

    /**
     * 用户上线：登记 sid → loginId、loginId → {sid}、node → {sid} 三个索引
     */
    public void addUserToOnlineChat(String sessionId, String loginId) {
        String sidKey = sidKey(sessionId);
        String userKey = userKey(loginId);
        String nodeKey = nodeKey(nodeId);

        redisTemplate.opsForValue().set(sidKey, loginId, ONLINE_TTL_SECONDS, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(userKey, sessionId);
        redisTemplate.expire(userKey, ONLINE_TTL_SECONDS, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(nodeKey, sessionId);
        redisTemplate.expire(nodeKey, ONLINE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 用户下线 - 同时已知 sessionId 和 loginId
     */
    public void removeUser(String sessionId, String loginId) {
        removeUserInternal(sessionId, loginId);
    }

    /**
     * 用户下线 - 仅知 sessionId，自动反查 loginId
     */
    public void removeUserBySessionId(String sessionId) {
        String loginId = (String) redisTemplate.opsForValue().get(sidKey(sessionId));
        removeUserInternal(sessionId, loginId);
    }

    /**
     * 用户下线 - 清除该用户的所有连接
     */
    public void removeUserByUsername(String loginId) {
        String userKey = userKey(loginId);
        Set<Object> sids = redisTemplate.opsForSet().members(userKey);
        if (sids != null) {
            for (Object sid : sids) {
                String sidStr = String.valueOf(sid);
                redisTemplate.delete(sidKey(sidStr));
                redisTemplate.opsForSet().remove(nodeKey(nodeId), sidStr);
            }
        }
        redisTemplate.delete(userKey);
    }

    private void removeUserInternal(String sessionId, String loginId) {
        if (loginId != null) {
            String userKey = userKey(loginId);
            redisTemplate.opsForSet().remove(userKey, sessionId);
            // 用户 Set 为空时连同 key 一起回收
            Long size = redisTemplate.opsForSet().size(userKey);
            if (size != null && size == 0L) {
                redisTemplate.delete(userKey);
            }
        }
        redisTemplate.delete(sidKey(sessionId));
        redisTemplate.opsForSet().remove(nodeKey(nodeId), sessionId);
    }

    // ---------------------------------------------------------------- 读操作

    /**
     * 根据 sessionId 查询 loginId
     */
    public String getUserBySessionId(String sessionId) {
        Object val = redisTemplate.opsForValue().get(sidKey(sessionId));
        return val == null ? null : val.toString();
    }

    /**
     * 在线用户数（不同 loginId 数量）
     * <p>
     * 通过 SCAN 实现，避免 KEYS 阻塞。仅用于监控/管理，非热路径。
     */
    public long getOnlineUserCount() {
        return countByPrefix(WEBSOCKET_ONLINE_USER_PREFIX + "*");
    }

    /**
     * WebSocket 连接数（不同 sid 数量）
     */
    public long getConnectionCount() {
        return countByPrefix(WEBSOCKET_ONLINE_SID_PREFIX + "*");
    }

    /**
     * 某个用户的连接数（多端登录数）
     */
    public long getUserConnectionCount(String loginId) {
        Long size = redisTemplate.opsForSet().size(userKey(loginId));
        return size == null ? 0L : size;
    }

    /**
     * 获取所有在线用户 loginId 列表（SCAN 实现，非热路径）
     */
    public List<String> getAllOnlineUsernames() {
        List<String> result = new ArrayList<>();
        scanKeys(WEBSOCKET_ONLINE_USER_PREFIX + "*", key -> {
            // 截取前缀后的 loginId
            result.add(key.substring(WEBSOCKET_ONLINE_USER_PREFIX.length()));
        });
        return result;
    }

    /**
     * 获取用户的所有连接 sessionId 列表
     */
    public List<String> getUserSessionIds(String loginId) {
        Set<Object> members = redisTemplate.opsForSet().members(userKey(loginId));
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> sids = new ArrayList<>(members.size());
        for (Object m : members) {
            sids.add(String.valueOf(m));
        }
        return sids;
    }

    // ---------------------------------------------------------------- pub/sub

    /**
     * 透传 websocket 给 service 服务
     */
    public void sendWsToService(TransferMessage transferMessage) {
        redisTemplate.convertAndSend(GlobalConstant.WS_TO_SERVICE, transferMessage);
    }

    /**
     * 透传 service 给 websocket 服务
     */
    public void sendServiceToWs(TransferMessage transferMessage) {
        redisTemplate.convertAndSend(GlobalConstant.SERVICE_TO_WS, transferMessage);
    }

    // ---------------------------------------------------------------- 内部工具

    private String sidKey(String sessionId) {
        return WEBSOCKET_ONLINE_SID_PREFIX + sessionId;
    }

    private String userKey(String loginId) {
        return WEBSOCKET_ONLINE_USER_PREFIX + loginId;
    }

    private String nodeKey(String node) {
        return WEBSOCKET_ONLINE_NODE_PREFIX + node;
    }

    /**
     * 清理指定节点登记过的所有 sid（连同其 sid String 与 user Set 中的成员）
     */
    private void cleanupNode(String node) {
        String nKey = nodeKey(node);
        Set<Object> sids = redisTemplate.opsForSet().members(nKey);
        if (sids == null || sids.isEmpty()) {
            redisTemplate.delete(nKey);
            return;
        }
        for (Object sidObj : sids) {
            String sid = String.valueOf(sidObj);
            // 反查 loginId 同步清 user Set
            Object loginIdObj = redisTemplate.opsForValue().get(sidKey(sid));
            if (loginIdObj != null) {
                String userKey = userKey(loginIdObj.toString());
                redisTemplate.opsForSet().remove(userKey, sid);
                Long left = redisTemplate.opsForSet().size(userKey);
                if (left != null && left == 0L) {
                    redisTemplate.delete(userKey);
                }
            }
            redisTemplate.delete(sidKey(sid));
        }
        redisTemplate.delete(nKey);
    }

    private long countByPrefix(String pattern) {
        long count = 0;
        try (Cursor<byte[]> cursor = scanCursor(pattern)) {
            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
        } catch (Exception e) {
            log.warn("【websocket】扫描 key 计数异常, pattern=[{}], err={}", pattern, e.getMessage());
        }
        return count;
    }

    private void scanKeys(String pattern, java.util.function.Consumer<String> consumer) {
        try (Cursor<byte[]> cursor = scanCursor(pattern)) {
            while (cursor.hasNext()) {
                consumer.accept(new String(cursor.next()));
            }
        } catch (Exception e) {
            log.warn("【websocket】扫描 key 异常, pattern=[{}], err={}", pattern, e.getMessage());
        }
    }

    private Cursor<byte[]> scanCursor(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(200).build();
        return redisTemplate.getRequiredConnectionFactory().getConnection().keyCommands().scan(options);
    }

    private String buildNodeId() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            host = "unknown-host";
        }
        String jvmName = ManagementFactory.getRuntimeMXBean().getName(); // pid@host
        String pid = jvmName.contains("@") ? jvmName.substring(0, jvmName.indexOf('@')) : jvmName;
        return applicationName + "@" + host + "#" + pid;
    }

}
