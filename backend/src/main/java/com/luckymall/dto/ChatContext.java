package com.luckymall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天上下文
 * 用于存储会话状态和推广频次信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 当前会话中推广次数
     */
    @Builder.Default
    private int promotionCount = 0;
    
    /**
     * 用户是否拒绝推广
     */
    @Builder.Default
    private boolean promotionRejected = false;
    
    /**
     * 最近一次推广时间戳
     */
    private long lastPromotionTimestamp;
    
    /**
     * 当前浏览的商品ID
     */
    private Long currentProductId;
    
    /**
     * 当前浏览的商品名称
     */
    private String currentProductName;
    
    /**
     * 最近浏览的商品列表
     */
    @Builder.Default
    private List<String> viewedProducts = new ArrayList<>();
    
    /**
     * 是否已经询问过支付方式
     */
    @Builder.Default
    private boolean askedAboutPayment = false;
    
    /**
     * 是否已经询问过积分
     */
    @Builder.Default
    private boolean askedAboutPoints = false;
    
    /**
     * 最近一次交互的时间戳
     */
    private long lastInteractionTimestamp;
    
    /**
     * 当前对话主题
     */
    @Builder.Default
    private String currentTopic = "";
    
    /**
     * 已识别的实体信息
     */
    @Builder.Default
    private Map<String, Object> recognizedEntities = new HashMap<>();
    
    /**
     * 上一次查询的对象（如订单ID、商品ID等）
     */
    private String lastQueryObject;
    
    /**
     * 情绪分析历史
     */
    @Builder.Default
    private List<EmotionAnalysisResult> emotionHistory = new ArrayList<>();
    
    /**
     * 情绪变化趋势
     * STABLE: 稳定
     * IMPROVING: 改善
     * DETERIORATING: 恶化
     */
    @Builder.Default
    private String emotionTrend = "STABLE";
    
    /**
     * 最近一次情绪分析结果
     */
    private EmotionAnalysisResult lastEmotionResult;
} 