package com.luckymall.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建订单响应DTO
 */
@Data
public class CreateOrderResponse {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 是否分期付款
     */
    private Boolean isInstallment;
    
    /**
     * 分期期数
     */
    private Integer installmentCount;
    
    /**
     * 每期金额
     */
    private BigDecimal monthlyAmount;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
} 