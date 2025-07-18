package com.luckymall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 聊天响应数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String result;
    private String responseId;
    private String message;
    private String sessionId;
    private Long responseTime;
    private Integer responseTimeMs;
    private Boolean cacheHit;
    private String error;
    private List<String> suggestions;
    private PromotionInfo promotionInfo;

    @Data
    public static class PromotionInfo {
        private String type;
        private Integer pointsEarned;
        private BigDecimal discountAmount;
        private List<Integer> installmentOptions;
    }
} 