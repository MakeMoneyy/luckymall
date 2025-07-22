package com.luckymall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存一致性服务
 * 提供缓存与数据库一致性保障策略
 */
@Slf4j
@Service
public class CacheConsistencyService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ScheduledThreadPoolExecutor delayExecutor;
    
    @Autowired
    private CacheProtectionService cacheProtectionService;
    
    /**
     * Cache Aside Pattern：先更新数据库，再删除缓存
     * 适用于读多写少场景
     * @param key 缓存键
     * @param updateAction 数据库更新操作
     */
    public void updateWithCacheAside(String key, Runnable updateAction) {
        try {
            // 1. 更新数据库
            updateAction.run();
            log.debug("CacheAside: 更新数据库成功 - {}", key);
            
            // 2. 删除缓存
            cacheProtectionService.deleteCache(key);
        } catch (Exception e) {
            log.error("CacheAside: 更新失败 - {}", key, e);
            throw e;
        }
    }
    
    /**
     * Cache Aside Pattern：先更新数据库，再删除缓存，带返回值
     * @param key 缓存键
     * @param updateAction 数据库更新操作
     * @return 操作结果
     */
    public <T> T updateWithCacheAsideWithResult(String key, Supplier<T> updateAction) {
        try {
            // 1. 更新数据库
            T result = updateAction.get();
            log.debug("CacheAside: 更新数据库成功 - {}", key);
            
            // 2. 删除缓存
            cacheProtectionService.deleteCache(key);
            
            return result;
        } catch (Exception e) {
            log.error("CacheAside: 更新失败 - {}", key, e);
            throw e;
        }
    }
    
    /**
     * 延迟双删：删缓存 -> 更新数据库 -> 延迟一段时间 -> 再次删除缓存
     * 适用于对一致性要求较高的场景
     * @param key 缓存键
     * @param updateAction 数据库更新操作
     * @param delayMs 延迟时间(毫秒)
     */
    public void updateWithDelayedDoubleDelete(String key, Runnable updateAction, long delayMs) {
        try {
            // 1. 先删除缓存
            cacheProtectionService.deleteCache(key);
            log.debug("DelayedDoubleDelete: 第一次删除缓存 - {}", key);
            
            // 2. 更新数据库
            updateAction.run();
            log.debug("DelayedDoubleDelete: 更新数据库成功 - {}", key);
            
            // 3. 延迟一段时间后再次删除缓存
            delayExecutor.schedule(() -> {
                try {
                    cacheProtectionService.deleteCache(key);
                    log.debug("DelayedDoubleDelete: 延迟{}ms后第二次删除缓存 - {}", delayMs, key);
                } catch (Exception e) {
                    log.error("DelayedDoubleDelete: 延迟删除缓存失败 - {}", key, e);
                }
            }, delayMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("DelayedDoubleDelete: 更新失败 - {}", key, e);
            throw e;
        }
    }
    
    /**
     * Write Through：直写模式，同时更新数据库和缓存
     * 适用于读写比较均衡的场景
     * @param key 缓存键
     * @param updateAction 数据库更新操作（返回更新后的值）
     * @param expireTime 缓存过期时间
     * @param timeUnit 时间单位
     */
    public <T> T updateWithWriteThrough(String key, Supplier<T> updateAction, long expireTime, TimeUnit timeUnit) {
        try {
            // 1. 更新数据库并获取最新值
            T latestValue = updateAction.get();
            log.debug("WriteThrough: 更新数据库成功 - {}", key);
            
            // 2. 更新缓存
            if (latestValue != null) {
                cacheProtectionService.updateCache(key, latestValue, expireTime, timeUnit);
                log.debug("WriteThrough: 更新缓存成功 - {}", key);
            } else {
                cacheProtectionService.deleteCache(key);
                log.debug("WriteThrough: 删除缓存 (值为null) - {}", key);
            }
            
            return latestValue;
        } catch (Exception e) {
            log.error("WriteThrough: 更新失败 - {}", key, e);
            throw e;
        }
    }
} 