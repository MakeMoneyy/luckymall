package com.luckymall.service;

import com.luckymall.dto.ChatContext;
import com.luckymall.dto.EmotionAnalysisResult;
import com.luckymall.entity.HumanServiceSession;
import com.luckymall.mapper.HumanServiceSessionMapper;

/**
 * 人工客服转接服务接口
 */
public interface HumanServiceTransferService {
    
    /**
     * 创建人工客服会话
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param aiSessionId AI会话ID
     * @param reason 转接原因
     * @param emotionResult 情绪分析结果
     * @return 人工客服会话
     */
    HumanServiceSession createHumanServiceSession(Long userId, String sessionId, 
                                                 String aiSessionId, String reason, 
                                                 EmotionAnalysisResult emotionResult);
    
    /**
     * 获取人工客服会话状态
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 人工客服会话
     */
    HumanServiceSession getHumanServiceSession(Long userId, String sessionId);
    
    /**
     * 更新人工客服会话状态
     * @param sessionId 会话ID
     * @param status 状态
     * @return 更新后的会话
     */
    HumanServiceSession updateSessionStatus(String sessionId, String status);
    
    /**
     * 分配人工客服
     * @param sessionId 会话ID
     * @return 分配结果
     */
    boolean assignStaff(String sessionId);
    
    /**
     * 传递历史对话
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param aiSessionId AI会话ID
     * @return 传递结果
     */
    boolean transferChatHistory(Long userId, String sessionId, String aiSessionId);
    
    /**
     * 获取人工客服会话Mapper
     * @return 人工客服会话Mapper
     */
    HumanServiceSessionMapper getHumanServiceSessionMapper();
} 