package com.luckymall.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ChatRequest {
    private Long userId;
    private String sessionId;
    private String message;
    private ChatContext context;

    @Data
    public static class ChatContext {
        private Long productId;
        private BigDecimal productPrice;
        private String currentPage;
    }
} 