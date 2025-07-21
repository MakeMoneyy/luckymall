package com.luckymall.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 人工客服会话实体
 */
@Data
public class HumanServiceSession {
    /**
     * 会话ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * AI会话ID
     */
    private String aiSessionId;
    
    /**
     * 状态：WAITING(等待), IN_PROGRESS(进行中), COMPLETED(已完成), CANCELLED(已取消)
     */
    private String status;
    
    /**
     * 客服人员ID
     */
    private String staffId;
    
    /**
     * 转接原因
     */
    private String transferReason;
    
    /**
     * 情绪数据（JSON格式）
     */
    private String emotionData;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 