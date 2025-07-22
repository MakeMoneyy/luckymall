package com.luckymall.service.impl;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.dto.EmotionAnalysisResult;
import com.luckymall.dto.IntentRecognitionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 聊天缓存服务
 * 实现多级缓存策略，提高响应速度
 */
@Service
@Slf4j
public class ChatCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 本地内存缓存 (一级缓存) - 使用Object类型以支持不同类型的缓存条目
    private final Map<String, CacheEntry<?>> localCache = new ConcurrentHashMap<>();
    
    // 缓存配置
    private static final long LOCAL_CACHE_EXPIRE_MINUTES = 10; // 本地缓存过期时间
    private static final long REDIS_CACHE_EXPIRE_HOURS = 24;   // Redis缓存过期时间
    private static final int LOCAL_CACHE_MAX_SIZE = 1000;      // 本地缓存最大条目数
    
    // 缓存前缀
    private static final String CHAT_CACHE_PREFIX = "chat:response:";
    private static final String INTENT_CACHE_PREFIX = "chat:intent:";
    private static final String EMOTION_CACHE_PREFIX = "chat:emotion:";
    
    /**
     * 获取聊天响应缓存
     * @param request 聊天请求
     * @return 缓存的响应，如果不存在则返回null
     */
    public ChatResponse getChatResponseCache(ChatRequest request) {
        // 构建缓存键
        String cacheKey = buildChatCacheKey(request);
        
        // 1. 检查本地缓存
        CacheEntry<?> localEntry = localCache.get(cacheKey);
        if (localEntry != null && !localEntry.isExpired()) {
            log.debug("本地缓存命中: {}", cacheKey);
            return (ChatResponse) localEntry.getValue();
        }
        
        // 2. 检查Redis缓存
        Object redisValue = redisTemplate.opsForValue().get(cacheKey);
        if (redisValue instanceof ChatResponse) {
            ChatResponse response = (ChatResponse) redisValue;
            log.debug("Redis缓存命中: {}", cacheKey);
            
            // 更新本地缓存
            updateLocalCache(cacheKey, response);
            
            return response;
        }
        
        return null;
    }
    
    /**
     * 保存聊天响应缓存
     * @param request 聊天请求
     * @param response 聊天响应
     */
    public void saveChatResponseCache(ChatRequest request, ChatResponse response) {
        String cacheKey = buildChatCacheKey(request);
        
        // 保存到本地缓存
        updateLocalCache(cacheKey, response);
        
        // 保存到Redis缓存
        redisTemplate.opsForValue().set(cacheKey, response, REDIS_CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        log.debug("保存缓存: {}", cacheKey);
    }
    
    /**
     * 获取意图识别结果缓存
     * @param message 用户消息
     * @return 缓存的意图识别结果，如果不存在则返回null
     */
    public IntentRecognitionResult getIntentCache(String message) {
        String cacheKey = INTENT_CACHE_PREFIX + message.hashCode();
        
        // 1. 检查本地缓存
        CacheEntry<?> localEntry = localCache.get(cacheKey);
        if (localEntry != null && !localEntry.isExpired() && localEntry.getValue() instanceof IntentRecognitionResult) {
            log.debug("本地意图缓存命中: {}", cacheKey);
            return (IntentRecognitionResult) localEntry.getValue();
        }
        
        // 2. 检查Redis缓存
        Object redisValue = redisTemplate.opsForValue().get(cacheKey);
        if (redisValue instanceof IntentRecognitionResult) {
            IntentRecognitionResult result = (IntentRecognitionResult) redisValue;
            
            // 更新本地缓存
            localCache.put(cacheKey, new CacheEntry<>(result));
            
            return result;
        }
        
        return null;
    }
    
    /**
     * 保存意图识别结果缓存
     * @param message 用户消息
     * @param result 意图识别结果
     */
    public void saveIntentCache(String message, IntentRecognitionResult result) {
        String cacheKey = INTENT_CACHE_PREFIX + message.hashCode();
        
        // 保存到本地缓存
        localCache.put(cacheKey, new CacheEntry<>(result));
        
        // 保存到Redis缓存
        redisTemplate.opsForValue().set(cacheKey, result, 12, TimeUnit.HOURS);
    }
    
    /**
     * 获取情感分析结果缓存
     * @param message 用户消息
     * @return 缓存的情感分析结果，如果不存在则返回null
     */
    public EmotionAnalysisResult getEmotionCache(String message) {
        String cacheKey = EMOTION_CACHE_PREFIX + message.hashCode();
        
        // 1. 检查本地缓存
        CacheEntry<?> localEntry = localCache.get(cacheKey);
        if (localEntry != null && !localEntry.isExpired() && localEntry.getValue() instanceof EmotionAnalysisResult) {
            log.debug("本地情感缓存命中: {}", cacheKey);
            return (EmotionAnalysisResult) localEntry.getValue();
        }
        
        // 2. 检查Redis缓存
        Object redisValue = redisTemplate.opsForValue().get(cacheKey);
        if (redisValue instanceof EmotionAnalysisResult) {
            EmotionAnalysisResult result = (EmotionAnalysisResult) redisValue;
            
            // 更新本地缓存
            localCache.put(cacheKey, new CacheEntry<>(result));
            
            return result;
        }
        
        return null;
    }
    
    /**
     * 保存情感分析结果缓存
     * @param message 用户消息
     * @param result 情感分析结果
     */
    public void saveEmotionCache(String message, EmotionAnalysisResult result) {
        String cacheKey = EMOTION_CACHE_PREFIX + message.hashCode();
        
        // 保存到本地缓存
        localCache.put(cacheKey, new CacheEntry<>(result));
        
        // 保存到Redis缓存
        redisTemplate.opsForValue().set(cacheKey, result, 12, TimeUnit.HOURS);
    }
    
    /**
     * 清理过期缓存
     * 定期执行，清理本地缓存中的过期条目
     */
    public void cleanExpiredCache() {
        int beforeSize = localCache.size();
        localCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int afterSize = localCache.size();
        
        log.debug("清理过期缓存: 清理前 {} 条，清理后 {} 条，清理了 {} 条", 
                beforeSize, afterSize, (beforeSize - afterSize));
    }
    
    /**
     * 构建聊天缓存键
     * @param request 聊天请求
     * @return 缓存键
     */
    private String buildChatCacheKey(ChatRequest request) {
        StringBuilder keyBuilder = new StringBuilder(CHAT_CACHE_PREFIX);
        keyBuilder.append(request.getMessage().hashCode());
        
        // 添加上下文信息到键中
        if (request.getContext() != null && !request.getContext().isEmpty()) {
            keyBuilder.append(":");
            
            // 只选取关键上下文信息，避免键过长
            if (request.getContext().containsKey("currentPage")) {
                keyBuilder.append("page=").append(request.getContext().get("currentPage"));
            }
            
            if (request.getContext().containsKey("productId")) {
                keyBuilder.append(",pid=").append(request.getContext().get("productId"));
            }
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 更新本地缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    private <T> void updateLocalCache(String key, T value) {
        // 如果本地缓存过大，先清理一部分
        if (localCache.size() >= LOCAL_CACHE_MAX_SIZE) {
            // 简单策略：清理20%的缓存
            int toRemove = LOCAL_CACHE_MAX_SIZE / 5;
            int removed = 0;
            
            for (String cacheKey : localCache.keySet()) {
                localCache.remove(cacheKey);
                removed++;
                if (removed >= toRemove) break;
            }
            
            log.debug("本地缓存达到上限，清理了 {} 条缓存", removed);
        }
        
        localCache.put(key, new CacheEntry<>(value));
    }
    
    /**
     * 缓存条目，包含值和过期时间
     * @param <T> 缓存值类型
     */
    private static class CacheEntry<T> {
        private final T value;
        private final long expireTime;
        
        public CacheEntry(T value) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(LOCAL_CACHE_EXPIRE_MINUTES);
        }
        
        public T getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
} 