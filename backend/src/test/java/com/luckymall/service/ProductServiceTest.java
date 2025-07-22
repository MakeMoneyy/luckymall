package com.luckymall.service;

import com.luckymall.entity.Product;
import com.luckymall.common.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务测试类
 */
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetAllProducts() {
        // 测试获取所有商品列表
        PageResult<Product> result = productService.getProducts(1, 10, null, null, null, null);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 0);
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
    }

    @Test
    public void testGetProductById() {
        // 获取第一页商品列表
        PageResult<Product> pageResult = productService.getProducts(1, 1, null, null, null, null);
        
        // 确保有商品数据
        if (!pageResult.getRecords().isEmpty()) {
            Long productId = pageResult.getRecords().get(0).getId();
            
            // 测试根据ID获取商品详情
            Product product = productService.getProductById(productId);
            
            assertNotNull(product);
            assertEquals(productId, product.getId());
            assertNotNull(product.getName());
            assertNotNull(product.getPrice());
        } else {
            // 如果没有商品数据，跳过测试
            System.out.println("数据库中没有商品数据，跳过testGetProductById测试");
        }
    }

    @Test
    public void testSearchProducts() {
        // 测试商品搜索功能
        PageResult<Product> result = productService.searchProducts("手机", 1, 10, null, null);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        
        // 验证搜索结果包含关键词
        if (!result.getRecords().isEmpty()) {
            boolean containsKeyword = result.getRecords().stream()
                .anyMatch(p -> 
                    (p.getName() != null && p.getName().contains("手机")) || 
                    (p.getDescription() != null && p.getDescription().contains("手机")) ||
                    (p.getCategoryName() != null && p.getCategoryName().contains("手机"))
                );
            assertTrue(containsKeyword, "搜索结果应该包含关键词'手机'");
        } else {
            // 如果没有搜索结果，跳过断言
            System.out.println("搜索'手机'没有返回任何结果，跳过关键词匹配检查");
        }
    }

    @Test
    public void testGetProductsByCategory() {
        // 获取第一个分类的ID
        PageResult<Product> pageResult = productService.getProducts(1, 1, null, null, null, null);
        
        // 确保有商品数据
        if (!pageResult.getRecords().isEmpty() && pageResult.getRecords().get(0).getCategoryId() != null) {
            Long categoryId = pageResult.getRecords().get(0).getCategoryId();
            
            // 测试按分类获取商品
            PageResult<Product> result = productService.getProducts(1, 10, categoryId, null, null, null);
            
            assertNotNull(result);
            if (!result.getRecords().isEmpty()) {
                // 验证所有商品都属于指定分类
                boolean allSameCategory = result.getRecords().stream()
                    .allMatch(p -> categoryId.equals(p.getCategoryId()));
                assertTrue(allSameCategory);
            }
        } else {
            // 如果没有商品数据或分类ID，跳过测试
            System.out.println("数据库中没有带分类ID的商品数据，跳过testGetProductsByCategory测试");
        }
    }

    @Test
    public void testGetProductsWithPriceRange() {
        // 设置价格区间
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("10000.00");
        
        // 测试价格区间筛选
        PageResult<Product> result = productService.getProducts(1, 10, null, minPrice, maxPrice, null);
        
        assertNotNull(result);
        if (!result.getRecords().isEmpty()) {
            // 验证所有商品价格在指定区间内
            boolean priceInRange = result.getRecords().stream()
                .allMatch(p -> 
                    p.getPrice().compareTo(minPrice) >= 0 && 
                    p.getPrice().compareTo(maxPrice) <= 0);
            assertTrue(priceInRange);
        }
    }

    @Test
    public void testSortProductsByPrice() {
        // 测试按价格升序排序
        PageResult<Product> result = productService.getProducts(1, 10, null, null, null, "price_asc");
        
        assertNotNull(result);
        if (result.getRecords().size() > 1) {
            // 验证价格升序排列
            for (int i = 0; i < result.getRecords().size() - 1; i++) {
                BigDecimal currentPrice = result.getRecords().get(i).getPrice();
                BigDecimal nextPrice = result.getRecords().get(i + 1).getPrice();
                assertTrue(currentPrice.compareTo(nextPrice) <= 0);
            }
        }
    }

    @Test
    public void testSortProductsBySales() {
        // 测试按销量降序排序
        PageResult<Product> result = productService.getProducts(1, 10, null, null, null, "sales_desc");
        
        assertNotNull(result);
        if (result.getRecords().size() > 1) {
            // 验证销量降序排列
            for (int i = 0; i < result.getRecords().size() - 1; i++) {
                Integer currentSales = result.getRecords().get(i).getSalesCount();
                Integer nextSales = result.getRecords().get(i + 1).getSalesCount();
                assertTrue(currentSales >= nextSales);
            }
        }
    }
} 