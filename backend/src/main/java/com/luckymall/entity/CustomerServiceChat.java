package com.luckymall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerServiceChat {
    private Long id;
    private Long userId;
    private String sessionId;
    private String userMessage;
    private String botResponse;
    private String intentType;
    private Boolean cacheHit = false;
    private Integer responseTimeMs;
    private LocalDateTime createdTime;
} 