package com.luckymall.service;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.dto.UserCreditCardResponse;

public interface CustomerServiceService {
    
    /**
     * 处理客服聊天请求
     */
    ChatResponse processChat(ChatRequest request);
    
    /**
     * 获取用户信用卡信息
     */
    UserCreditCardResponse getUserCreditCard(Long userId);
} 