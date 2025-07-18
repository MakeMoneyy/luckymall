package com.luckymall.entity;

import lombok.Data;

@Data
public class FaqKnowledge {
    private Long id;
    private String question;
    private String answer;
    private String category;
    private String creditCardPromotion;
    private Integer hitCount = 0;
    private Integer status = 1;
} 