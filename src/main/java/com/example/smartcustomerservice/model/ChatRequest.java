package com.example.smartcustomerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 聊天请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 会话ID，首次对话可为空
     */
    private String sessionId;
    
    /**
     * 用户发送的消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
    
    /**
     * 上下文信息，可包含商品ID、价格、当前页面等
     */
    private Map<String, Object> context;
} 