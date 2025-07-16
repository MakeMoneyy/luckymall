package com.luckymall.service;

import com.luckymall.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取用户购物车商品列表
     */
    List<CartItem> getCartItems(Long userId);
    
    /**
     * 添加商品到购物车
     */
    void addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     */
    void updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity);
    
    /**
     * 从购物车删除商品
     */
    void removeFromCart(Long userId, Long cartItemId);
    
    /**
     * 根据商品ID从购物车删除商品
     */
    void removeFromCartByProductId(Long userId, Long productId);
    
    /**
     * 清空用户购物车
     */
    void clearCart(Long userId);
    
    /**
     * 获取用户购物车商品数量
     */
    int getCartItemCount(Long userId);
} 