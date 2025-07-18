package com.luckymall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 通义千问API配置类
 */
@Configuration
@ConfigurationProperties(prefix = "dashscope")
@Data
@Component
public class DashScopeConfig {
    
    private Api api = new Api();
    
    @Data
    public static class Api {
        /**
         * API密钥
         */
        private String key;
        
        /**
         * 模型名称
         */
        private String model = "qwen-plus";
        
        /**
         * 温度参数，控制生成文本的随机性
         */
        private Double temperature = 0.7;
        
        /**
         * Top P参数，控制生成文本的多样性
         */
        private Double topP = 0.9;
        
        /**
         * 最大生成token数
         */
        private Integer maxTokens = 2048;
    }
} 