package com.luckymall.service.impl;

import com.luckymall.dto.CreateOrderRequest;
import com.luckymall.dto.CreateOrderResponse;
import com.luckymall.entity.*;
import com.luckymall.mapper.*;
import com.luckymall.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private InstallmentPlanMapper installmentPlanMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    // 订单号计数器（实际项目中可以使用Redis或数据库序列）
    private static final AtomicLong ORDER_COUNTER = new AtomicLong(1);

    @Override
    public CreateOrderResponse createOrder(Long userId, CreateOrderRequest request) {
        log.info("开始创建订单，用户ID：{}，请求：{}", userId, request);
        
        try {
            // 1. 验证参数
            validateCreateOrderRequest(userId, request);
            
            // 2. 获取选中的购物车商品
            List<CartItem> cartItems = getSelectedCartItems(request.getCartItemIds());
            if (cartItems.isEmpty()) {
                throw new RuntimeException("购物车商品不存在或已失效");
            }
            
            // 3. 计算订单金额
            BigDecimal totalAmount = calculateTotalAmount(cartItems);
            
            // 4. 验证金额是否匹配
            if (totalAmount.compareTo(request.getExpectedAmount()) != 0) {
                throw new RuntimeException("订单金额不匹配，请刷新后重试");
            }
            
            // 5. 获取收货地址
            UserAddress address = userAddressMapper.selectUserAddressById(request.getAddressId());
            if (address == null || !address.getUserId().equals(userId)) {
                throw new RuntimeException("收货地址不存在");
            }
            
            // 6. 处理分期付款
            InstallmentPlan installmentPlan = null;
            BigDecimal monthlyAmount = null;
            if (Boolean.TRUE.equals(request.getIsInstallment()) && request.getInstallmentPlanId() != null) {
                installmentPlan = installmentPlanMapper.selectInstallmentPlanById(request.getInstallmentPlanId());
                if (installmentPlan == null || installmentPlan.getStatus() != 1) {
                    throw new RuntimeException("分期方案不存在或已禁用");
                }
                
                // 验证分期金额范围
                if (totalAmount.compareTo(installmentPlan.getMinAmount()) < 0 || 
                    totalAmount.compareTo(installmentPlan.getMaxAmount()) > 0) {
                    throw new RuntimeException("订单金额不在分期方案范围内");
                }
                
                monthlyAmount = installmentPlan.calculateMonthlyAmount(totalAmount);
            }
            
            // 7. 创建订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setOrderStatus("PENDING_PAYMENT");
            order.setPaymentStatus("UNPAID");
            order.setPaymentMethod(request.getPaymentMethod());
            order.setTotalAmount(totalAmount);
            order.setActualAmount(totalAmount); // 暂时没有优惠
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setShippingFee(BigDecimal.ZERO); // 暂时免运费
            order.setReceiverName(address.getReceiverName());
            order.setReceiverPhone(address.getReceiverPhone());
            order.setReceiverAddress(address.getFullAddress());
            order.setOrderRemark(request.getOrderRemark());
            
            // 分期信息
            if (installmentPlan != null) {
                order.setIsInstallment(1);
                order.setInstallmentPlanId(installmentPlan.getId());
                order.setInstallmentCount(installmentPlan.getInstallmentCount());
                order.setMonthlyAmount(monthlyAmount);
            } else {
                order.setIsInstallment(0);
            }
            
            // 插入订单
            orderMapper.insertOrder(order);
            log.info("订单创建成功，订单ID：{}，订单号：{}", order.getId(), order.getOrderNo());
            
            // 8. 创建订单商品
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(cartItem.getProduct().getId());
                orderItem.setProductName(cartItem.getProduct().getName());
                orderItem.setProductImage(cartItem.getProduct().getImageUrl());
                orderItem.setProductPrice(cartItem.getProduct().getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setSubtotal(cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                orderItems.add(orderItem);
            }
            
            // 批量插入订单商品
            if (!orderItems.isEmpty()) {
                orderItemMapper.insertOrderItems(orderItems);
                log.info("订单商品创建成功，数量：{}", orderItems.size());
            }
            
            // 8.5. 扣减商品库存并更新销量
            for (CartItem cartItem : cartItems) {
                Long productId = cartItem.getProduct().getId();
                Integer quantity = cartItem.getQuantity();
                
                // 扣减库存
                int stockUpdateResult = productMapper.decreaseStockQuantity(productId, quantity);
                if (stockUpdateResult == 0) {
                    throw new RuntimeException("扣减商品库存失败，商品ID：" + productId + "，可能库存不足");
                }
                
                // 更新销量
                productMapper.increaseSalesCount(productId, quantity);
                log.info("商品{}库存扣减{}，销量增加{}", productId, quantity, quantity);
            }
            
            // 9. 清空购物车中的已下单商品
            for (Long cartItemId : request.getCartItemIds()) {
                cartMapper.deleteCartItem(cartItemId);
            }
            log.info("购物车商品清理完成");
            
            // 10. 构建响应
            CreateOrderResponse response = new CreateOrderResponse();
            response.setOrderId(order.getId());
            response.setOrderNo(order.getOrderNo());
            response.setTotalAmount(order.getTotalAmount());
            response.setActualAmount(order.getActualAmount());
            response.setIsInstallment(order.getIsInstallment() == 1);
            response.setInstallmentCount(order.getInstallmentCount());
            response.setMonthlyAmount(order.getMonthlyAmount());
            response.setPaymentMethod(order.getPaymentMethod());
            response.setReceiverAddress(order.getReceiverAddress());
            
            log.info("订单创建完成：{}", response);
            return response;
            
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new RuntimeException("创建订单失败: " + e.getMessage());
        }
    }

    @Override
    public Order getOrderById(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        return orderMapper.selectOrderById(orderId);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }
        return orderMapper.selectOrderByOrderNo(orderNo);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return orderMapper.selectOrdersByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByUserIdAndStatus(Long userId, String orderStatus) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return orderMapper.selectOrdersByUserIdAndStatus(userId, orderStatus);
    }

    @Override
    public boolean updateOrderStatus(Long orderId, String orderStatus) {
        if (orderId == null || orderStatus == null) {
            return false;
        }
        return orderMapper.updateOrderStatus(orderId, orderStatus) > 0;
    }

    @Override
    public boolean updatePaymentStatus(Long orderId, String paymentStatus) {
        if (orderId == null || paymentStatus == null) {
            return false;
        }
        return orderMapper.updatePaymentStatus(orderId, paymentStatus) > 0;
    }

    /**
     * 处理订单支付成功后的状态流转
     */
    public boolean processPaymentSuccess(Long orderId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return false;
            }

            // 更新支付状态为已支付
            boolean paymentUpdated = orderMapper.updatePaymentStatus(orderId, "PAID") > 0;
            if (!paymentUpdated) {
                return false;
            }

            // 根据支付方式和分期情况更新订单状态
            String newOrderStatus;
            if (order.getIsInstallment() == 1) {
                // 分期付款：首期支付成功后状态变为"PAID"
                newOrderStatus = "PAID";
            } else {
                // 全款支付：直接变为已支付状态
                newOrderStatus = "PAID";
            }

            // 更新订单状态
            boolean statusUpdated = orderMapper.updateOrderStatus(orderId, newOrderStatus) > 0;
            
            log.info("订单{}支付成功处理完成，订单状态：{}，支付状态：PAID", orderId, newOrderStatus);
            return statusUpdated;

        } catch (Exception e) {
            log.error("处理订单支付成功失败，订单ID：{}", orderId, e);
            return false;
        }
    }

    @Override
    public boolean cancelOrder(Long orderId, Long userId) {
        // 验证订单是否存在且属于当前用户
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有待付款状态的订单可以取消
        if (!"PENDING_PAYMENT".equals(order.getOrderStatus())) {
            throw new RuntimeException("当前订单状态不允许取消");
        }
        
        try {
            // 1. 更新订单状态为已取消
            boolean orderCancelled = orderMapper.updateOrderStatus(orderId, "CANCELLED") > 0;
            if (!orderCancelled) {
                return false;
            }

            // 2. 回滚库存和销量
            rollbackInventoryForOrder(orderId);
            
            log.info("订单{}取消成功，已回滚库存", orderId);
            return true;
        } catch (Exception e) {
            log.error("取消订单失败，订单ID：{}", orderId, e);
            throw new RuntimeException("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 为订单回滚库存和销量
     */
    private void rollbackInventoryForOrder(Long orderId) {
        try {
            // 获取订单商品列表
            List<OrderItem> orderItems = orderItemMapper.selectOrderItemsByOrderId(orderId);
            
            for (OrderItem orderItem : orderItems) {
                Long productId = orderItem.getProductId();
                Integer quantity = orderItem.getQuantity();
                
                // 回滚库存
                int stockRestoreResult = productMapper.increaseStockQuantity(productId, quantity);
                if (stockRestoreResult > 0) {
                    log.info("商品{}库存回滚成功，增加数量：{}", productId, quantity);
                } else {
                    log.warn("商品{}库存回滚失败", productId);
                }
                
                // 回滚销量
                int salesRollbackResult = productMapper.decreaseSalesCount(productId, quantity);
                if (salesRollbackResult > 0) {
                    log.info("商品{}销量回滚成功，减少数量：{}", productId, quantity);
                } else {
                    log.warn("商品{}销量回滚失败", productId);
                }
            }
        } catch (Exception e) {
            log.error("回滚订单{}库存失败", orderId, e);
            throw new RuntimeException("回滚库存失败: " + e.getMessage());
        }
    }

    @Override
    public boolean confirmReceive(Long orderId, Long userId) {
        // 验证订单是否存在且属于当前用户
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已发货状态的订单可以确认收货
        if (!"SHIPPED".equals(order.getOrderStatus())) {
            throw new RuntimeException("当前订单状态不允许确认收货");
        }
        
        // 更新订单状态为已完成
        Order updateOrder = new Order();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus("COMPLETED");
        updateOrder.setCompletedAt(LocalDateTime.now());
        
        return orderMapper.updateOrder(updateOrder) > 0;
    }

    @Override
    public String generateOrderNo() {
        // 生成订单号：ORD + 时间戳 + 3位递增序号
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long counter = ORDER_COUNTER.getAndIncrement() % 1000;
        return String.format("ORD%s%03d", timestamp, counter);
    }
    
    /**
     * 验证创建订单请求
     */
    private void validateCreateOrderRequest(Long userId, CreateOrderRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getCartItemIds() == null || request.getCartItemIds().isEmpty()) {
            throw new IllegalArgumentException("购买商品不能为空");
        }
        if (request.getAddressId() == null) {
            throw new IllegalArgumentException("收货地址不能为空");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("支付方式不能为空");
        }
        if (request.getExpectedAmount() == null || request.getExpectedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("订单金额必须大于0");
        }
        
        // 如果选择分期付款，必须提供分期方案ID
        if (Boolean.TRUE.equals(request.getIsInstallment()) && request.getInstallmentPlanId() == null) {
            throw new IllegalArgumentException("选择分期付款时必须提供分期方案");
        }
    }
    
    /**
     * 获取选中的购物车商品
     */
    private List<CartItem> getSelectedCartItems(List<Long> cartItemIds) {
        List<CartItem> cartItems = new ArrayList<>();
        for (Long cartItemId : cartItemIds) {
            CartItem cartItem = cartMapper.selectCartItemById(cartItemId);
            if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().getStatus() == 1) {
                // 验证库存
                if (cartItem.getProduct().getStockQuantity() < cartItem.getQuantity()) {
                    throw new RuntimeException("商品 " + cartItem.getProduct().getName() + " 库存不足");
                }
                cartItems.add(cartItem);
            }
        }
        return cartItems;
    }
    
    /**
     * 计算订单总金额
     */
    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotal = cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        return totalAmount;
    }
} 