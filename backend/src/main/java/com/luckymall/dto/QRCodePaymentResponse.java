package com.luckymall.dto;

import lombok.Data;

/**
 * 二维码支付响应DTO
 */
@Data
public class QRCodePaymentResponse {
    private String qrCodeUrl;
    private String paymentNo;
    private String expireTime;
    private Double amount;
} 