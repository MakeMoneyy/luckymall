package com.luckymall.service.impl;

import com.luckymall.config.DashScopeConfig;
import com.luckymall.dto.ChatContext;
import com.luckymall.dto.EmotionAnalysisResult;
import com.luckymall.service.EmotionAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 情感分析服务实现类
 */
@Service
@Slf4j
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    @Autowired
    private DashScopeConfig dashScopeConfig;
    
    // 强烈负面情绪关键词
    private static final String[] STRONG_NEGATIVE_KEYWORDS = {
        "气死", "愤怒", "恼火", "烦死", "滚蛋", "混蛋", "垃圾", "废物", "投诉", "举报",
        "差劲", "太差", "气愤", "生气", "恶心", "讨厌", "可恶", "可恨", "骗子", "欺骗",
        "退款", "退货", "辞职", "炒鱿鱼", "炒掉", "投诉", "举报", "骂人", "脏话"
    };
    
    // 负面情绪关键词
    private static final String[] NEGATIVE_KEYWORDS = {
        "不满", "不爽", "不高兴", "不开心", "不满意", "不好", "不行", "不可以", "不能接受", "不能忍受",
        "难受", "难过", "失望", "沮丧", "郁闷", "烦躁", "焦虑", "担心", "忧虑", "害怕",
        "不喜欢", "不想要", "不需要", "不合适", "不合格", "不符合", "不达标", "不给力"
    };
    
    // 中性情绪关键词
    private static final String[] NEUTRAL_KEYWORDS = {
        "一般", "还行", "凑合", "将就", "马马虎虎", "差不多", "可以", "尚可", "勉强", "将就",
        "不确定", "不清楚", "不知道", "不明白", "不懂", "不了解", "不记得", "不确定", "可能", "也许"
    };
    
    // 正面情绪关键词
    private static final String[] POSITIVE_KEYWORDS = {
        "满意", "喜欢", "不错", "好", "优秀", "棒", "赞", "给力", "满足", "开心",
        "高兴", "愉快", "快乐", "舒心", "舒服", "舒适", "欣慰", "欣喜", "欣慰", "欣喜",
        "感谢", "谢谢", "感激", "感恩", "好评", "点赞", "表扬", "称赞", "夸奖", "赞美"
    };
    
    // 强烈正面情绪关键词
    private static final String[] STRONG_POSITIVE_KEYWORDS = {
        "非常满意", "特别满意", "极其满意", "十分满意", "相当满意", "超级满意", "完美", "太好了", "太棒了", "太赞了",
        "特别好", "特别棒", "特别赞", "特别给力", "特别满足", "特别开心", "特别高兴", "特别愉快", "特别快乐", "特别舒心",
        "非常感谢", "特别感谢", "万分感谢", "十分感谢", "由衷感谢", "衷心感谢", "真心感谢", "诚挚感谢", "诚心感谢", "感谢万分"
    };
    
    // 问号和感叹号模式
    private static final Pattern QUESTION_PATTERN = Pattern.compile("[?？]+");
    private static final Pattern EXCLAMATION_PATTERN = Pattern.compile("[!！]+");

    @Override
    public EmotionAnalysisResult analyzeEmotion(String message, ChatContext context) {
        // 初始化结果
        EmotionAnalysisResult result = new EmotionAnalysisResult();
        result.setTimestamp(System.currentTimeMillis());
        
        // 默认为中性情绪
        String emotionType = "NEUTRAL";
        int emotionIntensity = 3;
        List<String> emotionKeywords = new ArrayList<>();
        boolean suggestHumanService = false;
        
        // 将消息转为小写进行匹配
        String lowerMessage = message.toLowerCase();
        
        // 检查是否包含强烈负面情绪关键词
        if (containsKeywords(lowerMessage, STRONG_NEGATIVE_KEYWORDS, emotionKeywords)) {
            emotionType = "NEGATIVE";
            emotionIntensity = 1;
            suggestHumanService = true;
        }
        // 检查是否包含负面情绪关键词
        else if (containsKeywords(lowerMessage, NEGATIVE_KEYWORDS, emotionKeywords)) {
            emotionType = "NEGATIVE";
            emotionIntensity = 2;
            
            // 检查是否有感叹号，如果有多个感叹号，可能情绪更强烈
            Matcher exclamationMatcher = EXCLAMATION_PATTERN.matcher(message);
            int exclamationCount = 0;
            while (exclamationMatcher.find()) {
                exclamationCount++;
            }
            
            if (exclamationCount >= 2) {
                emotionIntensity = 1;
                suggestHumanService = true;
            }
        }
        // 检查是否包含正面情绪关键词
        else if (containsKeywords(lowerMessage, POSITIVE_KEYWORDS, emotionKeywords)) {
            emotionType = "POSITIVE";
            emotionIntensity = 4;
        }
        // 检查是否包含强烈正面情绪关键词
        else if (containsKeywords(lowerMessage, STRONG_POSITIVE_KEYWORDS, emotionKeywords)) {
            emotionType = "POSITIVE";
            emotionIntensity = 5;
        }
        // 检查是否包含中性情绪关键词
        else if (containsKeywords(lowerMessage, NEUTRAL_KEYWORDS, emotionKeywords)) {
            emotionType = "NEUTRAL";
            emotionIntensity = 3;
        }
        // 如果没有匹配到任何情绪关键词，则根据消息长度和标点符号判断
        else {
            // 检查问号数量
            Matcher questionMatcher = QUESTION_PATTERN.matcher(message);
            int questionCount = 0;
            while (questionMatcher.find()) {
                questionCount++;
            }
            
            // 检查感叹号数量
            Matcher exclamationMatcher = EXCLAMATION_PATTERN.matcher(message);
            int exclamationCount = 0;
            while (exclamationMatcher.find()) {
                exclamationCount++;
            }
            
            // 如果有多个问号或感叹号，可能表示情绪不稳定
            if (questionCount >= 3 || exclamationCount >= 2) {
                emotionType = "NEGATIVE";
                emotionIntensity = 2;
            }
            // 如果消息很长，可能是在抱怨
            else if (message.length() > 50) {
                emotionType = "NEGATIVE";
                emotionIntensity = 2;
            }
        }
        
        // 设置分析结果
        result.setEmotionType(emotionType);
        result.setEmotionIntensity(emotionIntensity);
        result.setEmotionKeywords(emotionKeywords);
        result.setSuggestHumanService(suggestHumanService);
        
        // 更新上下文
        updateContext(context, result);
        
        return result;
    }

    @Override
    public boolean shouldTransferToHuman(EmotionAnalysisResult result, ChatContext context) {
        // 如果情绪分析结果建议转人工，直接返回true
        if (result.getSuggestHumanService()) {
            return true;
        }
        
        // 如果情绪强度为1（非常负面），建议转人工
        if ("NEGATIVE".equals(result.getEmotionType()) && result.getEmotionIntensity() <= 1) {
            return true;
        }
        
        // 如果情绪趋势是恶化，并且当前情绪是负面的，建议转人工
        if ("DETERIORATING".equals(context.getEmotionTrend()) && "NEGATIVE".equals(result.getEmotionType())) {
            return true;
        }
        
        // 如果连续3次情绪都是负面的，建议转人工
        List<EmotionAnalysisResult> history = context.getEmotionHistory();
        if (history.size() >= 3) {
            int negativeCount = 0;
            for (int i = history.size() - 1; i >= Math.max(0, history.size() - 3); i--) {
                if ("NEGATIVE".equals(history.get(i).getEmotionType())) {
                    negativeCount++;
                }
            }
            
            if (negativeCount >= 3) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String trackEmotionTrend(ChatContext context, EmotionAnalysisResult currentResult) {
        List<EmotionAnalysisResult> history = context.getEmotionHistory();
        
        // 如果历史记录不足，无法判断趋势
        if (history.size() < 2) {
            return "STABLE";
        }
        
        // 获取最近的两次情绪分析结果（不包括当前结果）
        EmotionAnalysisResult previousResult = history.get(history.size() - 2);
        
        // 计算情绪变化
        int currentIntensity = currentResult.getEmotionIntensity();
        int previousIntensity = previousResult.getEmotionIntensity();
        
        // 如果情绪类型相同，比较强度
        if (currentResult.getEmotionType().equals(previousResult.getEmotionType())) {
            // 如果是负面情绪
            if ("NEGATIVE".equals(currentResult.getEmotionType())) {
                // 情绪强度数值越小，表示负面情绪越强烈
                if (currentIntensity < previousIntensity) {
                    return "DETERIORATING";
                } else if (currentIntensity > previousIntensity) {
                    return "IMPROVING";
                }
            } 
            // 如果是正面情绪
            else if ("POSITIVE".equals(currentResult.getEmotionType())) {
                // 情绪强度数值越大，表示正面情绪越强烈
                if (currentIntensity > previousIntensity) {
                    return "IMPROVING";
                } else if (currentIntensity < previousIntensity) {
                    return "DETERIORATING";
                }
            }
        } 
        // 如果情绪类型不同
        else {
            // 从负面变为正面
            if ("NEGATIVE".equals(previousResult.getEmotionType()) && "POSITIVE".equals(currentResult.getEmotionType())) {
                return "IMPROVING";
            }
            // 从正面变为负面
            else if ("POSITIVE".equals(previousResult.getEmotionType()) && "NEGATIVE".equals(currentResult.getEmotionType())) {
                return "DETERIORATING";
            }
            // 从中性变为负面
            else if ("NEUTRAL".equals(previousResult.getEmotionType()) && "NEGATIVE".equals(currentResult.getEmotionType())) {
                return "DETERIORATING";
            }
            // 从中性变为正面
            else if ("NEUTRAL".equals(previousResult.getEmotionType()) && "POSITIVE".equals(currentResult.getEmotionType())) {
                return "IMPROVING";
            }
            // 从负面变为中性
            else if ("NEGATIVE".equals(previousResult.getEmotionType()) && "NEUTRAL".equals(currentResult.getEmotionType())) {
                return "IMPROVING";
            }
            // 从正面变为中性
            else if ("POSITIVE".equals(previousResult.getEmotionType()) && "NEUTRAL".equals(currentResult.getEmotionType())) {
                return "DETERIORATING";
            }
        }
        
        // 检查整体趋势（最近3条记录）
        if (history.size() >= 3) {
            // 获取最近三次情绪记录
            List<EmotionAnalysisResult> recentHistory = history.subList(history.size() - 3, history.size());
            
            // 计算负面情绪的数量
            int negativeCount = 0;
            for (EmotionAnalysisResult result : recentHistory) {
                if ("NEGATIVE".equals(result.getEmotionType())) {
                    negativeCount++;
                }
            }
            
            // 如果负面情绪数量增加，认为是恶化趋势
            if (negativeCount >= 2 && "NEGATIVE".equals(currentResult.getEmotionType())) {
                return "DETERIORATING";
            }
            
            // 检查情绪强度变化趋势
            if ("NEGATIVE".equals(currentResult.getEmotionType()) && 
                "NEGATIVE".equals(recentHistory.get(1).getEmotionType()) && 
                currentResult.getEmotionIntensity() < recentHistory.get(1).getEmotionIntensity()) {
                return "DETERIORATING";
            }
        }
        
        return "STABLE";
    }
    
    /**
     * 检查消息是否包含关键词，并将匹配的关键词添加到列表中
     * @param message 消息
     * @param keywords 关键词数组
     * @param matchedKeywords 匹配的关键词列表
     * @return 是否包含
     */
    private boolean containsKeywords(String message, String[] keywords, List<String> matchedKeywords) {
        boolean contains = false;
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                contains = true;
                matchedKeywords.add(keyword);
            }
        }
        return contains;
    }
    
    /**
     * 更新上下文
     * @param context 上下文
     * @param result 情绪分析结果
     */
    private void updateContext(ChatContext context, EmotionAnalysisResult result) {
        // 添加当前情绪分析结果到历史记录
        context.getEmotionHistory().add(result);
        
        // 如果历史记录过长，保留最近10条
        if (context.getEmotionHistory().size() > 10) {
            context.setEmotionHistory(context.getEmotionHistory().subList(
                    context.getEmotionHistory().size() - 10, 
                    context.getEmotionHistory().size()));
        }
        
        // 更新情绪变化趋势
        String trend = trackEmotionTrend(context, result);
        context.setEmotionTrend(trend);
        
        // 更新最近一次情绪分析结果
        context.setLastEmotionResult(result);
    }
} 