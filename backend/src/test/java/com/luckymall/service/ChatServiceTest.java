package com.luckymall.service;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    public void testProcessChat() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId("test-user");
        request.setSessionId(UUID.randomUUID().toString());
        request.setMessage("你好，我想了解一下信用卡支付有什么优惠？");
        request.setContext(new HashMap<>());

        // 调用服务
        ChatResponse response = chatService.processChat(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertFalse(response.getResult().isEmpty());
        assertEquals(request.getSessionId(), response.getSessionId());
        assertNotNull(response.getResponseTime());
        assertFalse(response.getCacheHit());
        
        // 第二次调用，应该命中缓存
        ChatResponse cachedResponse = chatService.processChat(request);
        assertTrue(cachedResponse.getCacheHit());
    }
} 