package com.luckymall.mapper;

import com.luckymall.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper {
    
    /**
     * 根据用户ID获取购物车商品列表
     */
    List<CartItem> selectCartItemsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和商品ID查询购物车项
     */
    CartItem selectCartItemByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 添加商品到购物车
     */
    int insertCartItem(CartItem cartItem);
    
    /**
     * 更新购物车商品数量
     */
    int updateCartItemQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 删除购物车商品
     */
    int deleteCartItem(@Param("id") Long id);
    
    /**
     * 根据用户ID删除所有购物车商品
     */
    int deleteCartItemsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和商品ID删除购物车商品
     */
    int deleteCartItemByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 统计用户购物车商品数量
     */
    int countCartItemsByUserId(@Param("userId") Long userId);
} 