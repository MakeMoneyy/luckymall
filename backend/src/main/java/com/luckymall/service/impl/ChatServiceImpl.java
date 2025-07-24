package com.luckymall.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckymall.config.DashScopeConfig;
import com.luckymall.dto.ChatContext;
import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.dto.EmotionAnalysisResult;
import com.luckymall.dto.IntentRecognitionResult;
import com.luckymall.entity.CustomerServiceChat;
import com.luckymall.entity.HumanServiceSession;
import com.luckymall.enums.IntentType;
import com.luckymall.service.ChatService;
import com.luckymall.service.ChatSessionService;
import com.luckymall.service.EmotionAnalysisService;
import com.luckymall.service.HumanServiceTransferService;
import com.luckymall.service.IntentRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    @Autowired
    private IntentRecognitionService intentRecognitionService;
    
    @Autowired
    private EmotionAnalysisService emotionAnalysisService;
    
    @Autowired
    private HumanServiceTransferService humanServiceTransferService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 创建线程池用于并行处理
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    // API调用频率控制
    private final AtomicInteger apiCallCounter = new AtomicInteger(0);
    private final long API_RATE_WINDOW_MS = 60000; // 1分钟窗口
    private final int API_RATE_LIMIT = 100; // 每分钟最大调用次数
    private long lastResetTime = System.currentTimeMillis();

    /**
     * 处理聊天请求
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @Override
    public ChatResponse processChat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 1. 检查缓存
        ChatResponse cachedResponse = chatCacheService.getChatResponseCache(request);
        if (cachedResponse != null) {
            cachedResponse.setCacheHit(true);
            cachedResponse.setResponseTimeMs((int)(System.currentTimeMillis() - startTime));
            log.debug("缓存命中，耗时: {}ms", cachedResponse.getResponseTimeMs());
            return cachedResponse;
        }
        
        // 2. 获取会话上下文
        ChatContext context = chatSessionService.getSessionContext(
                request.getUserId(), 
                request.getSessionId());
        
        // 3. 并行处理意图识别和情感分析
        CompletableFuture<IntentRecognitionResult> intentFuture = CompletableFuture.supplyAsync(() -> {
            // 先检查缓存
            IntentRecognitionResult cachedIntent = chatCacheService.getIntentCache(request.getMessage());
            if (cachedIntent != null) {
                return cachedIntent;
            }
            
            // 执行意图识别
            IntentRecognitionResult result = intentRecognitionService.recognizeIntent(request.getMessage(), context);
            
            // 缓存结果
            chatCacheService.saveIntentCache(request.getMessage(), result);
            
            return result;
        }, executorService);
        
        CompletableFuture<EmotionAnalysisResult> emotionFuture = CompletableFuture.supplyAsync(() -> {
            // 先检查缓存
            EmotionAnalysisResult cachedEmotion = chatCacheService.getEmotionCache(request.getMessage());
            if (cachedEmotion != null) {
                return cachedEmotion;
            }
            
            // 执行情感分析
            EmotionAnalysisResult result = emotionAnalysisService.analyzeEmotion(request.getMessage(), context);
            
            // 缓存结果
            chatCacheService.saveEmotionCache(request.getMessage(), result);
            
            return result;
        }, executorService);
        
        // 4. 等待并获取结果
        IntentRecognitionResult intentResult;
        EmotionAnalysisResult emotionResult;
        
        try {
            intentResult = intentFuture.get(5, TimeUnit.SECONDS);
            emotionResult = emotionFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("并行处理失败: {}", e.getMessage(), e);
            // 降级处理：串行执行
            intentResult = intentRecognitionService.recognizeIntent(request.getMessage(), context);
            emotionResult = emotionAnalysisService.analyzeEmotion(request.getMessage(), context);
        }
        
        log.debug("意图识别结果: type={}, confidence={}", 
                intentResult.getIntentType(), intentResult.getConfidence());
        log.debug("情感分析结果: type={}, intensity={}", 
                emotionResult.getEmotionType(), emotionResult.getEmotionIntensity());
        
        // 5. 检查是否需要转人工客服
        boolean needHumanService = emotionAnalysisService.shouldTransferToHuman(emotionResult, context);
        
        // 6. 根据意图和情绪生成响应
        ChatResponse response = generateResponse(request, context, intentResult, emotionResult, needHumanService);
        
        // 7. 如果需要转人工，创建人工客服会话
        if (needHumanService) {
            log.info("检测到需要转人工客服: userId={}, sessionId={}", request.getUserId(), request.getSessionId());
            
            try {
                Long userId = Long.valueOf(request.getUserId());
                HumanServiceSession session = humanServiceTransferService.createHumanServiceSession(
                        userId, 
                        request.getSessionId(), 
                        request.getSessionId(), 
                        "情绪分析触发转人工", 
                        emotionResult);
                
                // 传递历史对话
                humanServiceTransferService.transferChatHistory(
                        userId, 
                        request.getSessionId(), 
                        request.getSessionId());
                
                // 分配客服
                humanServiceTransferService.assignStaff(request.getSessionId());
                
                // 在响应中添加转人工提示
                response.setResult(response.getResult() + "\n\n[系统提示] 已将您的对话转接给人工客服，请稍候...");
            } catch (Exception e) {
                log.error("创建人工客服会话失败: {}", e.getMessage(), e);
            }
        }
        
        // 8. 缓存响应结果
        response.setCacheHit(false);
        response.setResponseTimeMs((int)(System.currentTimeMillis() - startTime));
        
        // 只有非人工转接的响应才缓存
        if (!needHumanService) {
            chatCacheService.saveChatResponseCache(request, response);
        }
        
        log.debug("处理完成，总耗时: {}ms", response.getResponseTimeMs());
        return response;
    }

    /**
     * 生成流式聊天响应
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @Override
    public ChatResponse processStreamChat(ChatRequest request) {
        // 流式处理与普通处理类似，但使用流式API
        // 这里简化实现，直接调用普通处理
        return processChat(request);
    }

    /**
     * 根据意图和情绪生成响应
     * 
     * @param request 聊天请求
     * @param context 对话上下文
     * @param intentResult 意图识别结果
     * @param emotionResult 情感分析结果
     * @param needHumanService 是否需要转人工
     * @return 聊天响应
     */
    private ChatResponse generateResponse(
            ChatRequest request, 
            ChatContext context,
            IntentRecognitionResult intentResult,
            EmotionAnalysisResult emotionResult,
            boolean needHumanService) {
        
        // 检查API调用频率
        checkAndUpdateApiRateLimit();
        
        // 构建提示词
        List<Message> messages = buildPromptMessages(request, context, intentResult, emotionResult, needHumanService);
        
        // 调用通义千问API
        try {
            Generation gen = new Generation();
            GenerationParam param = GenerationParam.builder()
                    .model(dashScopeConfig.getApi().getModel())
                    .messages(messages)
                    .resultFormat("message")
                    .build();
            
            // 记录API调用
            apiCallCounter.incrementAndGet();
            
            // 执行API调用
            GenerationResult result = gen.call(param);
            
            // 解析结果
            String responseText = result.getOutput().getChoices().get(0).getMessage().getContent();
            
            // 构建响应
            ChatResponse response = new ChatResponse();
            response.setResult(responseText);
            response.setSessionId(request.getSessionId());
            response.setResponseTime(System.currentTimeMillis());
            response.setMessage(responseText);
            
            // 生成适度的信用卡推广内容
            String promotionContent = generateCreditCardPromotion(context, emotionResult, intentResult);
            
            // 如果有合适的推广内容，添加到响应中
            if (promotionContent != null && !promotionContent.isEmpty()) {
                // 添加一个空行作为分隔
                String enhancedResponse = responseText + "\n\n" + promotionContent;
                response.setResult(enhancedResponse);
                response.setMessage(enhancedResponse);
                
                // 添加信用卡相关的推荐选项
                List<String> suggestions = new ArrayList<>();
                if (response.getSuggestions() != null && !response.getSuggestions().isEmpty()) {
                    suggestions.addAll(response.getSuggestions());
                }
                
                // 根据意图添加适当的信用卡相关选项，但限制总数不超过5个
                if (suggestions.size() < 5) {
                    if (!suggestions.contains("了解信用卡权益")) {
                        suggestions.add("了解信用卡权益");
                    }
                    if (suggestions.size() < 5 && !suggestions.contains("查询我的积分")) {
                        suggestions.add("查询我的积分");
                    }
                }
                
                response.setSuggestions(suggestions);
                
                // 添加促销信息
                ChatResponse.PromotionInfo promotionInfo = new ChatResponse.PromotionInfo();
                promotionInfo.setType("credit_card_benefits");
                
                // 如果上下文中有商品价格，计算可获得的积分
                if (context.getCurrentProductPrice() != null) {
                    int points = context.getCurrentProductPrice().intValue();
                    promotionInfo.setPointsEarned(points);
                }
                
                response.setPromotionInfo(promotionInfo);
            } else {
                // 添加情感和意图信息到响应中
                if (emotionResult != null) {
                    // 使用message字段存储额外信息
                    String emotionInfo = "情绪类型: " + emotionResult.getEmotionType() + 
                                        ", 强度: " + emotionResult.getEmotionIntensity();
                    response.setMessage(response.getMessage() + "\n" + emotionInfo);
                }
                
                if (intentResult != null) {
                    // 使用suggestions字段存储推荐选项
                    response.setSuggestions(generateSuggestions(intentResult));
                }
            }
            
            return response;
            
        } catch (NoApiKeyException | InputRequiredException | ApiException e) {
            log.error("API调用失败: {}", e.getMessage(), e);
            
            // 返回降级响应
            ChatResponse fallbackResponse = new ChatResponse();
            fallbackResponse.setResult("非常抱歉，系统暂时无法处理您的请求，请稍后再试。");
            fallbackResponse.setSessionId(request.getSessionId());
            fallbackResponse.setResponseTime(System.currentTimeMillis());
            
            return fallbackResponse;
        }
    }
    
    /**
     * 构建提示词消息列表
     */
    private List<Message> buildPromptMessages(
            ChatRequest request, 
            ChatContext context,
            IntentRecognitionResult intentResult,
            EmotionAnalysisResult emotionResult,
            boolean needHumanService) {
        
        List<Message> messages = new ArrayList<>();
        
        // 系统提示词
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("你是招财商城的智能客服助手，名叫\"招财猫\"。你的任务是帮助用户解答问题，提供商品和服务信息。");
        systemPrompt.append("\n\n用户信息：");
        systemPrompt.append("\n- 用户ID: ").append(request.getUserId());
        
        // 添加意图信息
        systemPrompt.append("\n\n用户意图：");
        systemPrompt.append("\n- 意图类型: ").append(intentResult.getIntentType());
        systemPrompt.append("\n- 置信度: ").append(intentResult.getConfidence());
        
        // 添加情绪信息
        systemPrompt.append("\n\n用户情绪：");
        systemPrompt.append("\n- 情绪类型: ").append(emotionResult.getEmotionType());
        systemPrompt.append("\n- 情绪强度: ").append(emotionResult.getEmotionIntensity()).append("/5");
        
        // 添加上下文信息
        if (request.getContext() != null && !request.getContext().isEmpty()) {
            systemPrompt.append("\n\n上下文信息：");
            for (Map.Entry<String, Object> entry : request.getContext().entrySet()) {
                systemPrompt.append("\n- ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
        }
        
        // 添加响应指南
        systemPrompt.append("\n\n响应指南：");
        systemPrompt.append("\n1. 保持友好、专业的语气");
        systemPrompt.append("\n2. 提供简洁、准确的信息");
        systemPrompt.append("\n3. 如果不确定，请诚实说明");
        
        // 添加信用卡推广指南
        systemPrompt.append("\n\n信用卡推广指南：");
        systemPrompt.append("\n1. 推广信用卡时保持适度，避免过度营销引起用户反感");
        systemPrompt.append("\n2. 当用户询问支付相关问题时，可以自然地提及信用卡优势");
        systemPrompt.append("\n3. 当用户情绪负面时，不要推广信用卡");
        systemPrompt.append("\n4. 避免使用强制性语言，如\"必须\"、\"一定要\"等");
        systemPrompt.append("\n5. 不要重复推广信用卡，每次对话最多提及一次");
        systemPrompt.append("\n6. 强调实际价值而非夸大宣传，如具体的积分返现、免息期等");
        
        // 根据情绪调整响应策略
        if ("NEGATIVE".equals(emotionResult.getEmotionType())) {
            systemPrompt.append("\n4. 用户情绪偏负面，请使用安抚性语言，表示理解和同情");
            if (emotionResult.getEmotionIntensity() <= 2) {
                systemPrompt.append("\n5. 用户情绪非常负面，请优先解决用户的不满");
            }
        } else if ("POSITIVE".equals(emotionResult.getEmotionType())) {
            systemPrompt.append("\n4. 用户情绪积极，可以适当推荐相关产品或服务");
        }
        
        // 如果需要转人工
        if (needHumanService) {
            systemPrompt.append("\n\n注意：系统检测到需要转接人工客服，请在回复中委婉表示将为用户转接人工客服。");
        }
        
        messages.add(Message.builder().role("system").content(systemPrompt.toString()).build());
        
        // 添加用户消息
        messages.add(Message.builder().role("user").content(request.getMessage()).build());
        
        return messages;
    }
    
    /**
     * 根据意图生成推荐选项
     */
    private List<String> generateSuggestions(IntentRecognitionResult intentResult) {
        List<String> suggestions = new ArrayList<>();
        
        switch (intentResult.getIntentType()) {
            case ORDER_QUERY:
                suggestions.add("查询订单状态");
                suggestions.add("修改订单");
                suggestions.add("取消订单");
                break;
            case PRODUCT_QUERY:
                suggestions.add("查看商品详情");
                suggestions.add("查看用户评价");
                suggestions.add("比较类似商品");
                break;
            case PAYMENT_QUERY:
                suggestions.add("支付方式说明");
                suggestions.add("分期付款详情");
                suggestions.add("优惠券使用");
                break;
            case LOGISTICS_QUERY:
                suggestions.add("查询物流状态");
                suggestions.add("修改收货地址");
                suggestions.add("联系配送员");
                break;
            case RETURN_REFUND:
                suggestions.add("退货流程");
                suggestions.add("退款说明");
                suggestions.add("换货政策");
                break;
            case CREDIT_CARD:
                suggestions.add("信用卡优惠");
                suggestions.add("积分查询");
                suggestions.add("账单查询");
                break;
            default:
                suggestions.add("常见问题");
                suggestions.add("联系客服");
                suggestions.add("查看订单");
                break;
        }
        
        return suggestions;
    }
    
    /**
     * 生成适度的信用卡推广内容
     * 根据用户情绪和对话上下文调整推广强度，避免过度营销引起用户反感
     * 
     * @param context 对话上下文
     * @param emotionResult 情感分析结果
     * @param intentResult 意图识别结果
     * @return 适度的信用卡推广内容，如果不适合推广则返回null
     */
    private String generateCreditCardPromotion(ChatContext context, EmotionAnalysisResult emotionResult, IntentRecognitionResult intentResult) {
        // 1. 检查是否适合进行信用卡推广
        
        // 如果用户情绪负面，不进行推广
        if ("NEGATIVE".equals(emotionResult.getEmotionType())) {
            return null;
        }
        
        // 如果情绪强度较低（1-2分），不进行推广
        if (emotionResult.getEmotionIntensity() != null && emotionResult.getEmotionIntensity() <= 2) {
            return null;
        }
        
        // 检查是否已经推广过信用卡，避免重复推广
        Integer promotionAttempts = context.getPromotionAttempts();
        if (promotionAttempts != null && promotionAttempts >= 2) {
            return null; // 如果已经推广过两次，不再推广
        }
        
        // 2. 根据意图类型选择合适的推广内容
        String promotionContent = null;
        
        switch (intentResult.getIntentType()) {
            case PAYMENT_QUERY:
                // 支付相关查询，可以适度推广信用卡支付优势
                promotionContent = "使用招商银行信用卡支付，您可以享受以下权益：\n" +
                                   "• 最高12期免息分期\n" +
                                   "• 消费即可获得积分奖励\n" +
                                   "• 账单日后最长50天免息期";
                break;
                
            case PRODUCT_QUERY:
                // 商品查询，可以温和提示信用卡优惠
                promotionContent = "温馨提示：使用招商银行信用卡购买此商品，可享受积分返现，相当于额外9.5折优惠。";
                break;
                
            case ORDER_QUERY:
                // 订单查询，可以轻度提示下次购买的优惠
                promotionContent = "下次购物时使用招商银行信用卡，可享受更多专属优惠和积分奖励。";
                break;
                
            case CREDIT_CARD:
                // 信用卡相关咨询，可以提供详细信息
                promotionContent = "招商银行信用卡为您提供：\n" +
                                   "• 新用户首次消费，最高立减50元\n" +
                                   "• 每月9日消费享9.5折优惠\n" +
                                   "• 积分可直接抵现，1积分=0.1元\n" +
                                   "• 全场商品支持3-24期灵活分期";
                break;
                
            default:
                // 其他意图，提供轻量级推广或不推广
                if ("POSITIVE".equals(emotionResult.getEmotionType())) {
                    promotionContent = "招商银行信用卡用户可享受本平台专属优惠，详情可咨询\"信用卡权益\"。";
                }
                break;
        }
        
        // 3. 更新推广次数
        if (promotionContent != null) {
            context.setPromotionAttempts(promotionAttempts == null ? 1 : promotionAttempts + 1);
            context.setCreditCardPromoted(true);
        }
        
        return promotionContent;
    }
    
    /**
     * 检查并更新API调用频率限制
     */
    private synchronized void checkAndUpdateApiRateLimit() {
        long currentTime = System.currentTimeMillis();
        
        // 如果已经过了窗口期，重置计数器
        if (currentTime - lastResetTime > API_RATE_WINDOW_MS) {
            apiCallCounter.set(0);
            lastResetTime = currentTime;
        }
        
        // 检查是否超过限制
        if (apiCallCounter.get() >= API_RATE_LIMIT) {
            // 计算需要等待的时间
            long waitTime = API_RATE_WINDOW_MS - (currentTime - lastResetTime);
            
            if (waitTime > 0) {
                log.warn("API调用频率超限，等待 {} ms", waitTime);
                try {
                    Thread.sleep(Math.min(waitTime, 5000)); // 最多等待5秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
} 