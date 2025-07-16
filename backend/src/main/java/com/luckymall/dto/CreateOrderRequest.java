package com.luckymall.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 选中的购物车商品ID列表
     */
    @NotEmpty(message = "购买商品不能为空")
    private List<Long> cartItemIds;
    
    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;
    
    /**
     * 支付方式
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;
    
    /**
     * 是否分期付款
     */
    private Boolean isInstallment = false;
    
    /**
     * 分期方案ID（分期时必填）
     */
    private Long installmentPlanId;
    
    /**
     * 订单备注
     */
    private String orderRemark;
    
    /**
     * 预期总金额（用于验证）
     */
    @NotNull(message = "订单金额不能为空")
    @Min(value = 0, message = "订单金额不能小于0")
    private BigDecimal expectedAmount;
} 