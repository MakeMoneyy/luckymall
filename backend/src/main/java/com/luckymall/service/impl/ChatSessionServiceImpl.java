package com.luckymall.service.impl;

import com.luckymall.dto.ChatContext;
import com.luckymall.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 聊天会话服务实现类
 */
@Service
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String SESSION_CONTEXT_PREFIX = "chat:context:";
    private static final long SESSION_EXPIRE_HOURS = 24;

    @Override
    public ChatContext getSessionContext(String userId, String sessionId) {
        String contextKey = buildContextKey(userId, sessionId);
        try {
            Object cachedContext = redisTemplate.opsForValue().get(contextKey);
            if (cachedContext != null && cachedContext instanceof ChatContext) {
                log.debug("获取会话上下文: {}", contextKey);
                return (ChatContext) cachedContext;
            }
        } catch (Exception e) {
            log.error("获取会话上下文失败: {}", e.getMessage(), e);
        }
        
        // 如果不存在，创建新的上下文
        ChatContext context = ChatContext.builder()
                .userId(userId)
                .sessionId(sessionId)
                .lastInteractionTimestamp(System.currentTimeMillis())
                .build();
        
        saveSessionContext(userId, sessionId, context);
        return context;
    }

    @Override
    public int incrementPromotionCount(String userId, String sessionId) {
        ChatContext context = getSessionContext(userId, sessionId);
        context.setPromotionCount(context.getPromotionCount() + 1);
        context.setLastPromotionTimestamp(System.currentTimeMillis());
        saveSessionContext(userId, sessionId, context);
        return context.getPromotionCount();
    }

    @Override
    public void setPromotionRejected(String userId, String sessionId, boolean rejected) {
        ChatContext context = getSessionContext(userId, sessionId);
        context.setPromotionRejected(rejected);
        saveSessionContext(userId, sessionId, context);
    }

    @Override
    public void saveSessionContext(String userId, String sessionId, ChatContext context) {
        String contextKey = buildContextKey(userId, sessionId);
        try {
            context.setLastInteractionTimestamp(System.currentTimeMillis());
            redisTemplate.opsForValue().set(contextKey, context, SESSION_EXPIRE_HOURS, TimeUnit.HOURS);
            log.debug("保存会话上下文: {}", contextKey);
        } catch (Exception e) {
            log.error("保存会话上下文失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 构建上下文键
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 上下文键
     */
    private String buildContextKey(String userId, String sessionId) {
        return SESSION_CONTEXT_PREFIX + userId + ":" + sessionId;
    }
} 