package com.luckymall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luckymall.entity.Product;
import com.luckymall.mapper.ProductMapper;
import com.luckymall.service.ProductService;
import com.luckymall.common.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<Product> getProducts(Integer current, Integer size, Long categoryId, 
                                          BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        log.debug("获取商品列表 - 页码: {}, 大小: {}, 分类: {}, 价格区间: [{}, {}], 排序: {}", 
                 current, size, categoryId, minPrice, maxPrice, sortBy);
        
        // 设置分页参数
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
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在或已下架");
        }
        
        return product;
    }

    @Override
    public PageResult<Product> searchProducts(String keyword, Integer current, Integer size, 
                                             String sortBy, Long categoryId) {
        log.debug("搜索商品 - 关键词: {}, 页码: {}, 大小: {}, 排序: {}, 分类: {}", 
                 keyword, current, size, sortBy, categoryId);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        // 搜索商品
        List<Product> products = productMapper.searchProducts(keyword.trim(), categoryId, sortBy);
        
        // 获取分页信息
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        
        return PageResult.of(products, pageInfo.getTotal(), current, size);
    }
} 