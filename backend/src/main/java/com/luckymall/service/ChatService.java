package com.luckymall.service;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 处理聊天请求
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse processChat(ChatRequest request);
    
    /**
     * 获取流式聊天响应
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse processStreamChat(ChatRequest request);
} 