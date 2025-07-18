package com.luckymall.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.luckymall.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 聊天缓存服务
 */
@Service
@Slf4j
public class ChatCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_CACHE_PREFIX = "chat:response:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    /**
     * 从缓存中获取聊天响应
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param message   消息内容
     * @return 缓存的聊天响应，如果不存在则返回null
     */
    public ChatResponse getFromCache(String userId, String sessionId, String message) {
        String cacheKey = buildCacheKey(userId, sessionId, message);
        try {
            Object cachedResponse = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResponse != null && cachedResponse instanceof ChatResponse) {
                log.debug("缓存命中: {}", cacheKey);
                ChatResponse response = (ChatResponse) cachedResponse;
                response.setCacheHit(true);
                return response;
            }
        } catch (Exception e) {
            log.error("从缓存获取聊天响应失败: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将聊天响应保存到缓存
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param message   消息内容
     * @param response  聊天响应
     */
    public void saveToCache(String userId, String sessionId, String message, ChatResponse response) {
        String cacheKey = buildCacheKey(userId, sessionId, message);
        try {
            redisTemplate.opsForValue().set(cacheKey, response, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            log.debug("聊天响应已缓存: {}", cacheKey);
        } catch (Exception e) {
            log.error("缓存聊天响应失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 构建缓存键
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param message   消息内容
     * @return 缓存键
     */
    private String buildCacheKey(String userId, String sessionId, String message) {
        return CHAT_CACHE_PREFIX + DigestUtils.md5Hex(userId + ":" + sessionId + ":" + message);
    }
} 