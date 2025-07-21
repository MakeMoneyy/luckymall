package com.luckymall.controller;

import com.luckymall.common.Result;
import com.luckymall.dto.PaymentRequest;
import com.luckymall.dto.QRCodePaymentResponse;
import com.luckymall.entity.Payment;
import com.luckymall.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 处理订单支付
     */
    @PostMapping("/{userId}/pay")
    public Result<Payment> payOrder(@PathVariable Long userId, @Valid @RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(userId, request);
            return Result.success(payment);
        } catch (Exception e) {
            log.error("支付失败", e);
            return Result.error("支付失败: " + e.getMessage());
        }
    }

    /**
     * 获取支付宝支付二维码
     */
    @PostMapping("/{userId}/alipay/qrcode")
    public Result<QRCodePaymentResponse> getAlipayQRCode(@PathVariable Long userId, @Valid @RequestBody PaymentRequest request) {
        try {
            QRCodePaymentResponse response = paymentService.generateAlipayQRCode(userId, request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取支付宝支付二维码失败", e);
            return Result.error("获取支付宝支付二维码失败: " + e.getMessage());
        }
    }

    /**
     * 获取微信支付二维码
     */
    @PostMapping("/{userId}/wechat/qrcode")
    public Result<QRCodePaymentResponse> getWechatPayQRCode(@PathVariable Long userId, @Valid @RequestBody PaymentRequest request) {
        try {
            QRCodePaymentResponse response = paymentService.generateWechatPayQRCode(userId, request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取微信支付二维码失败", e);
            return Result.error("获取微信支付二维码失败: " + e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{paymentNo}")
    public Result<String> queryPaymentStatus(@PathVariable String paymentNo) {
        try {
            String status = paymentService.queryPaymentStatus(paymentNo);
            return Result.success(status);
        } catch (Exception e) {
            log.error("查询支付状态失败", e);
            return Result.error("查询支付状态失败: " + e.getMessage());
        }
    }
} 