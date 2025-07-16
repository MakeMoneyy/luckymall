package com.luckymall.service;

import com.luckymall.entity.Product;
import com.luckymall.common.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务测试类
 */
@SpringBootTest
@SpringJUnitConfig
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
        // 测试根据ID获取商品详情
        Product product = productService.getProductById(1L);
        
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertNotNull(product.getName());
        assertNotNull(product.getPrice());
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
                .anyMatch(product -> product.getName().contains("手机"));
            assertTrue(containsKeyword);
        }
    }

    @Test
    public void testGetProductsByCategory() {
        // 测试按分类获取商品
        PageResult<Product> result = productService.getProducts(1, 10, 6L, null, null, null);
        
        assertNotNull(result);
        if (!result.getRecords().isEmpty()) {
            // 验证所有商品都属于指定分类
            boolean allSameCategory = result.getRecords().stream()
                .allMatch(product -> product.getCategoryId().equals(6L));
            assertTrue(allSameCategory);
        }
    }

    @Test
    public void testGetProductsWithPriceRange() {
        // 测试价格区间筛选
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("1000.00");
        
        PageResult<Product> result = productService.getProducts(1, 10, null, minPrice, maxPrice, null);
        
        assertNotNull(result);
        if (!result.getRecords().isEmpty()) {
            // 验证所有商品价格在指定区间内
            boolean priceInRange = result.getRecords().stream()
                .allMatch(product -> 
                    product.getPrice().compareTo(minPrice) >= 0 && 
                    product.getPrice().compareTo(maxPrice) <= 0);
            assertTrue(priceInRange);
        }
    }

    @Test
    public void testSortProductsByPrice() {
        // 测试按价格排序
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
        // 测试按销量排序
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