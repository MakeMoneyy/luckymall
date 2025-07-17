package com.luckymall.mapper;

import com.luckymall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品数据访问层接口
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 根据ID查询商品
     * 
     * @param id 商品ID
     * @return 商品信息
     */
    Product selectById(@Param("id") Long id);
    
    /**
     * 分页查询商品列表
     * 
     * @param categoryId 分类ID（可选）
     * @param minPrice 最小价格（可选）
     * @param maxPrice 最大价格（可选）
     * @param sortBy 排序方式（可选）
     * @return 商品列表
     */
    List<Product> selectProducts(@Param("categoryId") Long categoryId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                @Param("sortBy") String sortBy);
    
    /**
     * 统计商品总数
     * 
     * @param categoryId 分类ID（可选）
     * @param minPrice 最小价格（可选）
     * @param maxPrice 最大价格（可选）
     * @return 商品总数
     */
    Long countProducts(@Param("categoryId") Long categoryId,
                      @Param("minPrice") BigDecimal minPrice,
                      @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * 搜索商品
     * 
     * @param keyword 搜索关键词
     * @param categoryId 分类ID（可选）
     * @param sortBy 排序方式（可选）
     * @return 商品列表
     */
    List<Product> searchProducts(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                @Param("sortBy") String sortBy);
    
    /**
     * 统计搜索商品总数
     * 
     * @param keyword 搜索关键词
     * @param categoryId 分类ID（可选）
     * @return 商品总数
     */
    Long countSearchProducts(@Param("keyword") String keyword,
                            @Param("categoryId") Long categoryId);
    
    /**
     * 更新商品库存
     * 
     * @param productId 商品ID
     * @param stockQuantity 新的库存数量
     * @return 更新的行数
     */
    int updateStockQuantity(@Param("productId") Long productId, @Param("stockQuantity") Integer stockQuantity);
    
    /**
     * 扣减商品库存
     * 
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 更新的行数
     */
    int decreaseStockQuantity(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品库存（用于订单取消等场景）
     * 
     * @param productId 商品ID
     * @param quantity 增加数量
     * @return 更新的行数
     */
    int increaseStockQuantity(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 更新商品销量
     * 
     * @param productId 商品ID
     * @param salesCount 增加的销量
     * @return 更新的行数
     */
    int increaseSalesCount(@Param("productId") Long productId, @Param("salesCount") Integer salesCount);
    
    /**
     * 减少商品销量（用于订单取消等场景）
     * 
     * @param productId 商品ID
     * @param salesCount 减少的销量
     * @return 更新的行数
     */
    int decreaseSalesCount(@Param("productId") Long productId, @Param("salesCount") Integer salesCount);
} 