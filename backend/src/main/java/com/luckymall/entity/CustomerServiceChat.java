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
    private String recognizedIntent;
    private String extractedEntities;
    private String emotionType;
    private Integer emotionIntensity;
    private Boolean transferredToHuman = false;
    private Boolean cacheHit = false;
    private Integer responseTimeMs;
    private LocalDateTime createdTime;
} 