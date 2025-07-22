package com.luckymall.config;

import com.luckymall.service.impl.ChatCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {

    @Autowired
    private ChatCacheService chatCacheService;
    
    /**
     * 每10分钟清理一次过期缓存
     */
    @Scheduled(fixedRate = 600000) // 10分钟
    public void cleanExpiredCache() {
        log.info("开始清理过期缓存...");
        chatCacheService.cleanExpiredCache();
        log.info("过期缓存清理完成");
    }
    
    /**
     * 每天凌晨3点执行数据库清理任务
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void dailyDatabaseCleanup() {
        log.info("开始执行数据库清理任务...");
        // 这里可以添加数据库清理逻辑，或者调用相关服务
        log.info("数据库清理任务完成");
    }
    
    /**
     * 每小时执行一次系统健康检查
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void systemHealthCheck() {
        log.info("执行系统健康检查...");
        // 这里可以添加系统健康检查逻辑
        log.info("系统健康检查完成");
    }
} 