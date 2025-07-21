package com.luckymall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckymall.dto.ChatContext;
import com.luckymall.dto.EmotionAnalysisResult;
import com.luckymall.entity.CustomerServiceChat;
import com.luckymall.entity.HumanServiceSession;
import com.luckymall.mapper.CustomerServiceChatMapper;
import com.luckymall.mapper.HumanServiceSessionMapper;
import com.luckymall.service.HumanServiceTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人工客服转接服务实现类
 */
@Service
@Slf4j
public class HumanServiceTransferServiceImpl implements HumanServiceTransferService {

    @Autowired
    private HumanServiceSessionMapper humanServiceSessionMapper;
    
    @Autowired
    private CustomerServiceChatMapper customerServiceChatMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public HumanServiceSession createHumanServiceSession(Long userId, String sessionId, 
                                                       String aiSessionId, String reason, 
                                                       EmotionAnalysisResult emotionResult) {
        // 检查是否已存在会话
        HumanServiceSession existingSession = humanServiceSessionMapper.findByUserIdAndSessionId(userId, sessionId);
        if (existingSession != null) {
            log.info("人工客服会话已存在: userId={}, sessionId={}", userId, sessionId);
            return existingSession;
        }
        
        // 创建新的会话
        HumanServiceSession session = new HumanServiceSession();
        session.setUserId(userId);
        session.setSessionId(sessionId);
        session.setAiSessionId(aiSessionId);
        session.setStatus("WAITING");
        session.setTransferReason(reason);
        
        // 将情绪分析结果转为JSON
        try {
            String emotionData = objectMapper.writeValueAsString(emotionResult);
            session.setEmotionData(emotionData);
        } catch (JsonProcessingException e) {
            log.error("情绪数据序列化失败: {}", e.getMessage(), e);
        }
        
        session.setCreatedTime(LocalDateTime.now());
        session.setUpdatedTime(LocalDateTime.now());
        
        // 保存会话
        humanServiceSessionMapper.insert(session);
        log.info("创建人工客服会话: userId={}, sessionId={}", userId, sessionId);
        
        return session;
    }

    @Override
    public HumanServiceSession getHumanServiceSession(Long userId, String sessionId) {
        return humanServiceSessionMapper.findByUserIdAndSessionId(userId, sessionId);
    }

    @Override
    public HumanServiceSession updateSessionStatus(String sessionId, String status) {
        // 更新状态
        humanServiceSessionMapper.updateStatus(sessionId, status);
        
        // 获取更新后的会话
        HumanServiceSession session = humanServiceSessionMapper.findBySessionId(sessionId);
        log.info("更新人工客服会话状态: sessionId={}, status={}", sessionId, status);
        
        return session;
    }

    @Override
    public boolean assignStaff(String sessionId) {
        // 这里应该有分配算法，例如轮询、负载均衡等
        // 简单起见，这里随机分配一个客服ID
        String staffId = "staff00" + (Math.random() * 3 + 1);
        
        // 更新会话
        int rows = humanServiceSessionMapper.assignStaff(sessionId, staffId);
        
        boolean success = rows > 0;
        if (success) {
            log.info("分配客服成功: sessionId={}, staffId={}", sessionId, staffId);
        } else {
            log.warn("分配客服失败: sessionId={}", sessionId);
        }
        
        return success;
    }

    @Override
    public boolean transferChatHistory(Long userId, String sessionId, String aiSessionId) {
        // 获取历史对话记录
        List<CustomerServiceChat> chatHistory = customerServiceChatMapper.findByUserIdAndSessionId(userId, sessionId);
        
        if (chatHistory == null || chatHistory.isEmpty()) {
            log.warn("没有找到历史对话记录: userId={}, sessionId={}", userId, sessionId);
            return false;
        }
        
        // 在实际应用中，这里应该将历史对话记录传递给人工客服系统
        // 例如，可以通过消息队列、WebSocket等方式
        
        log.info("传递历史对话记录: userId={}, sessionId={}, 记录数={}", userId, sessionId, chatHistory.size());
        
        return true;
    }
    
    @Override
    public HumanServiceSessionMapper getHumanServiceSessionMapper() {
        return humanServiceSessionMapper;
    }
} 