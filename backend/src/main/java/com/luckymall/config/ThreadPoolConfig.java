package com.luckymall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置类
 */
@Configuration
public class ThreadPoolConfig {
    
    /**
     * 延迟执行线程池
     * 用于缓存延迟双删等异步操作
     */
    @Bean
    public ScheduledThreadPoolExecutor delayExecutor() {
        return new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            new NamedThreadFactory("delay-executor")
        );
    }
    
    /**
     * 自定义线程工厂
     */
    static class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
} 