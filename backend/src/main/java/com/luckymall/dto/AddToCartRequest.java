package com.luckymall.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 添加到购物车请求DTO
 */
@Data
public class AddToCartRequest {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 商品数量
     */
    @Min(value = 1, message = "商品数量不能小于1")
    private Integer quantity = 1;
} 