package com.luckymall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis分布式锁服务
 */
@Slf4j
@Service
public class RedisLockService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_TIMEOUT = 30000; // 30秒
    private static final long MAX_WAIT_TIME = 3000; // 最大等待时间3秒
    private static final long RETRY_INTERVAL = 100; // 重试间隔100毫秒
    
    /**
     * 获取分布式锁
     * @param key 锁标识
     * @param value 锁值（用于标识锁持有者）
     * @param timeout 锁超时时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String key, String value, long timeout) {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(
            redisTemplate.opsForValue().setIfAbsent(lockKey, value, timeout, TimeUnit.MILLISECONDS)
        );
    }
    
    /**
     * 释放分布式锁
     * @param key 锁标识
     * @param value 锁值（必须与获取锁时的值相同）
     * @return 是否释放成功
     */
    public boolean releaseLock(String key, String value) {
        String lockKey = LOCK_PREFIX + key;
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(lockKey),
            value
        );
        
        return result != null && result == 1L;
    }
    
    /**
     * 使用分布式锁执行操作
     * @param key 锁标识
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public <T> T executeWithLock(String key, Supplier<T> action) {
        return executeWithLock(key, DEFAULT_LOCK_TIMEOUT, action);
    }
    
    /**
     * 使用分布式锁执行操作（可指定超时时间）
     * @param key 锁标识
     * @param timeout 锁超时时间（毫秒）
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public <T> T executeWithLock(String key, long timeout, Supplier<T> action) {
        String value = UUID.randomUUID().toString();
        boolean locked = false;
        
        try {
            // 尝试获取锁，如果失败则重试
            long startTime = System.currentTimeMillis();
            while (!locked && System.currentTimeMillis() - startTime < MAX_WAIT_TIME) {
                locked = tryLock(key, value, timeout);
                if (!locked) {
                    // 等待一段时间后重试
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("获取锁被中断: " + key, e);
                    }
                }
            }
            
            if (!locked) {
                throw new RuntimeException("获取锁超时: " + key);
            }
            
            log.debug("获取锁成功: {}", key);
            // 执行业务逻辑
            return action.get();
        } finally {
            // 释放锁
            if (locked) {
                boolean released = releaseLock(key, value);
                if (released) {
                    log.debug("释放锁成功: {}", key);
                } else {
                    log.warn("释放锁失败: {}", key);
                }
            }
        }
    }
    
    /**
     * 无返回值的锁执行方法
     * @param key 锁标识
     * @param action 要执行的操作
     */
    public void executeWithLockVoid(String key, Runnable action) {
        executeWithLock(key, () -> {
            action.run();
            return null;
        });
    }
} 