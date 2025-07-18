package com.luckymall.controller;

import com.luckymall.common.Result;
import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.dto.UserCreditCardResponse;
import com.luckymall.service.CustomerServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer-service")
@CrossOrigin(origins = "*")
public class CustomerServiceController {
    
    @Autowired
    private CustomerServiceService customerServiceService;
    
    /**
     * 发送聊天消息
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            ChatResponse response = customerServiceService.processChat(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("聊天服务异常：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户信用卡信息
     */
    @GetMapping("/user/credit-card-info/{userId}")
    public Result<UserCreditCardResponse> getUserCreditCard(@PathVariable Long userId) {
        try {
            UserCreditCardResponse response = customerServiceService.getUserCreditCard(userId);
            if (response == null) {
                return Result.error("未找到用户信用卡信息");
            }
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取信用卡信息异常：" + e.getMessage());
        }
    }
} 