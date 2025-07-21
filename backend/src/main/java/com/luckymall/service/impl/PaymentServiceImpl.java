package com.luckymall.service.impl;

import com.luckymall.dto.PaymentRequest;
import com.luckymall.dto.QRCodePaymentResponse;
import com.luckymall.entity.Payment;
import com.luckymall.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Override
    public Payment processPayment(Long userId, PaymentRequest request) {
        log.info("处理支付请求: userId={}, orderId={}, method={}", userId, request.getOrderId(), request.getPaymentMethod());
        
        // 模拟支付处理
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(request.getOrderId());
        payment.setUserId(userId);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setStatus("SUCCESS");
        payment.setPaymentTime(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        return payment;
    }

    @Override
    public QRCodePaymentResponse generateAlipayQRCode(Long userId, PaymentRequest request) {
        log.info("生成支付宝支付二维码: userId={}, orderId={}", userId, request.getOrderId());
        
        // 模拟生成支付宝支付二维码
        QRCodePaymentResponse response = new QRCodePaymentResponse();
        response.setQrCodeUrl("https://via.placeholder.com/300x300?text=支付宝支付");
        response.setPaymentNo(generatePaymentNo());
        response.setExpireTime(LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setAmount(request.getAmount());
        
        return response;
    }

    @Override
    public QRCodePaymentResponse generateWechatPayQRCode(Long userId, PaymentRequest request) {
        log.info("生成微信支付二维码: userId={}, orderId={}", userId, request.getOrderId());
        
        // 模拟生成微信支付二维码
        QRCodePaymentResponse response = new QRCodePaymentResponse();
        response.setQrCodeUrl("https://via.placeholder.com/300x300?text=微信支付");
        response.setPaymentNo(generatePaymentNo());
        response.setExpireTime(LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setAmount(request.getAmount());
        
        return response;
    }

    @Override
    public String queryPaymentStatus(String paymentNo) {
        log.info("查询支付状态: paymentNo={}", paymentNo);
        
        // 模拟查询支付状态
        return "SUCCESS";
    }
    
    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }
} 