package com.luckymall.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付实体类
 */
@Data
public class Payment {
    private Long id;
    private String paymentNo;
    private Long orderId;
    private Long userId;
    private String paymentMethod;
    private Double amount;
    private String status;
    private LocalDateTime paymentTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 