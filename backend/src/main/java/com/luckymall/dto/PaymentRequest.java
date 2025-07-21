package com.luckymall.dto;

import lombok.Data;

/**
 * 支付请求DTO
 */
@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod;
    private Long creditCardId;
    private Long installmentPlanId;
    private Double amount;
} 