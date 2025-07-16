package com.luckymall.service;

import com.luckymall.entity.Product;
import com.luckymall.common.PageResult;

import java.math.BigDecimal;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 分页获取商品列表
     * 
     * @param current 当前页码
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @param minPrice 最小价格（可选）
     * @param maxPrice 最大价格（可选）
     * @param sortBy 排序方式（可选）
     * @return 分页商品列表
     */
    PageResult<Product> getProducts(Integer current, Integer size, Long categoryId, 
                                   BigDecimal minPrice, BigDecimal maxPrice, String sortBy);
    
    /**
     * 根据ID获取商品详情
     * 
     * @param id 商品ID
     * @return 商品详情
     */
    Product getProductById(Long id);
    
    /**
     * 搜索商品
     * 
     * @param keyword 搜索关键词
     * @param current 当前页码
     * @param size 每页大小
     * @param sortBy 排序方式（可选）
     * @param categoryId 分类ID（可选）
     * @return 搜索结果
     */
    PageResult<Product> searchProducts(String keyword, Integer current, Integer size, 
                                      String sortBy, Long categoryId);
} 