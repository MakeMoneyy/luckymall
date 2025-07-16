package com.luckymall.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
public class Order {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单状态
     */
    private String orderStatus;
    
    /**
     * 支付状态
     */
    private String paymentStatus;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 是否分期付款
     */
    private Integer isInstallment;
    
    /**
     * 分期方案ID
     */
    private Long installmentPlanId;
    
    /**
     * 分期期数
     */
    private Integer installmentCount;
    
    /**
     * 每期金额
     */
    private BigDecimal monthlyAmount;
    
    /**
     * 订单备注
     */
    private String orderRemark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 支付时间
     */
    private LocalDateTime paidAt;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 订单商品列表（关联查询）
     */
    private List<OrderItem> orderItems;
    
    /**
     * 分期方案（关联查询）
     */
    private InstallmentPlan installmentPlan;
} 