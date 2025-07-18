package com.luckymall.service.impl;

import com.luckymall.dto.ChatRequest;
import com.luckymall.dto.ChatResponse;
import com.luckymall.dto.UserCreditCardResponse;
import com.luckymall.entity.CustomerServiceChat;
import com.luckymall.entity.FaqKnowledge;
import com.luckymall.entity.UserCreditCard;
import com.luckymall.mapper.CustomerServiceChatMapper;
import com.luckymall.mapper.FaqKnowledgeMapper;
import com.luckymall.mapper.UserCreditCardMapper;
import com.luckymall.service.CustomerServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerServiceServiceImpl implements CustomerServiceService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CustomerServiceChatMapper chatMapper;
    
    @Autowired
    private UserCreditCardMapper userCreditCardMapper;
    
    @Autowired
    private FaqKnowledgeMapper faqKnowledgeMapper;
    
    private static final String AI_CACHE_PREFIX = "ai_response:";
    private static final String USER_CARD_PREFIX = "user_card:";
    private static final String CONTEXT_PREFIX = "chat_context:";
    
    @Override
    public ChatResponse processChat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 1. 构建缓存key并检查缓存
        String cacheKey = buildCacheKey(request.getMessage(), request.getContext());
        ChatResponse cachedResponse = (ChatResponse) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedResponse != null) {
            cachedResponse.setCacheHit(true);
            cachedResponse.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
            
            // 记录对话
            saveChatRecord(request, cachedResponse, true, (int) (System.currentTimeMillis() - startTime));
            
            return cachedResponse;
        }
        
        // 2. 生成AI响应
        ChatResponse response = generateAIResponse(request);
        response.setCacheHit(false);
        response.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
        
        // 3. 缓存响应结果
        redisTemplate.opsForValue().set(cacheKey, response, 1, TimeUnit.HOURS);
        
        // 4. 记录对话
        saveChatRecord(request, response, false, response.getResponseTimeMs());
        
        return response;
    }
    
    @Override
    public UserCreditCardResponse getUserCreditCard(Long userId) {
        String key = USER_CARD_PREFIX + userId;
        
        // 先从缓存获取
        UserCreditCardResponse cachedResponse = (UserCreditCardResponse) redisTemplate.opsForValue().get(key);
        if (cachedResponse != null) {
            return cachedResponse;
        }
        
        // 缓存未命中，从数据库查询
        UserCreditCard userCard = userCreditCardMapper.findByUserId(userId);
        if (userCard == null) {
            return null;
        }
        
        UserCreditCardResponse response = new UserCreditCardResponse();
        response.setCardLevel(userCard.getCardLevel());
        response.setPointsBalance(userCard.getPointsBalance());
        response.setPointsExpiring(userCard.getPointsExpiring());
        response.setExpiringDate(userCard.getExpiringDate());
        response.setAvailableCredit(userCard.getCreditLimit());
        response.setNextBillDate(userCard.getBillDate());
        
        // 缓存结果
        redisTemplate.opsForValue().set(key, response, 10, TimeUnit.MINUTES);
        
        return response;
    }
    
    private ChatResponse generateAIResponse(ChatRequest request) {
        String message = request.getMessage().toLowerCase();
        
        // 1. 首先尝试FAQ匹配
        List<FaqKnowledge> faqs = faqKnowledgeMapper.findByKeyword(message);
        if (!faqs.isEmpty()) {
            FaqKnowledge faq = faqs.get(0);
            faqKnowledgeMapper.incrementHitCount(faq.getId());
            
            return buildFaqResponse(faq, request);
        }
        
        // 2. 基于关键词进行信用卡推广
        if (message.contains("分期") || message.contains("付款") || message.contains("支付")) {
            return buildInstallmentResponse(request);
        } else if (message.contains("积分") || message.contains("优惠")) {
            return buildPointsResponse(request);
        } else if (message.contains("退货") || message.contains("物流") || message.contains("发货")) {
            return buildShippingResponse(request);
        }
        
        // 3. 默认响应
        return buildDefaultResponse(request);
    }
    
    private ChatResponse buildFaqResponse(FaqKnowledge faq, ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setResponseId(UUID.randomUUID().toString());
        
        String fullMessage = faq.getAnswer();
        if (faq.getCreditCardPromotion() != null && !faq.getCreditCardPromotion().isEmpty()) {
            fullMessage += "\n\n" + faq.getCreditCardPromotion();
        }
        
        response.setMessage(fullMessage);
        response.setSuggestions(Arrays.asList("了解更多权益", "立即使用信用卡支付", "查看我的积分"));
        
        // 添加促销信息
        ChatResponse.PromotionInfo promotionInfo = new ChatResponse.PromotionInfo();
        promotionInfo.setType("credit_card_benefits");
        promotionInfo.setPointsEarned(calculatePoints(request.getContext()));
        promotionInfo.setInstallmentOptions(Arrays.asList(3, 6, 12, 24));
        response.setPromotionInfo(promotionInfo);
        
        return response;
    }
    
    private ChatResponse buildInstallmentResponse(ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setResponseId(UUID.randomUUID().toString());
        
        UserCreditCardResponse userCard = getUserCreditCard(request.getUserId());
        String cardLevel = userCard != null ? userCard.getCardLevel() : "GOLD";
        
        String message = String.format(
            "当然可以分期！使用您的招商银行%s信用卡，这款商品支持：\n" +
            "🎁 12期免息分期，月供仅需%.0f元\n" +
            "💰 立即获得%d积分（价值%.1f元）\n" +
            "⏰ 48天超长免息期\n" +
            "💳 享受购物保险，商品损坏全额赔付\n\n" +
            "相比其他支付方式，您将额外获得价值约50元的权益！\n" +
            "现在就用信用卡下单吧～",
            getCardLevelText(cardLevel),
            getMonthlyPayment(request.getContext()),
            calculatePoints(request.getContext()),
            calculatePoints(request.getContext()) * 0.1
        );
        
        response.setMessage(message);
        response.setSuggestions(Arrays.asList("立即使用信用卡支付", "查看分期方案", "了解更多权益"));
        
        ChatResponse.PromotionInfo promotionInfo = new ChatResponse.PromotionInfo();
        promotionInfo.setType("installment_promotion");
        promotionInfo.setPointsEarned(calculatePoints(request.getContext()));
        promotionInfo.setInstallmentOptions(Arrays.asList(3, 6, 12, 24));
        if (request.getContext() != null && request.getContext().getProductPrice() != null) {
            promotionInfo.setDiscountAmount(request.getContext().getProductPrice().multiply(new BigDecimal("0.01")));
        }
        response.setPromotionInfo(promotionInfo);
        
        return response;
    }
    
    private ChatResponse buildPointsResponse(ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setResponseId(UUID.randomUUID().toString());
        
        UserCreditCardResponse userCard = getUserCreditCard(request.getUserId());
        if (userCard != null) {
            String message = String.format(
                "您的招行积分可是很值钱的哦！\n" +
                "💎 当前积分：%d分（价值%.1f元）\n" +
                "🛍️ 可直接抵扣现金使用\n" +
                "🎁 兑换精美礼品\n" +
                "⚡ 重要提醒：%d积分将在15天后到期！\n\n" +
                "建议您：\n" +
                "1. 立即使用积分抵扣部分商品费用\n" +
                "2. 用信用卡支付剩余金额，获得新积分\n" +
                "3. 这样既用了即将到期的积分，又赚了新积分！\n\n" +
                "要我帮您计算最优搭配方案吗？",
                userCard.getPointsBalance(),
                userCard.getPointsBalance() * 0.1,
                userCard.getPointsExpiring()
            );
            response.setMessage(message);
        } else {
            response.setMessage("使用信用卡支付可以获得丰厚积分奖励！每消费1元获得1积分，积分可直接抵现金使用哦～");
        }
        
        response.setSuggestions(Arrays.asList("计算积分方案", "立即使用信用卡", "查看积分商城"));
        
        return response;
    }
    
    private ChatResponse buildShippingResponse(ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setResponseId(UUID.randomUUID().toString());
        
        response.setMessage(
            "您的订单预计明天发货哦～\n\n" +
            "💡小贴士：下次购买建议使用信用卡支付，可以享受：\n" +
            "📦 物流保险，万一包裹丢失或损坏全额赔付\n" +
            "🚚 免费退货运费险\n" +
            "⚡ 48小时快速理赔\n" +
            "完全零风险购物体验！"
        );
        
        response.setSuggestions(Arrays.asList("了解信用卡保险", "查看订单详情", "联系客服"));
        
        return response;
    }
    
    private ChatResponse buildDefaultResponse(ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setResponseId(UUID.randomUUID().toString());
        
        response.setMessage(
            "感谢您的咨询！我是您的专属购物助手。\n\n" +
            "💡小贴士：使用招商银行信用卡支付，享受更多专属权益：\n" +
            "• 每笔消费获得积分奖励\n" +
            "• 免费分期付款服务\n" +
            "• 购物保险保障\n" +
            "• 48天超长免息期\n\n" +
            "还有什么可以帮您的吗？"
        );
        
        response.setSuggestions(Arrays.asList("查看商品", "了解信用卡权益", "联系人工客服"));
        
        return response;
    }
    
    private String buildCacheKey(String message, ChatRequest.ChatContext context) {
        String contextData = "";
        if (context != null) {
            contextData = String.format("%s_%s_%s", 
                context.getProductId() != null ? context.getProductId() : "0",
                context.getProductPrice() != null ? context.getProductPrice() : "0",
                context.getCurrentPage() != null ? context.getCurrentPage() : "");
        }
        return AI_CACHE_PREFIX + DigestUtils.md5DigestAsHex((message + contextData).getBytes());
    }
    
    private void saveChatRecord(ChatRequest request, ChatResponse response, boolean cacheHit, int responseTime) {
        CustomerServiceChat chat = new CustomerServiceChat();
        chat.setUserId(request.getUserId());
        chat.setSessionId(request.getSessionId());
        chat.setUserMessage(request.getMessage());
        chat.setBotResponse(response.getMessage());
        chat.setCacheHit(cacheHit);
        chat.setResponseTimeMs(responseTime);
        chat.setCreatedTime(LocalDateTime.now());
        
        chatMapper.insert(chat);
    }
    
    private Integer calculatePoints(ChatRequest.ChatContext context) {
        if (context != null && context.getProductPrice() != null) {
            return context.getProductPrice().intValue(); // 1元1积分
        }
        return 100; // 默认积分
    }
    
    private double getMonthlyPayment(ChatRequest.ChatContext context) {
        if (context != null && context.getProductPrice() != null) {
            return context.getProductPrice().doubleValue() / 12;
        }
        return 100.0; // 默认月供
    }
    
    private String getCardLevelText(String cardLevel) {
        switch (cardLevel) {
            case "PLATINUM": return "白金卡";
            case "DIAMOND": return "钻石卡";
            default: return "金卡";
        }
    }
} 