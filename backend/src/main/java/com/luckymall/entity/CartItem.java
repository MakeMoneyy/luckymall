package com.luckymall.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购物车商品实体类
 */
@Data
public class CartItem {
    
    /**
     * 购物车项ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品数量
     */
    private Integer quantity;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 商品信息（查询时关联）
     */
    private Product product;
} 