package com.luckymall.dto;

import com.luckymall.enums.IntentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 意图识别结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentRecognitionResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 意图类型
     */
    private IntentType intentType;
    
    /**
     * 置信度
     */
    private Float confidence;
    
    /**
     * 提取的实体信息
     */
    @Builder.Default
    private Map<String, Object> extractedEntities = new HashMap<>();
    
    /**
     * 原始查询
     */
    private String originalQuery;
    
    /**
     * 分析时间戳
     */
    private long timestamp;
} 