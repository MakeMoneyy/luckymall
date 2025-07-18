  package com.luckymall.service;

import com.luckymall.dto.ChatContext;

/**
 * 聊天会话服务接口
 * 用于管理聊天会话上下文，包括推广频次跟踪
 */
public interface ChatSessionService {
    
    /**
     * 获取会话上下文
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 会话上下文
     */
    ChatContext getSessionContext(String userId, String sessionId);
    
    /**
     * 增加推广计数
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 当前推广次数
     */
    int incrementPromotionCount(String userId, String sessionId);
    
    /**
     * 设置用户拒绝推广标志
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param rejected 是否拒绝
     */
    void setPromotionRejected(String userId, String sessionId, boolean rejected);
    
    /**
     * 保存会话上下文
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param context 会话上下文
     */
    void saveSessionContext(String userId, String sessionId, ChatContext context);
} 