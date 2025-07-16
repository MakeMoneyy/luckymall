package com.luckymall.service.impl;

import com.luckymall.entity.CartItem;
import com.luckymall.entity.Product;
import com.luckymall.mapper.CartMapper;
import com.luckymall.mapper.ProductMapper;
import com.luckymall.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<CartItem> getCartItems(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return cartMapper.selectCartItemsByUserId(userId);
    }

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        
        // 验证商品是否存在且有效
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 检查购物车中是否已存在该商品
        CartItem existingItem = cartMapper.selectCartItemByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            // 如果已存在，检查库存是否足够
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("商品库存不足");
            }
        }
        
        // 添加到购物车
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        
        cartMapper.insertCartItem(cartItem);
        log.info("用户{}成功添加商品{}到购物车，数量:{}", userId, productId, quantity);
    }

    @Override
    public void updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (cartItemId == null) {
            throw new IllegalArgumentException("购物车项ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        
        // 验证购物车项是否属于该用户
        CartItem cartItem = cartMapper.selectCartItemsByUserId(userId).stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElse(null);
        
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }
        
        // 验证库存
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("商品库存不足");
        }
        
        cartMapper.updateCartItemQuantity(cartItemId, quantity);
        log.info("用户{}成功更新购物车项{}数量为{}", userId, cartItemId, quantity);
    }

    @Override
    public void removeFromCart(Long userId, Long cartItemId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (cartItemId == null) {
            throw new IllegalArgumentException("购物车项ID不能为空");
        }
        
        // 验证购物车项是否属于该用户
        CartItem cartItem = cartMapper.selectCartItemsByUserId(userId).stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElse(null);
        
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }
        
        cartMapper.deleteCartItem(cartItemId);
        log.info("用户{}成功删除购物车项{}", userId, cartItemId);
    }

    @Override
    public void removeFromCartByProductId(Long userId, Long productId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        cartMapper.deleteCartItemByUserIdAndProductId(userId, productId);
        log.info("用户{}成功删除商品{}从购物车", userId, productId);
    }

    @Override
    public void clearCart(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        cartMapper.deleteCartItemsByUserId(userId);
        log.info("用户{}成功清空购物车", userId);
    }

    @Override
    public int getCartItemCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return cartMapper.countCartItemsByUserId(userId);
    }
} 