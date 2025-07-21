package com.luckymall.service;

import com.luckymall.dto.PaymentRequest;
import com.luckymall.dto.QRCodePaymentResponse;
import com.luckymall.entity.Payment;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 处理支付
     */
    Payment processPayment(Long userId, PaymentRequest request);
    
    /**
     * 生成支付宝支付二维码
     */
    QRCodePaymentResponse generateAlipayQRCode(Long userId, PaymentRequest request);
    
    /**
     * 生成微信支付二维码
     */
    QRCodePaymentResponse generateWechatPayQRCode(Long userId, PaymentRequest request);
    
    /**
     * 查询支付状态
     */
    String queryPaymentStatus(String paymentNo);
} 