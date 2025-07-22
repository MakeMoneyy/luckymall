package com.luckymall.service;

import com.luckymall.service.impl.RedisLockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisLockServiceTest {

    @Autowired
    private RedisLockService lockService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    public void testExecuteWithLock() {
        // 清理可能存在的测试锁
        redisTemplate.delete("lock:test_lock");
        
        String result = lockService.executeWithLock("test_lock", () -> "success");
        
        assertEquals("success", result);
    }
    
    @Test
    public void testConcurrentExecution() throws InterruptedException {
        String lockKey = "concurrent_test_lock";
        
        // 清理可能存在的测试锁
        redisTemplate.delete("lock:" + lockKey);
        
        // 并发线程数
        int threadCount = 10;
        
        // 共享计数器
        AtomicInteger counter = new AtomicInteger(0);
        
        // 线程池和同步器
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 启动多个线程同时尝试获取锁
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    lockService.executeWithLock(lockKey, () -> {
                        // 模拟业务操作
                        int current = counter.incrementAndGet();
                        
                        // 等待一小段时间，增加并发冲突的可能性
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        // 如果锁正常工作，counter的值不会被覆盖
                        assertEquals(current, counter.get());
                        
                        return null;
                    });
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        latch.await();
        executor.shutdown();
        
        // 验证最终计数器的值
        assertEquals(threadCount, counter.get());
    }
} 