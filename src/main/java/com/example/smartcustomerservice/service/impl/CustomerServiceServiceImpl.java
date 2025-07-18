package com.example.smartcustomerservice.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.smartcustomerservice.model.ChatRequest;
import com.example.smartcustomerservice.model.ChatResponse;
import com.example.smartcustomerservice.service.CustomerServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 智能客服服务实现类
 */
@Slf4j
@Service
public class CustomerServiceServiceImpl implements CustomerServiceService {
    
    @Value("${dashscope.api-key}")
    private String apiKey;
    
    @Value("${dashscope.model}")
    private String model;
    
    @Value("${customer-service.system-prompt}")
    private String systemPrompt;
    
    @Value("${customer-service.max-context-history}")
    private int maxContextHistory;
    
    @Value("${cache.redis.ai-response-expiration}")
    private long aiResponseExpiration;
    
    @Value("${cache.redis.context-expiration}")
    private long contextExpiration;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CHAT_CONTEXT_PREFIX = "chat:context:";
    private static final String CHAT_CACHE_PREFIX = "chat:cache:";
    
    @Override
    public ChatResponse processChat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 处理会话ID
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = createSession(request.getUserId());
            }
            
            // 2. 获取或创建聊天上下文
            List<Map<String, String>> chatHistory = getChatHistory(sessionId);
            
            // 3. 检查缓存
            String cacheKey = buildCacheKey(request.getMessage(), chatHistory);
            ChatResponse cachedResponse = (ChatResponse) redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedResponse != null) {
                cachedResponse.setCacheHit(true);
                cachedResponse.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
                return cachedResponse;
            }
            
            // 4. 调用通义千问API
            String aiResponse = callTongyiQianwenAPI(request.getMessage(), chatHistory);
            
            // 5. 更新聊天历史
            updateChatHistory(sessionId, request.getMessage(), aiResponse);
            
            // 6. 构建响应
            ChatResponse response = buildChatResponse(aiResponse, sessionId, startTime);
            
            // 7. 缓存响应
            redisTemplate.opsForValue().set(cacheKey, response, aiResponseExpiration, TimeUnit.SECONDS);
            
            return response;
        } catch (Exception e) {
            log.error("处理聊天请求异常", e);
            return buildErrorResponse(e.getMessage(), startTime);
        }
    }
    
    @Override
    public String createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        
        // 初始化空的聊天历史
        redisTemplate.opsForValue().set(
                CHAT_CONTEXT_PREFIX + sessionId,
                new ArrayList<>(),
                contextExpiration,
                TimeUnit.SECONDS
        );
        
        log.info("为用户 {} 创建新会话: {}", userId, sessionId);
        return sessionId;
    }
    
    @Override
    public ChatResponse getSessionHistory(String sessionId) {
        List<Map<String, String>> chatHistory = getChatHistory(sessionId);
        
        // 构建历史消息响应
        StringBuilder historyContent = new StringBuilder();
        for (Map<String, String> message : chatHistory) {
            String role = message.get("role");
            String content = message.get("content");
            historyContent.append(role).append(": ").append(content).append("\n\n");
        }
        
        return ChatResponse.builder()
                .responseId(UUID.randomUUID().toString())
                .message(historyContent.toString())
                .build();
    }
    
    /**
     * 获取聊天历史
     */
    private List<Map<String, String>> getChatHistory(String sessionId) {
        List<Map<String, String>> chatHistory = (List<Map<String, String>>) redisTemplate.opsForValue()
                .get(CHAT_CONTEXT_PREFIX + sessionId);
        
        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
        }
        
        return chatHistory;
    }
    
    /**
     * 更新聊天历史
     */
    private void updateChatHistory(String sessionId, String userMessage, String aiResponse) {
        List<Map<String, String>> chatHistory = getChatHistory(sessionId);
        
        // 添加用户消息
        Map<String, String> userMessageMap = new HashMap<>();
        userMessageMap.put("role", "user");
        userMessageMap.put("content", userMessage);
        chatHistory.add(userMessageMap);
        
        // 添加AI响应
        Map<String, String> aiResponseMap = new HashMap<>();
        aiResponseMap.put("role", "assistant");
        aiResponseMap.put("content", aiResponse);
        chatHistory.add(aiResponseMap);
        
        // 如果历史记录超过最大限制，移除最旧的消息
        while (chatHistory.size() > maxContextHistory * 2) { // *2是因为每轮对话有两条消息
            chatHistory.remove(0);
        }
        
        // 更新Redis中的聊天历史
        redisTemplate.opsForValue().set(
                CHAT_CONTEXT_PREFIX + sessionId,
                chatHistory,
                contextExpiration,
                TimeUnit.SECONDS
        );
    }
    
    /**
     * 调用通义千问API
     */
    private String callTongyiQianwenAPI(String userMessage, List<Map<String, String>> chatHistory) 
            throws ApiException, NoApiKeyException, InputRequiredException {
        // 创建Generation实例
        Generation generation = new Generation();
        
        // 创建系统消息
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
                .build();
        
        // 构建历史消息列表
        List<Message> messages = new ArrayList<>();
        messages.add(systemMsg);
        
        // 添加历史消息
        for (Map<String, String> historyMessage : chatHistory) {
            String role = historyMessage.get("role");
            String content = historyMessage.get("content");
            
            Role messageRole = "user".equals(role) ? Role.USER : Role.ASSISTANT;
            
            messages.add(Message.builder()
                    .role(messageRole.getValue())
                    .content(content)
                    .build());
        }
        
        // 添加当前用户消息
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(userMessage)
                .build();
        messages.add(userMsg);
        
        // 构建请求参数
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model(model)
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        
        // 调用API
        GenerationResult result = generation.call(param);
        
        // 返回结果内容
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }
    
    /**
     * 构建缓存键
     */
    private String buildCacheKey(String message, List<Map<String, String>> chatHistory) {
        StringBuilder keyBuilder = new StringBuilder(CHAT_CACHE_PREFIX);
        keyBuilder.append(message.hashCode());
        
        // 只使用最近的几条消息作为缓存键的一部分
        int historySize = chatHistory.size();
        int startIndex = Math.max(0, historySize - 4); // 最多使用最近2轮对话
        
        for (int i = startIndex; i < historySize; i++) {
            Map<String, String> historyMessage = chatHistory.get(i);
            keyBuilder.append("_").append(historyMessage.get("content").hashCode());
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 构建聊天响应
     */
    private ChatResponse buildChatResponse(String aiResponse, String sessionId, long startTime) {
        return ChatResponse.builder()
                .responseId(UUID.randomUUID().toString())
                .message(aiResponse)
                .cacheHit(false)
                .responseTimeMs((int) (System.currentTimeMillis() - startTime))
                .build();
    }
    
    /**
     * 构建错误响应
     */
    private ChatResponse buildErrorResponse(String errorMessage, long startTime) {
        return ChatResponse.builder()
                .responseId(UUID.randomUUID().toString())
                .message("抱歉，服务出现了一些问题，请稍后再试。")
                .cacheHit(false)
                .responseTimeMs((int) (System.currentTimeMillis() - startTime))
                .build();
    }
} 