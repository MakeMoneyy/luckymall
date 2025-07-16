package com.luckymall.controller;

import com.luckymall.common.Result;
import com.luckymall.dto.AddToCartRequest;
import com.luckymall.dto.UpdateCartRequest;
import com.luckymall.entity.CartItem;
import com.luckymall.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取用户购物车商品列表
     */
    @GetMapping("/{userId}")
    public Result<List<CartItem>> getCartItems(@PathVariable Long userId) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            return Result.success(cartItems);
        } catch (Exception e) {
            log.error("获取购物车失败", e);
            return Result.error("获取购物车失败: " + e.getMessage());
        }
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/{userId}/add")
    public Result<String> addToCart(@PathVariable Long userId, 
                                  @Valid @RequestBody AddToCartRequest request) {
        try {
            cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            return Result.success("商品已添加到购物车");
        } catch (Exception e) {
            log.error("添加商品到购物车失败", e);
            return Result.error("添加商品到购物车失败: " + e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{userId}/update")
    public Result<String> updateCartItemQuantity(@PathVariable Long userId,
                                               @Valid @RequestBody UpdateCartRequest request) {
        try {
            cartService.updateCartItemQuantity(userId, request.getCartItemId(), request.getQuantity());
            return Result.success("购物车商品数量已更新");
        } catch (Exception e) {
            log.error("更新购物车商品数量失败", e);
            return Result.error("更新购物车商品数量失败: " + e.getMessage());
        }
    }

    /**
     * 从购物车删除商品
     */
    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public Result<String> removeFromCart(@PathVariable Long userId, 
                                       @PathVariable Long cartItemId) {
        try {
            cartService.removeFromCart(userId, cartItemId);
            return Result.success("商品已从购物车删除");
        } catch (Exception e) {
            log.error("从购物车删除商品失败", e);
            return Result.error("从购物车删除商品失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品ID从购物车删除商品
     */
    @DeleteMapping("/{userId}/remove-product/{productId}")
    public Result<String> removeFromCartByProductId(@PathVariable Long userId, 
                                                  @PathVariable Long productId) {
        try {
            cartService.removeFromCartByProductId(userId, productId);
            return Result.success("商品已从购物车删除");
        } catch (Exception e) {
            log.error("从购物车删除商品失败", e);
            return Result.error("从购物车删除商品失败: " + e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/{userId}/clear")
    public Result<String> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return Result.success("购物车已清空");
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }

    /**
     * 获取购物车商品数量
     */
    @GetMapping("/{userId}/count")
    public Result<Integer> getCartItemCount(@PathVariable Long userId) {
        try {
            int count = cartService.getCartItemCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取购物车商品数量失败", e);
            return Result.error("获取购物车商品数量失败: " + e.getMessage());
        }
    }
} 