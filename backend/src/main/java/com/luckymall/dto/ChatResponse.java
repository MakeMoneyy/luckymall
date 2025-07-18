package com.luckymall.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ChatResponse {
    private String responseId;
    private String message;
    private List<String> suggestions;
    private PromotionInfo promotionInfo;
    private Boolean cacheHit = false;
    private Integer responseTimeMs;

    @Data
    public static class PromotionInfo {
        private String type;
        private Integer pointsEarned;
        private BigDecimal discountAmount;
        private List<Integer> installmentOptions;
    }
} 