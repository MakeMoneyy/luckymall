package com.example.smartcustomerservice.util;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.Arrays;

/**
 * 通义千问API调用示例
 * 用于测试API连接是否正常
 */
public class DashScopeApiExample {

    /**
     * 使用Message方式调用通义千问API
     */
    public static GenerationResult callWithMessage(String apiKey) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        
        // 创建系统消息
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个专业的客服助手，擅长解答用户问题并提供帮助。")
                .build();
        
        // 创建用户消息
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你是谁？")
                .build();
        
        // 构建请求参数
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-turbo")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        
        // 调用API
        return gen.call(param);
    }
    
    /**
     * 主方法，用于直接测试API连接
     */
    public static void main(String[] args) {
        try {
            // 从环境变量获取API Key
            String apiKey = System.getenv("DASHSCOPE_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("请设置环境变量DASHSCOPE_API_KEY");
                System.exit(1);
            }
            
            // 调用API
            GenerationResult result = callWithMessage(apiKey);
            
            // 打印结果
            System.out.println("API调用成功！");
            System.out.println("回复内容: " + result.getOutput().getChoices().get(0).getMessage().getContent());
            
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.err.println("错误信息：" + e.getMessage());
            System.out.println("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
        }
        
        System.exit(0);
    }
} 