package com.luckymall.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新购物车请求DTO
 */
@Data
public class UpdateCartRequest {
    
    /**
     * 购物车项ID
     */
    @NotNull(message = "购物车项ID不能为空")
    private Long cartItemId;
    
    /**
     * 商品数量
     */
    @Min(value = 1, message = "商品数量不能小于1")
    private Integer quantity;
} 