package com.luckymall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 布隆过滤器服务
 * 用于防止缓存穿透
 */
@Slf4j
@Service
public class BloomFilterService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 使用ConcurrentHashMap模拟简单布隆过滤器
    // 生产环境建议使用Redis的布隆过滤器
    private final ConcurrentHashMap<String, Boolean> existenceMap = new ConcurrentHashMap<>();
    
    // 存储记录前缀
    private static final String BLOOM_KEY_PREFIX = "bloom:filter:";
    
    /**
     * 添加键到过滤器
     * @param prefix 键前缀
     * @param key 键
     */
    public void add(String prefix, String key) {
        String fullKey = buildKey(prefix, key);
        existenceMap.put(fullKey, Boolean.TRUE);
        log.debug("添加键到布隆过滤器: {}", fullKey);
    }
    
    /**
     * 批量添加键到过滤器
     * @param prefix 键前缀
     * @param keys 键列表
     */
    public void addAll(String prefix, Collection<String> keys) {
        for (String key : keys) {
            add(prefix, key);
        }
        log.debug("批量添加键到布隆过滤器: {}条记录，前缀={}", keys.size(), prefix);
    }
    
    /**
     * 检查键是否可能存在
     * @param prefix 键前缀
     * @param key 键
     * @return 是否可能存在
     */
    public boolean mightExist(String prefix, String key) {
        String fullKey = buildKey(prefix, key);
        return existenceMap.containsKey(fullKey);
    }
    
    /**
     * 初始化过滤器
     * @param prefix 键前缀
     * @param loader 数据加载器
     */
    public void initialize(String prefix, Supplier<List<String>> loader) {
        List<String> keys = loader.get();
        addAll(prefix, keys);
        log.info("布隆过滤器初始化完成: 前缀={}, 记录数={}", prefix, keys.size());
    }
    
    /**
     * 构建完整键名
     * @param prefix 键前缀
     * @param key 键
     * @return 完整键名
     */
    private String buildKey(String prefix, String key) {
        return BLOOM_KEY_PREFIX + prefix + ":" + key;
    }
    
    /**
     * 清空特定前缀的过滤器
     * @param prefix 键前缀
     */
    public void clear(String prefix) {
        String prefixPattern = BLOOM_KEY_PREFIX + prefix + ":";
        existenceMap.keySet().removeIf(key -> key.startsWith(prefixPattern));
        log.info("清空布隆过滤器: 前缀={}", prefix);
    }
} 