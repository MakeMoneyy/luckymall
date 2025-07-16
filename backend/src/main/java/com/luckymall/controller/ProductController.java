package com.luckymall.controller;

import com.luckymall.entity.Product;
import com.luckymall.service.ProductService;
import com.luckymall.common.Result;
import com.luckymall.common.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping
    public Result<PageResult<Product>> getProducts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy) {
        
        try {
            PageResult<Product> result = productService.getProducts(current, size, categoryId, minPrice, maxPrice, sortBy);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return Result.success(product);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取商品详情失败 - ID: {}", id, e);
            return Result.error("获取商品详情失败: " + e.getMessage());
        }
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<PageResult<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Long categoryId) {
        
        try {
            PageResult<Product> result = productService.searchProducts(keyword, current, size, sortBy, categoryId);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("搜索商品失败 - 关键词: {}", keyword, e);
            return Result.error("搜索商品失败: " + e.getMessage());
        }
    }
} 