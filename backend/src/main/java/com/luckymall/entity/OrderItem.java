package com.luckymall.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品实体类
 */
@Data
public class OrderItem {
    
    /**
     * 订单商品ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称（快照）
     */
    private String productName;
    
    /**
     * 商品图片（快照）
     */
    private String productImage;
    
    /**
     * 商品价格（快照）
     */
    private BigDecimal productPrice;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 商品信息（关联查询）
     */
    private Product product;
} 