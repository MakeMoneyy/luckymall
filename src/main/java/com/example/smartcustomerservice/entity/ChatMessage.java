package com.example.smartcustomerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：user-用户消息，assistant-客服回复
     */
    private String role;
    
    /**
     * 消息创建时间
     */
    private Date createTime;
    
    /**
     * 是否命中缓存
     */
    private Boolean cacheHit;
    
    /**
     * 响应时间(毫秒)
     */
    private Integer responseTimeMs;
} 