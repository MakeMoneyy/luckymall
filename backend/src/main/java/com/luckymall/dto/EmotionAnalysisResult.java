package com.luckymall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 情绪分析结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionAnalysisResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 情绪类型：POSITIVE, NEUTRAL, NEGATIVE
     */
    private String emotionType;
    
    /**
     * 情绪强度：1-5
     * 1: 非常负面
     * 2: 负面
     * 3: 中性
     * 4: 正面
     * 5: 非常正面
     */
    private Integer emotionIntensity;
    
    /**
     * 关键情绪词
     */
    @Builder.Default
    private List<String> emotionKeywords = new ArrayList<>();
    
    /**
     * 是否建议转人工
     */
    @Builder.Default
    private Boolean suggestHumanService = false;
    
    /**
     * 分析时间戳
     */
    private long timestamp;
} 