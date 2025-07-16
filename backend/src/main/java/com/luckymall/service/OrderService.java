package com.luckymall.service;

import com.luckymall.dto.CreateOrderRequest;
import com.luckymall.dto.CreateOrderResponse;
import com.luckymall.entity.Order;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    CreateOrderResponse createOrder(Long userId, CreateOrderRequest request);
    
    /**
     * 根据ID查询订单
     */
    Order getOrderById(Long orderId);
    
    /**
     * 根据订单号查询订单
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查询订单列表
     */
    List<Order> getOrdersByUserIdAndStatus(Long userId, String orderStatus);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, String orderStatus);
    
    /**
     * 更新支付状态
     */
    boolean updatePaymentStatus(Long orderId, String paymentStatus);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 确认收货
     */
    boolean confirmReceive(Long orderId, Long userId);
    
    /**
     * 生成订单号
     */
    String generateOrderNo();
} 