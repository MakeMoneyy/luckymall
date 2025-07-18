package com.example.smartcustomerservice.service;

import com.example.smartcustomerservice.model.ChatRequest;
import com.example.smartcustomerservice.model.ChatResponse;

/**
 * 智能客服服务接口
 */
public interface CustomerServiceService {
    
    /**
     * 处理聊天请求
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse processChat(ChatRequest request);
    
    /**
     * 创建新的会话
     * @param userId 用户ID
     * @return 会话ID
     */
    String createSession(Long userId);
    
    /**
     * 获取会话历史
     * @param sessionId 会话ID
     * @return 会话历史响应
     */
    ChatResponse getSessionHistory(String sessionId);
} 