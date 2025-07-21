package com.luckymall.controller;

import com.luckymall.common.Result;
import com.luckymall.entity.HumanServiceSession;
import com.luckymall.service.HumanServiceTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 人工客服控制器
 */
@RestController
@RequestMapping("/api/human-service")
@CrossOrigin(origins = "*")
@Slf4j
public class HumanServiceController {
    
    @Autowired
    private HumanServiceTransferService humanServiceTransferService;
    
    /**
     * 获取人工客服会话状态
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 会话状态
     */
    @GetMapping("/status")
    public Result<HumanServiceSession> getSessionStatus(@RequestParam Long userId, @RequestParam String sessionId) {
        try {
            HumanServiceSession session = humanServiceTransferService.getHumanServiceSession(userId, sessionId);
            if (session == null) {
                return Result.error("未找到人工客服会话");
            }
            return Result.success(session);
        } catch (Exception e) {
            log.error("获取人工客服会话状态失败: {}", e.getMessage(), e);
            return Result.error("获取人工客服会话状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 请求人工客服
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param aiSessionId AI会话ID
     * @param reason 转接原因
     * @return 会话状态
     */
    @PostMapping("/request")
    public Result<HumanServiceSession> requestHumanService(
            @RequestParam Long userId,
            @RequestParam String sessionId,
            @RequestParam(required = false) String aiSessionId,
            @RequestParam String reason) {
        try {
            // 创建人工客服会话
            HumanServiceSession session = humanServiceTransferService.createHumanServiceSession(
                    userId, sessionId, aiSessionId, reason, null);
            
            // 传递历史对话
            humanServiceTransferService.transferChatHistory(userId, sessionId, aiSessionId);
            
            // 分配客服
            boolean assigned = humanServiceTransferService.assignStaff(sessionId);
            if (!assigned) {
                return Result.error("暂无可用客服，请稍后再试");
            }
            
            return Result.success(session);
        } catch (Exception e) {
            log.error("请求人工客服失败: {}", e.getMessage(), e);
            return Result.error("请求人工客服失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消人工客服请求
     * @param sessionId 会话ID
     * @return 结果
     */
    @PostMapping("/cancel")
    public Result<Boolean> cancelHumanService(@RequestParam String sessionId) {
        try {
            HumanServiceSession session = humanServiceTransferService.updateSessionStatus(sessionId, "CANCELLED");
            return Result.success(session != null);
        } catch (Exception e) {
            log.error("取消人工客服请求失败: {}", e.getMessage(), e);
            return Result.error("取消人工客服请求失败: " + e.getMessage());
        }
    }
    
    /**
     * 完成人工客服会话
     * @param sessionId 会话ID
     * @return 结果
     */
    @PostMapping("/complete")
    public Result<Boolean> completeHumanService(@RequestParam String sessionId) {
        try {
            HumanServiceSession session = humanServiceTransferService.updateSessionStatus(sessionId, "COMPLETED");
            return Result.success(session != null);
        } catch (Exception e) {
            log.error("完成人工客服会话失败: {}", e.getMessage(), e);
            return Result.error("完成人工客服会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取等待队列信息
     * @return 等待队列信息
     */
    @GetMapping("/queue-info")
    public Result<Map<String, Object>> getQueueInfo() {
        try {
            // 获取等待中的会话列表
            List<HumanServiceSession> waitingSessions = humanServiceTransferService.getHumanServiceSessionMapper().findByStatus("WAITING");
            
            // 计算预计等待时间（简化实现，假设每个会话处理时间为5分钟）
            int queuePosition = 0;
            for (int i = 0; i < waitingSessions.size(); i++) {
                if (waitingSessions.get(i).getSessionId().equals("currentSessionId")) {
                    queuePosition = i + 1;
                    break;
                }
            }
            
            int estimatedWaitTime = queuePosition * 5; // 预计等待时间（分钟）
            
            Map<String, Object> queueInfo = Map.of(
                    "queueLength", waitingSessions.size(),
                    "queuePosition", queuePosition,
                    "estimatedWaitTime", estimatedWaitTime
            );
            
            return Result.success(queueInfo);
        } catch (Exception e) {
            log.error("获取等待队列信息失败: {}", e.getMessage(), e);
            return Result.error("获取等待队列信息失败: " + e.getMessage());
        }
    }
} 