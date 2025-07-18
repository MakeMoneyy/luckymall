package com.luckymall.dto;

import lombok.Data;

import java.util.Map;

/**
 * 聊天请求数据传输对象
 */
@Data
public class ChatRequest {
    private String userId;
    private String sessionId;
    private String message;
    private Map<String, Object> context;
} 