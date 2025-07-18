package com.luckymall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
} 