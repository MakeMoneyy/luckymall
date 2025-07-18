package com.luckymall.controller;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 处理聊天请求
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("收到聊天请求: {}", request);
        
        // 如果sessionId为空，生成一个新的
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            request.setSessionId(UUID.randomUUID().toString());
        }
        
        ChatResponse response = chatService.processChat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 处理流式聊天请求
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param message 消息内容
     * @param context 上下文信息
     * @return 聊天响应
     */
    @GetMapping("/stream")
    public ResponseEntity<ChatResponse> streamChat(
            @RequestParam String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam String message,
            @RequestParam(required = false) Map<String, Object> context) {
        
        // 构建请求对象
        ChatRequest request = new ChatRequest();
        request.setUserId(userId);
        request.setSessionId(sessionId != null ? sessionId : UUID.randomUUID().toString());
        request.setMessage(message);
        request.setContext(context);
        
        log.info("收到流式聊天请求: {}", request);
        
        ChatResponse response = chatService.processStreamChat(request);
        return ResponseEntity.ok(response);
    }
} 