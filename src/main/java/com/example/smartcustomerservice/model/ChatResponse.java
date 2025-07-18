package com.example.smartcustomerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 聊天响应模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * 响应ID
     */
    private String responseId;
    
    /**
     * 回复消息内容
     */
    private String message;
    
    /**
     * 是否命中缓存
     */
    private Boolean cacheHit;
    
    /**
     * 响应时间(毫秒)
     */
    private Integer responseTimeMs;
    
    /**
     * 建议回复列表
     */
    private List<String> suggestions;
    
    /**
     * 推广信息
     */
    private Map<String, Object> promotionInfo;
} 