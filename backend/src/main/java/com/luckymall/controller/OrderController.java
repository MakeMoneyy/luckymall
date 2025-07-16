package com.luckymall.controller;

import com.luckymall.common.Result;
import com.luckymall.dto.CreateOrderRequest;
import com.luckymall.dto.CreateOrderResponse;
import com.luckymall.entity.InstallmentPlan;
import com.luckymall.entity.Order;
import com.luckymall.service.InstallmentPlanService;
import com.luckymall.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private InstallmentPlanService installmentPlanService;

    /**
     * 创建订单
     */
    @PostMapping("/{userId}")
    public Result<CreateOrderResponse> createOrder(@PathVariable Long userId,
                                                 @Valid @RequestBody CreateOrderRequest request) {
        try {
            CreateOrderResponse response = orderService.createOrder(userId, request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return Result.error("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getOrdersByUserId(@PathVariable Long userId,
                                               @RequestParam(required = false) String status) {
        try {
            List<Order> orders;
            if (status != null && !status.trim().isEmpty()) {
                orders = orderService.getOrdersByUserIdAndStatus(userId, status);
            } else {
                orders = orderService.getOrdersByUserId(userId);
            }
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询用户订单失败", e);
            return Result.error("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public Result<String> cancelOrder(@PathVariable Long orderId,
                                    @RequestParam Long userId) {
        try {
            boolean success = orderService.cancelOrder(orderId, userId);
            if (success) {
                return Result.success("订单取消成功");
            } else {
                return Result.error("订单取消失败");
            }
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.error("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/{orderId}/confirm")
    public Result<String> confirmReceive(@PathVariable Long orderId,
                                       @RequestParam Long userId) {
        try {
            boolean success = orderService.confirmReceive(orderId, userId);
            if (success) {
                return Result.success("确认收货成功");
            } else {
                return Result.error("确认收货失败");
            }
        } catch (Exception e) {
            log.error("确认收货失败", e);
            return Result.error("确认收货失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有启用的分期方案
     */
    @GetMapping("/installment-plans")
    public Result<List<InstallmentPlan>> getInstallmentPlans() {
        try {
            List<InstallmentPlan> plans = installmentPlanService.getActiveInstallmentPlans();
            return Result.success(plans);
        } catch (Exception e) {
            log.error("查询分期方案失败", e);
            return Result.error("查询分期方案失败: " + e.getMessage());
        }
    }

    /**
     * 根据金额查询可用的分期方案
     */
    @GetMapping("/installment-plans/available")
    public Result<List<InstallmentPlan>> getAvailableInstallmentPlans(@RequestParam BigDecimal amount) {
        try {
            List<InstallmentPlan> plans = installmentPlanService.getAvailableInstallmentPlans(amount);
            return Result.success(plans);
        } catch (Exception e) {
            log.error("查询可用分期方案失败", e);
            return Result.error("查询分期方案失败: " + e.getMessage());
        }
    }
} 