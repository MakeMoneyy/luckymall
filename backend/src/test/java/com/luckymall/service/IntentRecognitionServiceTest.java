package com.luckymall.service;

import com.luckymall.dto.ChatContext;
import com.luckymall.dto.IntentRecognitionResult;
import com.luckymall.enums.IntentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 意图识别服务测试类
 */
@SpringBootTest
public class IntentRecognitionServiceTest {

    @Autowired
    private IntentRecognitionService intentRecognitionService;

    @Test
    public void testOrderQueryIntent() {
        // 准备测试数据
        String message = "我想查询一下我的订单状态";
        ChatContext context = new ChatContext();

        // 调用服务
        IntentRecognitionResult result = intentRecognitionService.recognizeIntent(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals(IntentType.ORDER_QUERY, result.getIntentType());
        assertTrue(result.getConfidence() > 0.5f);
    }

    @Test
    public void testProductQueryIntent() {
        // 准备测试数据
        String message = "这个手机多少钱？";
        ChatContext context = new ChatContext();
        context.setCurrentProductId(1L);
        
        Map<String, Object> recognizedEntities = new HashMap<>();
        recognizedEntities.put("currentPage", "product_detail");
        context.setRecognizedEntities(recognizedEntities);

        // 调用服务
        IntentRecognitionResult result = intentRecognitionService.recognizeIntent(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals(IntentType.PRODUCT_QUERY, result.getIntentType());
        assertTrue(result.getConfidence() > 0.5f);
    }

    @Test
    public void testPaymentQueryIntent() {
        // 准备测试数据
        String message = "我可以用信用卡分期付款吗？";
        ChatContext context = new ChatContext();

        // 调用服务
        IntentRecognitionResult result = intentRecognitionService.recognizeIntent(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals(IntentType.PAYMENT_QUERY, result.getIntentType());
        assertTrue(result.getConfidence() > 0.5f);
    }

    @Test
    public void testLogisticsQueryIntent() {
        // 准备测试数据
        String message = "我的包裹什么时候能到？";
        ChatContext context = new ChatContext();

        // 调用服务
        IntentRecognitionResult result = intentRecognitionService.recognizeIntent(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals(IntentType.LOGISTICS_QUERY, result.getIntentType());
        assertTrue(result.getConfidence() > 0.5f);
    }

    @Test
    public void testReturnRefundIntent() {
        // 准备测试数据
        String message = "我想退货，这个商品质量有问题";
        ChatContext context = new ChatContext();

        // 调用服务
        IntentRecognitionResult result = intentRecognitionService.recognizeIntent(message, context);

        // 验证结果
        assertNotNull(result);
        assertEquals(IntentType.RETURN_REFUND, result.getIntentType());
        assertTrue(result.getConfidence() > 0.5f);
    }

    @Test
    public void testExtractEntities() {
        // 准备测试数据
        String message = "我想查询一下订单号为123456的物流状态";
        ChatContext context = new ChatContext();

        // 调用服务
        IntentRecognitionResult result = intentRecognitionService.extractEntities(message, context);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getExtractedEntities());
        assertTrue(result.getExtractedEntities().containsKey("orderNumber"));
        assertEquals("123456", result.getExtractedEntities().get("orderNumber"));
    }
} 