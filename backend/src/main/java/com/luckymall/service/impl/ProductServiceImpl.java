package com.luckymall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luckymall.entity.Product;
import com.luckymall.mapper.ProductMapper;
import com.luckymall.service.ProductService;
import com.luckymall.common.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现类
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CacheProtectionService cacheProtectionService;
    
    @Autowired
    private BloomFilterService bloomFilterService;
    
    @Autowired
    private RedisLockService lockService;
    
    @Autowired
    private CacheConsistencyService cacheConsistencyService;
    
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String PRODUCT_STOCK_CACHE_PREFIX = "product:stock:";
    private static final long PRODUCT_CACHE_HOURS = 24; // 商品缓存24小时
    private static final Random random = new Random();

    /**
     * 初始化商品布隆过滤器
     */
    @PostConstruct
    public void initializeBloomFilter() {
        try {
            log.info("开始初始化商品布隆过滤器...");
            
            // 清除所有商品缓存，避免序列化问题
            cacheProtectionService.clearCacheByPrefix(PRODUCT_CACHE_PREFIX);
            log.info("已清除所有商品缓存");
            
            bloomFilterService.initialize("product", () -> productMapper.selectAllProductIds());
            log.info("商品布隆过滤器初始化完成");
        } catch (Exception e) {
            log.error("初始化商品布隆过滤器失败", e);
        }
    }

    @Override
    public PageResult<Product> getProducts(Integer current, Integer size, Long categoryId, 
                                          BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        log.debug("获取商品列表 - 页码: {}, 大小: {}, 分类: {}, 价格区间: [{}, {}], 排序: {}", 
                 current, size, categoryId, minPrice, maxPrice, sortBy);
        
        // 分页查询不做缓存，直接查数据库
        PageHelper.startPage(current, size);
        
        // 查询商品列表
        List<Product> products = productMapper.selectProducts(categoryId, minPrice, maxPrice, sortBy);
        
        // 获取分页信息
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        
        return PageResult.of(products, pageInfo.getTotal(), current, size);
    }

    @Override
    public Product getProductById(Long id) {
        log.debug("根据ID获取商品详情 - ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 使用布隆过滤器快速判断商品ID是否存在
        if (!bloomFilterService.mightExist("product", id.toString())) {
            log.debug("商品ID:{}在布隆过滤器中不存在，直接返回null", id);
            return null;
        }
        
        // 构建缓存键
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        
        // 使用缓存防护获取商品
        return cacheProtectionService.getWithMutex(
            cacheKey,
            key -> {
                Product product = productMapper.selectById(id);
                if (product == null) {
                    // 商品不存在但通过了布隆过滤器，说明布隆过滤器有误判
                    // 这是正常的，布隆过滤器只能保证不存在的一定不存在，存在的不一定存在
                    log.debug("商品ID:{}布隆过滤器误判", id);
                    return null;
                }
                return product;
            },
            PRODUCT_CACHE_HOURS + random.nextInt(60), // 添加随机过期时间，避免缓存雪崩
            TimeUnit.MINUTES
        );
    }

    @Override
    public PageResult<Product> searchProducts(String keyword, Integer current, Integer size, 
                                             String sortBy, Long categoryId) {
        log.debug("搜索商品 - 关键词: {}, 页码: {}, 大小: {}, 排序: {}, 分类: {}", 
                 keyword, current, size, sortBy, categoryId);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 搜索功能直接查数据库
        PageHelper.startPage(current, size);
        
        List<Product> products = productMapper.searchProducts(keyword.trim(), categoryId, sortBy);
        
        // 获取分页信息
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        
        return PageResult.of(products, pageInfo.getTotal(), current, size);
    }
    
    /**
     * 更新商品信息（添加缓存一致性处理）
     * @param product 商品信息
     * @return 是否更新成功
     */
    public boolean updateProduct(Product product) {
        log.debug("更新商品信息 - ID: {}", product.getId());
        
        if (product.getId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 缓存键
        String cacheKey = PRODUCT_CACHE_PREFIX + product.getId();
        
        // 使用延迟双删策略保证缓存一致性
        return cacheConsistencyService.updateWithCacheAsideWithResult(cacheKey, () -> {
            int rows = productMapper.updateById(product);
            return rows > 0;
        });
    }
    
    /**
     * 使用分布式锁安全扣减库存
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 是否扣减成功
     */
    public boolean decreaseStock(Long productId, int quantity) {
        log.debug("扣减商品库存 - 商品ID: {}, 数量: {}", productId, quantity);
        
        if (productId == null || quantity <= 0) {
            throw new IllegalArgumentException("商品ID不能为空且扣减数量必须大于0");
        }
        
        // 获取锁
        String lockKey = "stock:lock:" + productId;
        
        return lockService.executeWithLock(lockKey, () -> {
            // 带检查的扣减库存
            int rows = productMapper.decreaseStockWithCheck(productId, quantity);
            boolean success = rows > 0;
            
            if (success) {
                // 删除商品缓存
                String productCacheKey = PRODUCT_CACHE_PREFIX + productId;
                cacheProtectionService.deleteCache(productCacheKey);
                log.debug("商品库存扣减成功，删除商品缓存 - 商品ID: {}", productId);
            } else {
                log.warn("商品库存扣减失败，库存不足 - 商品ID: {}, 需要数量: {}", productId, quantity);
            }
            
            return success;
        });
    }
    
    /**
     * 增加商品库存
     * @param productId 商品ID
     * @param quantity 增加数量
     * @return 是否增加成功
     */
    public boolean increaseStock(Long productId, int quantity) {
        log.debug("增加商品库存 - 商品ID: {}, 数量: {}", productId, quantity);
        
        if (productId == null || quantity <= 0) {
            throw new IllegalArgumentException("商品ID不能为空且增加数量必须大于0");
        }
        
        // 获取锁
        String lockKey = "stock:lock:" + productId;
        
        return lockService.executeWithLock(lockKey, () -> {
            // 增加库存
            int rows = productMapper.increaseStockQuantity(productId, quantity);
            boolean success = rows > 0;
            
            if (success) {
                // 删除商品缓存
                String productCacheKey = PRODUCT_CACHE_PREFIX + productId;
                cacheProtectionService.deleteCache(productCacheKey);
                log.debug("商品库存增加成功，删除商品缓存 - 商品ID: {}", productId);
            }
            
            return success;
        });
    }
} 