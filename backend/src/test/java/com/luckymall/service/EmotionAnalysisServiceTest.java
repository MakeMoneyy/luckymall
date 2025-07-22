package com.luckymall.service;

import com.luckymall.dto.ChatContext;
import com.luckymall.dto.EmotionAnalysisResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 情感分析服务测试类
 */
@SpringBootTest
public class EmotionAnalysisServiceTest {

    @Autowired
    private EmotionAnalysisService emotionAnalysisService;

    @Test
    public void testPositiveEmotion() {
        // 准备测试数据
        String message = "非常感谢您的帮助，服务真的太好了！";
        ChatContext context = new ChatContext();

        // 调用服务
        EmotionAnalysisResult result = emotionAnalysisService.analyzeEmotion(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals("POSITIVE", result.getEmotionType());
        assertTrue(result.getEmotionIntensity() >= 4);
        assertFalse(result.getSuggestHumanService());
    }

    @Test
    public void testNegativeEmotion() {
        // 准备测试数据
        String message = "这个服务太差劲了，我要投诉你们！";
        ChatContext context = new ChatContext();

        // 调用服务
        EmotionAnalysisResult result = emotionAnalysisService.analyzeEmotion(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals("NEGATIVE", result.getEmotionType());
        assertTrue(result.getEmotionIntensity() <= 2);
        assertTrue(result.getSuggestHumanService());
    }

    @Test
    public void testNeutralEmotion() {
        // 准备测试数据
        String message = "我想查询一下我的订单状态";
        ChatContext context = new ChatContext();

        // 调用服务
        EmotionAnalysisResult result = emotionAnalysisService.analyzeEmotion(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals("NEUTRAL", result.getEmotionType());
        assertEquals(3, result.getEmotionIntensity());
        assertFalse(result.getSuggestHumanService());
    }

    @Test
    public void testEmotionTrend() {
        // 准备测试数据
        ChatContext context = new ChatContext();
        
        // 第一次情绪分析 - 中性
        EmotionAnalysisResult result1 = emotionAnalysisService.analyzeEmotion("我想查询一下我的订单", context);
        String trend1 = emotionAnalysisService.trackEmotionTrend(context, result1);
        assertEquals("STABLE", trend1);
        
        // 第二次情绪分析 - 稍微负面
        EmotionAnalysisResult result2 = emotionAnalysisService.analyzeEmotion("为什么我的订单还没发货？", context);
        String trend2 = emotionAnalysisService.trackEmotionTrend(context, result2);
        
        // 第三次情绪分析 - 更负面
        EmotionAnalysisResult result3 = emotionAnalysisService.analyzeEmotion("这太让人失望了，我等了好几天了！", context);
        String trend3 = emotionAnalysisService.trackEmotionTrend(context, result3);
        
        assertEquals("DETERIORATING", trend3);
    }

    @Test
    public void testShouldTransferToHuman() {
        // 准备测试数据
        ChatContext context = new ChatContext();
        
        // 强烈负面情绪
        EmotionAnalysisResult negativeResult = new EmotionAnalysisResult();
        negativeResult.setEmotionType("NEGATIVE");
        negativeResult.setEmotionIntensity(1);
        negativeResult.setSuggestHumanService(true);
        
        boolean shouldTransfer = emotionAnalysisService.shouldTransferToHuman(negativeResult, context);
        assertTrue(shouldTransfer);
        
        // 正面情绪
        EmotionAnalysisResult positiveResult = new EmotionAnalysisResult();
        positiveResult.setEmotionType("POSITIVE");
        positiveResult.setEmotionIntensity(5);
        positiveResult.setSuggestHumanService(false);
        
        boolean shouldNotTransfer = emotionAnalysisService.shouldTransferToHuman(positiveResult, context);
        assertFalse(shouldNotTransfer);
    }
} 