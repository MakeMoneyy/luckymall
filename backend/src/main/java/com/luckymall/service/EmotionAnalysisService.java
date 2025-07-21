package com.luckymall.service;

import com.luckymall.dto.ChatContext;
import com.luckymall.dto.EmotionAnalysisResult;

/**
 * 情感分析服务接口
 */
public interface EmotionAnalysisService {
    
    /**
     * 分析用户情绪
     * @param message 用户消息
     * @param context 对话上下文
     * @return 情绪分析结果
     */
    EmotionAnalysisResult analyzeEmotion(String message, ChatContext context);
    
    /**
     * 判断是否需要转人工
     * @param result 情绪分析结果
     * @param context 对话上下文
     * @return 是否需要转人工
     */
    boolean shouldTransferToHuman(EmotionAnalysisResult result, ChatContext context);
    
    /**
     * 跟踪情绪变化趋势
     * @param context 对话上下文
     * @param currentResult 当前情绪分析结果
     * @return 情绪变化趋势
     */
    String trackEmotionTrend(ChatContext context, EmotionAnalysisResult currentResult);
} 