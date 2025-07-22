package com.luckymall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存防护服务
 * 提供缓存击穿、缓存穿透防护功能
 */
@Slf4j
@Service
public class CacheProtectionService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisLockService lockService;
    
    // 空值占位符
    private static final EmptyValue EMPTY_VALUE = new EmptyValue();
    
    // 空值缓存TTL（秒）
    private static final long EMPTY_CACHE_TTL = 60;
    
    /**
     * 防击穿地获取缓存数据
     * @param key 缓存键
     * @param dbFallback 数据库回源函数
     * @param expireTime 缓存过期时间
     * @param timeUnit 时间单位
     * @return 缓存数据
     */
    public <T> T getWithMutex(String key, Function<String, T> dbFallback, long expireTime, TimeUnit timeUnit) {
        // 1. 从缓存获取数据
        Object value = redisTemplate.opsForValue().get(key);
        
        // 2. 判断是否为空值占位符
        if (value instanceof EmptyValue) {
            return null;
        }
        
        // 3. 缓存命中
        if (value != null) {
            log.debug("缓存命中: {}", key);
            return (T) value;
        }
        
        // 4. 缓存未命中，使用分布式锁防止击穿
        String lockKey = "mutex:cache:" + key;
        return lockService.executeWithLock(lockKey, () -> {
            // 双重检查，再次从缓存获取
            Object cachedValue = redisTemplate.opsForValue().get(key);
            
            // 判断是否为空值占位符
            if (cachedValue instanceof EmptyValue) {
                return null;
            }
            
            // 缓存命中
            if (cachedValue != null) {
                log.debug("二次检查缓存命中: {}", key);
                return (T) cachedValue;
            }
            
            // 从数据源获取数据
            log.debug("缓存未命中，回源查询: {}", key);
            T dbValue = dbFallback.apply(key);
            
            // 将数据放入缓存
            if (dbValue != null) {
                redisTemplate.opsForValue().set(key, dbValue, expireTime, timeUnit);
                log.debug("回源数据写入缓存: {}", key);
            } else {
                // 缓存空值，避免缓存穿透，但使用较短的过期时间
                redisTemplate.opsForValue().set(key, EMPTY_VALUE, EMPTY_CACHE_TTL, TimeUnit.SECONDS);
                log.debug("缓存空值防穿透: {}", key);
            }
            
            return dbValue;
        });
    }
    
    /**
     * 更新缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     */
    public <T> void updateCache(String key, T value, long expireTime, TimeUnit timeUnit) {
        if (value != null) {
            redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
            log.debug("更新缓存: {}", key);
        } else {
            redisTemplate.opsForValue().set(key, EMPTY_VALUE, EMPTY_CACHE_TTL, TimeUnit.SECONDS);
            log.debug("更新缓存(空值): {}", key);
        }
    }
    
    /**
     * 删除缓存
     * @param key 缓存键
     */
    public void deleteCache(String key) {
        redisTemplate.delete(key);
        log.debug("删除缓存: {}", key);
    }
    
    /**
     * 清除指定前缀的所有缓存
     * @param keyPrefix 缓存键前缀
     * @return 清除的缓存数量
     */
    public long clearCacheByPrefix(String keyPrefix) {
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if (keys != null && !keys.isEmpty()) {
            long count = redisTemplate.delete(keys);
            log.info("清除前缀为{}的缓存，共{}条", keyPrefix, count);
            return count;
        }
        return 0;
    }
    
    /**
     * 空值占位符类，用于缓存穿透保护
     */
    private static class EmptyValue implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @Override
        public String toString() {
            return "EMPTY_CACHE_VALUE";
        }
    }
} 