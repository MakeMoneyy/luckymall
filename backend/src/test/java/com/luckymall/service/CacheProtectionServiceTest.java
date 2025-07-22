package com.luckymall.service;

import com.luckymall.service.impl.CacheProtectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CacheProtectionServiceTest {

    @Autowired
    private CacheProtectionService cacheProtectionService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String TEST_KEY = "test:cache:key";
    
    @BeforeEach
    public void setUp() {
        // 清理测试缓存
        redisTemplate.delete(TEST_KEY);
    }
    
    @Test
    public void testGetWithMutex() {
        // 模拟数据源，只会被调用一次
        AtomicInteger callCount = new AtomicInteger(0);
        
        // 第一次调用，应该从数据源获取
        String result1 = cacheProtectionService.getWithMutex(
            TEST_KEY,
            key -> {
                callCount.incrementAndGet();
                return "cached_value";
            },
            5, 
            TimeUnit.MINUTES
        );
        
        // 第二次调用，应该从缓存获取
        String result2 = cacheProtectionService.getWithMutex(
            TEST_KEY,
            key -> {
                callCount.incrementAndGet();
                return "different_value"; // 这个值不会被返回，因为应该命中缓存
            },
            5, 
            TimeUnit.MINUTES
        );
        
        assertEquals("cached_value", result1);
        assertEquals("cached_value", result2);
        assertEquals(1, callCount.get(), "数据源应该只被调用一次");
    }
    
    @Test
    public void testCacheMutexProtection() throws InterruptedException {
        String cacheKey = "test:mutex:key";
        redisTemplate.delete(cacheKey);
        
        // 并发线程数
        int threadCount = 10;
        
        // 数据源调用计数
        AtomicInteger dbCallCount = new AtomicInteger(0);
        
        // 线程池和同步器
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        // 模拟耗时的数据源操作
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // 等待统一开始
                    
                    String result = cacheProtectionService.getWithMutex(
                        cacheKey,
                        key -> {
                            // 模拟耗时的数据库操作
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            dbCallCount.incrementAndGet();
                            return "mutex_protected_value";
                        },
                        5,
                        TimeUnit.MINUTES
                    );
                    
                    assertEquals("mutex_protected_value", result);
                } catch (Exception e) {
                    fail("测试应该不抛出异常: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        // 同时释放所有线程
        startLatch.countDown();
        
        // 等待所有线程完成
        endLatch.await();
        executor.shutdown();
        
        // 验证数据源只被调用一次
        assertEquals(1, dbCallCount.get(), "使用互斥锁时，数据源应该只被调用一次");
    }
    
    @Test
    public void testEmptyValueProtection() {
        String cacheKey = "test:empty:key";
        redisTemplate.delete(cacheKey);
        
        // 模拟返回空值的数据源
        AtomicInteger callCount = new AtomicInteger(0);
        
        // 第一次调用
        String result1 = cacheProtectionService.getWithMutex(
            cacheKey,
            key -> {
                callCount.incrementAndGet();
                return null; // 返回空值
            },
            5,
            TimeUnit.MINUTES
        );
        
        // 第二次调用
        String result2 = cacheProtectionService.getWithMutex(
            cacheKey,
            key -> {
                callCount.incrementAndGet();
                return "this_should_not_be_returned";
            },
            5,
            TimeUnit.MINUTES
        );
        
        assertNull(result1);
        assertNull(result2);
        assertEquals(1, callCount.get(), "空值也应该被缓存，防止缓存穿透");
    }
} 