package com.luckymall.service;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 聊天服务测试类
 */
@SpringBootTest
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @BeforeEach
    public void setUp() {
        // 清除与测试相关的缓存
        redisTemplate.keys("chat:response:*").forEach(key -> {
            redisTemplate.delete(key);
        });
    }

    @Test
    public void testProcessChat() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId("1");
        request.setSessionId(UUID.randomUUID().toString());
        request.setMessage("你好，我想了解一下信用卡支付有什么优惠？");
        request.setContext(new HashMap<>());

        // 第一次调用服务
        ChatResponse response = chatService.processChat(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertFalse(response.getResult().isEmpty());
        assertNotNull(response.getSessionId());  // 只检查sessionId非空，不比较具体值
        assertNotNull(response.getResponseTime());
        assertFalse(response.getCacheHit());
        
        // 第二次调用，应该命中缓存
        ChatResponse cachedResponse = chatService.processChat(request);
        
        assertNotNull(cachedResponse);
        assertTrue(cachedResponse.getCacheHit());
        // 缓存命中时响应时间应该更短
        assertTrue(cachedResponse.getResponseTime() <= response.getResponseTime());
    }
} 