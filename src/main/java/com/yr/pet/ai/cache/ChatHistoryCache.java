package com.yr.pet.ai.cache;


import com.alibaba.fastjson.JSON;
import com.yr.pet.ai.model.entity.SessionDO;
import com.yr.pet.ai.model.req.AiChatMessage;
import jakarta.annotation.Resource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ChatHistoryCache {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 会话超时时间：10分钟
    private static final long SESSION_TIMEOUT_SECONDS = 600;
    // ===== 手动序列化工具 =====
    private byte[] toJsonBytes(Object obj) {
        return JSON.toJSONString(obj).getBytes(StandardCharsets.UTF_8);
    }
    private AiChatMessage fromJsonBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), AiChatMessage.class);
    }

    /**
     * 获取sessionId
     */
    public Long getSessionId(Long userId) {
        String key = getCacheKey(userId);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        //注意手动序列化不能直接redisTemplate.opsForList().index(key, 0);
        // 需要使用execute方法获取原始连接
        List<byte[]> raw = redisTemplate.execute((RedisConnection c) ->
                c.lRange(keyBytes, 0, 0));
        // 反序列化回 AiChatMessage
        if (raw == null || raw.isEmpty()) return null;
        AiChatMessage msg = fromJsonBytes(raw.get(0));
        if (msg == null || msg.getContent() == null) return null;
        return Long.valueOf(msg.getContent());

    }
    /**
     *  userId 用户ID
     * 从缓存获取当前会话内历史对话
     * @return 会话历史消息列表
     */
    public List<AiChatMessage> getHistory(Long userId) {
        String key = getCacheKey(userId);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        List<byte[]> raw = redisTemplate.execute((RedisConnection connection) ->
                connection.lRange(keyBytes, 2, -1)
        );

        if (raw == null || raw.isEmpty()) return new ArrayList<>();

        return raw.stream()
                .map(this::fromJsonBytes)
                .collect(Collectors.toList());
    }
    /**
     *
     *  初始化会话
     * @param sessionDO 会话信息
     * @param userId 用户ID
     */
    public void initSession(SessionDO sessionDO, Long userId) {
        //创建会话 后把sessionID存redis
        AiChatMessage session = new AiChatMessage("SessionId", sessionDO.getSessionId().toString());
        AiChatMessage aiChatMessage = new AiChatMessage(AiChatMessage.ROLE_SYSTEM, AiChatMessage.CONTENT_SYSTEM);
        appendMessage(userId, session);
        appendMessage(userId, aiChatMessage);
    }
    /**
     * @Param userId 用户ID
     * @Param message   消息
     * 追加消息到会话历史并刷新过期时间
     */

    public void appendMessage(Long userId, AiChatMessage message) {
        String key = getCacheKey(userId);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = toJsonBytes(message);

        redisTemplate.execute((RedisConnection connection) -> {
            connection.rPush(keyBytes, valueBytes);
            connection.expire(keyBytes, SESSION_TIMEOUT_SECONDS);
            return null;
        });
    }
    /**
     * 构建缓存Key 一个用户可以有多个会话，但是redis只存储单次会话的对话历史
     * @Param userId 用户ID
     * @return 缓存Key
     */
    private String getCacheKey(Long userId) {
        return "chat:session:" + userId;
    }

}


