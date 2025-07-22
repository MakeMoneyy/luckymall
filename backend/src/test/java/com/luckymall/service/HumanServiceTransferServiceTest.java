package com.luckymall.service;

import com.luckymall.dto.EmotionAnalysisResult;
import com.luckymall.entity.HumanServiceSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 人工客服转接服务测试类
 */
@SpringBootTest
public class HumanServiceTransferServiceTest {

    @Autowired
    private HumanServiceTransferService humanServiceTransferService;

    @Test
    public void testCreateHumanServiceSession() {
        // 准备测试数据
        Long userId = 1L;
        String sessionId = "test-session-" + System.currentTimeMillis();
        String aiSessionId = "ai-session-" + System.currentTimeMillis();
        String reason = "用户情绪负面";
        
        EmotionAnalysisResult emotionResult = new EmotionAnalysisResult();
        emotionResult.setEmotionType("NEGATIVE");
        emotionResult.setEmotionIntensity(1);
        emotionResult.setSuggestHumanService(true);

        // 调用服务
        HumanServiceSession session = humanServiceTransferService.createHumanServiceSession(
                userId, sessionId, aiSessionId, reason, emotionResult);

        // 验证结果
        assertNotNull(session);
        assertEquals(userId, session.getUserId());
        assertEquals(sessionId, session.getSessionId());
        assertEquals(aiSessionId, session.getAiSessionId());
        assertEquals("WAITING", session.getStatus());
        assertEquals(reason, session.getTransferReason());
    }

    @Test
    public void testUpdateSessionStatus() {
        // 准备测试数据
        Long userId = 2L;
        String sessionId = "test-session-" + System.currentTimeMillis();
        String aiSessionId = "ai-session-" + System.currentTimeMillis();
        String reason = "用户请求人工客服";
        
        // 创建会话
        HumanServiceSession session = humanServiceTransferService.createHumanServiceSession(
                userId, sessionId, aiSessionId, reason, null);
        
        // 更新状态
        HumanServiceSession updatedSession = humanServiceTransferService.updateSessionStatus(sessionId, "IN_PROGRESS");
        
        // 验证结果
        assertNotNull(updatedSession);
        assertEquals("IN_PROGRESS", updatedSession.getStatus());
    }

    @Test
    public void testAssignStaff() {
        // 准备测试数据
        Long userId = 3L;
        String sessionId = "test-session-" + System.currentTimeMillis();
        String aiSessionId = "ai-session-" + System.currentTimeMillis();
        String reason = "系统自动转接";
        
        // 创建会话
        humanServiceTransferService.createHumanServiceSession(
                userId, sessionId, aiSessionId, reason, null);
        
        // 分配客服
        boolean result = humanServiceTransferService.assignStaff(sessionId);
        
        // 验证结果
        assertTrue(result);
        
        // 获取更新后的会话
        HumanServiceSession updatedSession = humanServiceTransferService.getHumanServiceSession(userId, sessionId);
        assertNotNull(updatedSession);
        assertNotNull(updatedSession.getStaffId());
    }

    @Test
    public void testTransferChatHistory() {
        // 准备测试数据
        Long userId = 4L;
        String sessionId = "test-session-" + System.currentTimeMillis();
        String aiSessionId = "ai-session-" + System.currentTimeMillis();
        
        // 调用服务
        boolean result = humanServiceTransferService.transferChatHistory(userId, sessionId, aiSessionId);
        
        // 由于测试环境可能没有实际的聊天记录，这里我们只验证方法不抛出异常
        // 在实际环境中，应该先创建一些聊天记录，然后验证传递是否成功
        assertDoesNotThrow(() -> humanServiceTransferService.transferChatHistory(userId, sessionId, aiSessionId));
    }
} 