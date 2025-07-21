package com.luckymall.service.impl;

import com.luckymall.config.DashScopeConfig;
import com.luckymall.dto.ChatContext;
import com.luckymall.dto.IntentRecognitionResult;
import com.luckymall.enums.IntentType;
import com.luckymall.service.IntentRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 意图识别服务实现类
 */
@Service
@Slf4j
public class IntentRecognitionServiceImpl implements IntentRecognitionService {

    @Autowired
    private DashScopeConfig dashScopeConfig;
    
    // 订单查询相关关键词
    private static final String[] ORDER_KEYWORDS = {"订单", "买", "购买", "下单", "购物", "我的订单", "查询订单", "查订单"};
    
    // 商品查询相关关键词
    private static final String[] PRODUCT_KEYWORDS = {"商品", "产品", "货物", "东西", "物品", "价格", "多少钱", "便宜", "贵"};
    
    // 支付查询相关关键词
    private static final String[] PAYMENT_KEYWORDS = {"支付", "付款", "付钱", "支付宝", "微信", "银行卡", "信用卡", "花呗", "分期"};
    
    // 物流查询相关关键词
    private static final String[] LOGISTICS_KEYWORDS = {"物流", "快递", "发货", "收货", "送货", "到货", "运输", "邮寄"};
    
    // 退换货相关关键词
    private static final String[] RETURN_KEYWORDS = {"退货", "换货", "退款", "返款", "退钱", "不想要了", "不满意", "质量问题"};
    
    // 信用卡相关关键词
    private static final String[] CREDIT_CARD_KEYWORDS = {"信用卡", "招行卡", "招商银行", "刷卡", "卡片", "额度", "账单", "还款"};
    
    // 积分查询相关关键词
    private static final String[] POINTS_KEYWORDS = {"积分", "点数", "兑换", "优惠", "折扣", "权益", "会员"};
    
    // 投诉相关关键词
    private static final String[] COMPLAINT_KEYWORDS = {"投诉", "不满", "差评", "糟糕", "垃圾", "太差", "失望", "举报"};
    
    // 表扬相关关键词
    private static final String[] PRAISE_KEYWORDS = {"表扬", "点赞", "好评", "满意", "优秀", "很好", "称赞", "感谢"};
    
    // 时间表达式正则
    private static final Pattern TIME_PATTERN = Pattern.compile("([昨今明后前]天|上|这|下个?)(周|月|星期|礼拜|年)?(一|二|三|四|五|六|七|日|天|\\d+)?|最近(\\d+)(天|周|月|年)|\\d{4}年?\\d{1,2}月?\\d{1,2}日?");
    
    // 数量表达式正则
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)(个|件|箱|袋|瓶|台|部|只|双|条|张|套|份|本|支|块|次)?");

    @Override
    public IntentRecognitionResult recognizeIntent(String message, ChatContext context) {
        // 初始化结果
        IntentRecognitionResult result = new IntentRecognitionResult();
        result.setOriginalQuery(message);
        result.setTimestamp(System.currentTimeMillis());
        
        // 默认为未知意图
        IntentType intentType = IntentType.UNKNOWN;
        float confidence = 0.0f;
        
        // 将消息转为小写进行匹配
        String lowerMessage = message.toLowerCase();
        
        // 检查是否包含订单查询关键词
        if (containsKeywords(lowerMessage, ORDER_KEYWORDS)) {
            intentType = IntentType.ORDER_QUERY;
            confidence = calculateConfidence(lowerMessage, ORDER_KEYWORDS);
        }
        // 检查是否包含商品查询关键词
        else if (containsKeywords(lowerMessage, PRODUCT_KEYWORDS)) {
            intentType = IntentType.PRODUCT_QUERY;
            confidence = calculateConfidence(lowerMessage, PRODUCT_KEYWORDS);
        }
        // 检查是否包含支付查询关键词
        else if (containsKeywords(lowerMessage, PAYMENT_KEYWORDS)) {
            intentType = IntentType.PAYMENT_QUERY;
            confidence = calculateConfidence(lowerMessage, PAYMENT_KEYWORDS);
        }
        // 检查是否包含物流查询关键词
        else if (containsKeywords(lowerMessage, LOGISTICS_KEYWORDS)) {
            intentType = IntentType.LOGISTICS_QUERY;
            confidence = calculateConfidence(lowerMessage, LOGISTICS_KEYWORDS);
        }
        // 检查是否包含退换货关键词
        else if (containsKeywords(lowerMessage, RETURN_KEYWORDS)) {
            intentType = IntentType.RETURN_REFUND;
            confidence = calculateConfidence(lowerMessage, RETURN_KEYWORDS);
        }
        // 检查是否包含信用卡关键词
        else if (containsKeywords(lowerMessage, CREDIT_CARD_KEYWORDS)) {
            intentType = IntentType.CREDIT_CARD;
            confidence = calculateConfidence(lowerMessage, CREDIT_CARD_KEYWORDS);
        }
        // 检查是否包含积分查询关键词
        else if (containsKeywords(lowerMessage, POINTS_KEYWORDS)) {
            intentType = IntentType.POINTS_QUERY;
            confidence = calculateConfidence(lowerMessage, POINTS_KEYWORDS);
        }
        // 检查是否包含投诉关键词
        else if (containsKeywords(lowerMessage, COMPLAINT_KEYWORDS)) {
            intentType = IntentType.COMPLAINT;
            confidence = calculateConfidence(lowerMessage, COMPLAINT_KEYWORDS);
        }
        // 检查是否包含表扬关键词
        else if (containsKeywords(lowerMessage, PRAISE_KEYWORDS)) {
            intentType = IntentType.PRAISE;
            confidence = calculateConfidence(lowerMessage, PRAISE_KEYWORDS);
        }
        
        // 设置识别结果
        result.setIntentType(intentType);
        result.setConfidence(confidence);
        
        // 提取实体
        Map<String, Object> entities = extractEntities(message, context).getExtractedEntities();
        result.setExtractedEntities(entities);
        
        // 更新上下文
        updateContext(context, result);
        
        return result;
    }

    @Override
    public IntentRecognitionResult extractEntities(String message, ChatContext context) {
        IntentRecognitionResult result = new IntentRecognitionResult();
        result.setOriginalQuery(message);
        result.setTimestamp(System.currentTimeMillis());
        
        Map<String, Object> entities = new HashMap<>();
        
        // 提取时间表达式
        Matcher timeMatcher = TIME_PATTERN.matcher(message);
        if (timeMatcher.find()) {
            String timeExpression = timeMatcher.group();
            entities.put("time_expression", timeExpression);
            
            // 解析时间表达式为具体日期范围
            Map<String, Object> timeRange = parseTimeExpression(timeExpression);
            if (timeRange != null) {
                entities.putAll(timeRange);
            }
        }
        
        // 提取数量表达式
        Matcher quantityMatcher = QUANTITY_PATTERN.matcher(message);
        if (quantityMatcher.find()) {
            String quantityExpression = quantityMatcher.group();
            String quantity = quantityMatcher.group(1);
            String unit = quantityMatcher.groupCount() > 1 ? quantityMatcher.group(2) : "";
            
            entities.put("quantity_expression", quantityExpression);
            entities.put("quantity", Integer.parseInt(quantity));
            if (unit != null && !unit.isEmpty()) {
                entities.put("unit", unit);
            }
        }
        
        // 设置提取的实体
        result.setExtractedEntities(entities);
        
        return result;
    }
    
    /**
     * 检查消息是否包含关键词
     * @param message 消息
     * @param keywords 关键词数组
     * @return 是否包含
     */
    private boolean containsKeywords(String message, String[] keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 计算置信度
     * @param message 消息
     * @param keywords 关键词数组
     * @return 置信度
     */
    private float calculateConfidence(String message, String[] keywords) {
        int matchCount = 0;
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                matchCount++;
            }
        }
        
        // 基础置信度为0.6，每匹配一个关键词增加0.1，最高为0.9
        return Math.min(0.6f + matchCount * 0.1f, 0.9f);
    }
    
    /**
     * 解析时间表达式
     * @param timeExpression 时间表达式
     * @return 时间范围
     */
    private Map<String, Object> parseTimeExpression(String timeExpression) {
        Map<String, Object> timeRange = new HashMap<>();
        LocalDate startDate = null;
        LocalDate endDate = null;
        
        // 处理"最近X天/周/月/年"
        Pattern recentPattern = Pattern.compile("最近(\\d+)(天|周|月|年)");
        Matcher recentMatcher = recentPattern.matcher(timeExpression);
        if (recentMatcher.find()) {
            int number = Integer.parseInt(recentMatcher.group(1));
            String unit = recentMatcher.group(2);
            
            endDate = LocalDate.now();
            
            switch (unit) {
                case "天":
                    startDate = endDate.minusDays(number);
                    break;
                case "周":
                    startDate = endDate.minusWeeks(number);
                    break;
                case "月":
                    startDate = endDate.minusMonths(number);
                    break;
                case "年":
                    startDate = endDate.minusYears(number);
                    break;
            }
        }
        // 处理"昨天"、"今天"、"明天"等
        else if (timeExpression.contains("昨天")) {
            startDate = LocalDate.now().minusDays(1);
            endDate = startDate;
        } else if (timeExpression.contains("今天")) {
            startDate = LocalDate.now();
            endDate = startDate;
        } else if (timeExpression.contains("明天")) {
            startDate = LocalDate.now().plusDays(1);
            endDate = startDate;
        } else if (timeExpression.contains("后天")) {
            startDate = LocalDate.now().plusDays(2);
            endDate = startDate;
        } else if (timeExpression.contains("前天")) {
            startDate = LocalDate.now().minusDays(2);
            endDate = startDate;
        }
        // 处理"上周"、"本周"、"下周"等
        else if (timeExpression.contains("上周") || timeExpression.contains("上个星期") || timeExpression.contains("上个礼拜")) {
            startDate = LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
            endDate = startDate.plusDays(6);
        } else if (timeExpression.contains("这周") || timeExpression.contains("本周") || timeExpression.contains("这个星期") || timeExpression.contains("这个礼拜")) {
            startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            endDate = startDate.plusDays(6);
        } else if (timeExpression.contains("下周") || timeExpression.contains("下个星期") || timeExpression.contains("下个礼拜")) {
            startDate = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY);
            endDate = startDate.plusDays(6);
        }
        // 处理"上月"、"本月"、"下月"等
        else if (timeExpression.contains("上月") || timeExpression.contains("上个月")) {
            startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            endDate = startDate.plusMonths(1).minusDays(1);
        } else if (timeExpression.contains("这月") || timeExpression.contains("本月") || timeExpression.contains("这个月")) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = startDate.plusMonths(1).minusDays(1);
        } else if (timeExpression.contains("下月") || timeExpression.contains("下个月")) {
            startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
            endDate = startDate.plusMonths(1).minusDays(1);
        }
        
        // 如果成功解析了时间范围
        if (startDate != null && endDate != null) {
            timeRange.put("start_date", startDate.format(DateTimeFormatter.ISO_DATE));
            timeRange.put("end_date", endDate.format(DateTimeFormatter.ISO_DATE));
            
            // 计算天数
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            timeRange.put("days", days);
            
            return timeRange;
        }
        
        return null;
    }
    
    /**
     * 更新上下文
     * @param context 上下文
     * @param result 识别结果
     */
    private void updateContext(ChatContext context, IntentRecognitionResult result) {
        // 更新当前对话主题
        if (result.getIntentType() != IntentType.UNKNOWN && result.getConfidence() > 0.7f) {
            context.setCurrentTopic(result.getIntentType().getCode());
        }
        
        // 更新已识别的实体信息
        context.getRecognizedEntities().putAll(result.getExtractedEntities());
        
        // 更新上一次查询对象
        if (result.getIntentType() == IntentType.ORDER_QUERY) {
            context.setLastQueryObject("order");
        } else if (result.getIntentType() == IntentType.PRODUCT_QUERY) {
            context.setLastQueryObject("product");
        } else if (result.getIntentType() == IntentType.PAYMENT_QUERY) {
            context.setLastQueryObject("payment");
        } else if (result.getIntentType() == IntentType.LOGISTICS_QUERY) {
            context.setLastQueryObject("logistics");
        }
    }
} 