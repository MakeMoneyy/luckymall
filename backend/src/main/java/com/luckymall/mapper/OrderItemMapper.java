package com.luckymall.mapper;

import com.luckymall.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 订单商品Mapper接口
 */
@Mapper
public interface OrderItemMapper {
    
    /**
     * 批量插入订单商品
     */
    int insertOrderItems(@Param("orderItems") List<OrderItem> orderItems);
    
    /**
     * 插入订单商品
     */
    int insertOrderItem(OrderItem orderItem);
    
    /**
     * 根据订单ID查询订单商品列表
     */
    List<OrderItem> selectOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据ID查询订单商品
     */
    OrderItem selectOrderItemById(@Param("id") Long id);
    
    /**
     * 更新订单商品
     */
    int updateOrderItem(OrderItem orderItem);
    
    /**
     * 删除订单商品
     */
    int deleteOrderItem(@Param("id") Long id);
} 