package com.luckymall.mapper;

import com.luckymall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 插入订单
     */
    int insertOrder(Order order);
    
    /**
     * 根据ID查询订单
     */
    Order selectOrderById(@Param("id") Long id);
    
    /**
     * 根据订单号查询订单
     */
    Order selectOrderByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> selectOrdersByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和状态查询订单列表
     */
    List<Order> selectOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("orderStatus") String orderStatus);
    
    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") String orderStatus);
    
    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") String paymentStatus);
    
    /**
     * 更新订单
     */
    int updateOrder(Order order);
    
    /**
     * 根据用户ID统计订单数量
     */
    int countOrdersByUserId(@Param("userId") Long userId);
} 