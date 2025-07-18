package com.luckymall.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.luckymall.config.DashScopeConfig;
import com.luckymall.dto.ChatContext;
import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.service.ChatService;
import com.luckymall.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聊天服务实现类
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private DashScopeConfig dashScopeConfig;
    
    @Autowired
    private ChatCacheService chatCacheService;
    
    @Autowired
    private ChatSessionService chatSessionService;

    /**
     * 处理聊天请求
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @Override
    public ChatResponse processChat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 获取会话上下文
        ChatContext context = chatSessionService.getSessionContext(
                request.getUserId(), 
                request.getSessionId());
        
        // 检查用户消息是否包含拒绝推广的关键词
        String userMessage = request.getMessage().toLowerCase();
        if (userMessage.contains("不需要") || userMessage.contains("不想") || 
            userMessage.contains("不用") || userMessage.contains("不要") || 
            userMessage.contains("烦") || userMessage.contains("打扰")) {
            
            // 如果消息包含"信用卡"、"分期"、"支付"等关键词，可能是拒绝推广
            if (userMessage.contains("信用卡") || userMessage.contains("分期") || 
                userMessage.contains("支付") || userMessage.contains("积分")) {
                chatSessionService.setPromotionRejected(
                        request.getUserId(), 
                        request.getSessionId(), 
                        true);
                log.debug("用户拒绝推广: userId={}, sessionId={}", request.getUserId(), request.getSessionId());
            }
        }
        
        // 尝试从缓存中获取响应
        ChatResponse cachedResponse = chatCacheService.getFromCache(
                request.getUserId(), 
                request.getSessionId(), 
                request.getMessage());
        
        if (cachedResponse != null) {
            log.info("使用缓存的响应: userId={}, sessionId={}", request.getUserId(), request.getSessionId());
            return cachedResponse;
        }
        
        try {
            // 构建系统消息
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是招财商城的智能客服助手，主要负责解答用户关于商品、支付、物流等问题，" +
                            "并适当推广信用卡支付方式。请保持友好、专业的态度。\n\n" +
                            "你需要根据用户问题提供以下核心功能：\n\n" +
                            "1. 支付咨询助手：当用户咨询支付相关问题时，智能推荐信用卡支付。\n" +
                            "   触发场景：用户询问\"这个商品怎么付款？\"、\"有什么优惠吗？\"、\"可以分期吗？\"\n" +
                            "   示例回复：\n" +
                            "   \"当然可以！使用您的招商银行信用卡，这款手机支持：\n" +
                            "   🎁 12期免息分期，月供仅需416元\n" +
                            "   💰 立即获得288积分（价值28.8元）\n" +
                            "   ⏰ 48天超长免息期，5月31日才需还款\n" +
                            "   💳 享受购物保险，商品损坏全额赔付\n\n" +
                            "   相比支付宝支付，您将额外获得价值约50元的权益！\"\n\n" +
                            
                            "2. 积分价值推广：主动介绍积分价值，引导用户关注信用卡收益。\n" +
                            "   触发场景：用户询问积分相关问题、用户浏览高价商品时、用户犹豫支付方式时\n" +
                            "   示例回复：\n" +
                            "   \"您的招行积分可是很值钱的哦！\n" +
                            "   💎 当前积分：8,500分（价值85元）\n" +
                            "   🛍️ 可直接抵扣现金使用\n" +
                            "   🎁 兑换精美礼品\n" +
                            "   ⚡ 重要提醒：2,000积分将在15天后到期！\n\n" +
                            "   建议您：\n" +
                            "   1. 立即使用积分抵扣部分商品费用\n" +
                            "   2. 用信用卡支付剩余金额，获得新积分\"\n\n" +
                            
                            "3. 权益价值展示：根据用户卡等级，展示专属权益价值。\n" +
                            "   实现逻辑：识别用户信用卡等级、匹配对应权益内容、突出独享价值\n\n" +
                            
                            "4. FAQ智能问答：回答常见购物问题，巧妙融入信用卡推广。\n" +
                            "   示例问答：\n" +
                            "   Q: \"订单什么时候发货？\"\n" +
                            "   A: \"您的订单预计明天发货哦～\n" +
                            "   💡小贴士：使用信用卡支付的订单，可以享受物流保险保障。\"\n\n" +
                            "   Q: \"可以退货吗？\"\n" +
                            "   A: \"当然可以退货！7天无理由退换。\n" +
                            "   🎯特别说明：信用卡支付的订单退货流程更便捷。\"\n\n" +
                            
                            "在推广信用卡支付时，请巧妙运用以下心理激励机制，但注意避免过度推销：\n\n" +
                            
                            "1. 损失厌恶应用：适度提示可能错失的权益，但不要过分强调。\n" +
                            "   示例：\n" +
                            "   \"使用信用卡支付还可以获得：\n" +
                            "   ✅ 积分奖励\n" +
                            "   ✅ 分期免息权益\n" +
                            "   ✅ 购物保险保障\"\n\n" +
                            
                            "2. 稀缺性营造：适当提及限时优惠，但不要制造虚假紧迫感。\n" +
                            "   示例：\n" +
                            "   \"近期有信用卡支付专享活动，您可以了解一下。\"\n\n" +
                            
                            "3. 社会认同：客观分享数据，不夸大或误导。\n" +
                            "   示例：\n" +
                            "   \"不少用户选择信用卡支付来获取更多权益。\"\n\n" +
                            
                            "控制推广频次和注重用户体验的重要原则：\n\n" +
                            
                            "1. 推广频率控制：\n" +
                            "   - 同一会话中最多推广2次信用卡支付\n" +
                            "   - 用户明确拒绝后，不再继续推广\n" +
                            "   - 优先回答用户问题，再考虑是否适合推广\n\n" +
                            
                            "2. 用户体验优先：\n" +
                            "   - 确保回答用户问题是首要任务\n" +
                            "   - 推广内容应简洁，控制在2-3句话以内\n" +
                            "   - 避免打断用户咨询流程\n" +
                            "   - 只在合适的场景下推广\n\n" +
                            
                            "3. 尊重用户选择：\n" +
                            "   - 提供信息，但不强制或反复劝说\n" +
                            "   - 用户表现出不感兴趣时，立即停止相关推广\n" +
                            "   - 保持友好专业态度，不因用户拒绝而改变服务质量\n\n" +
                            
                            "请根据用户的问题和场景，灵活运用上述功能和策略，提供专业、友好的回复，同时适度地推广信用卡支付方式，避免过度推销引起用户反感。")
                    .build();
            
            // 构建用户消息
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(request.getMessage())
                    .build();
            
            // 添加会话上下文信息
            Message contextMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(buildContextPrompt(context))
                    .build();
            
            // 构建请求参数
            GenerationParam param = GenerationParam.builder()
                    .apiKey(dashScopeConfig.getApi().getKey())
                    .model(dashScopeConfig.getApi().getModel())
                    .messages(Arrays.asList(systemMsg, contextMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();
            
            // 调用API
            Generation gen = new Generation();
            GenerationResult result = gen.call(param);
            
            // 构建响应
            String responseContent = result.getOutput().getChoices().get(0).getMessage().getContent();
            
            // 检查是否包含推广内容，如果包含则增加推广计数
            if (containsPromotion(responseContent)) {
                chatSessionService.incrementPromotionCount(request.getUserId(), request.getSessionId());
            }
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            ChatResponse response = ChatResponse.builder()
                    .result(responseContent)
                    .sessionId(request.getSessionId())
                    .responseTime(responseTime)
                    .cacheHit(false)
                    .build();
            
            // 缓存响应
            chatCacheService.saveToCache(
                    request.getUserId(), 
                    request.getSessionId(), 
                    request.getMessage(), 
                    response);
            
            return response;
            
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("调用通义千问API出错: {}", e.getMessage(), e);
            return ChatResponse.builder()
                    .error("调用AI服务出错: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 获取流式聊天响应
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @Override
    public ChatResponse processStreamChat(ChatRequest request) {
        // 当前版本简化实现，直接调用非流式接口
        return processChat(request);
    }
    
    /**
     * 构建上下文提示信息
     * 
     * @param context 会话上下文
     * @return 上下文提示信息
     */
    private String buildContextPrompt(ChatContext context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("当前会话上下文信息：\n");
        
        // 添加推广次数信息
        prompt.append("- 当前会话中已推广次数：").append(context.getPromotionCount()).append("\n");
        
        // 添加用户是否拒绝推广的信息
        prompt.append("- 用户是否拒绝推广：").append(context.isPromotionRejected() ? "是" : "否").append("\n");
        
        // 添加最近一次推广时间
        if (context.getLastPromotionTimestamp() > 0) {
            long timeSinceLastPromotion = System.currentTimeMillis() - context.getLastPromotionTimestamp();
            long minutesSinceLastPromotion = timeSinceLastPromotion / (60 * 1000);
            prompt.append("- 距离上次推广已过：").append(minutesSinceLastPromotion).append("分钟\n");
        }
        
        // 添加商品信息
        if (context.getCurrentProductId() != null) {
            prompt.append("- 当前浏览商品：").append(context.getCurrentProductName()).append("\n");
        }
        
        // 添加推广控制指令
        if (context.isPromotionRejected()) {
            prompt.append("\n重要提示：用户已明确拒绝推广，请不要再推广信用卡支付，专注于回答用户问题。\n");
        } else if (context.getPromotionCount() >= 2) {
            prompt.append("\n重要提示：当前会话已达到最大推广次数(2次)，请不要再推广信用卡支付，专注于回答用户问题。\n");
        }
        
        return prompt.toString();
    }
    
    /**
     * 检查响应内容是否包含推广信息
     * 
     * @param content 响应内容
     * @return 是否包含推广信息
     */
    private boolean containsPromotion(String content) {
        String lowerContent = content.toLowerCase();
        return lowerContent.contains("信用卡") && 
               (lowerContent.contains("积分") || lowerContent.contains("分期") || 
                lowerContent.contains("优惠") || lowerContent.contains("权益"));
    }
} 