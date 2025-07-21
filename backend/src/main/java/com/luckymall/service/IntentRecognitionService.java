package com.luckymall.service;

import com.luckymall.dto.ChatContext;
import com.luckymall.dto.IntentRecognitionResult;

/**
 * 意图识别服务接口
 */
public interface IntentRecognitionService {
    
    /**
     * 识别用户意图
     * @param message 用户消息
     * @param context 对话上下文
     * @return 意图识别结果
     */
    IntentRecognitionResult recognizeIntent(String message, ChatContext context);
    
    /**
     * 从用户消息中提取实体信息
     * @param message 用户消息
     * @param context 对话上下文
     * @return 提取的实体信息
     */
    IntentRecognitionResult extractEntities(String message, ChatContext context);
} 