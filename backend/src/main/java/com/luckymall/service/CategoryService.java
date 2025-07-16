package com.luckymall.service;

import com.luckymall.entity.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 获取所有分类列表
     * 
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 根据父分类ID获取子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getCategoriesByParentId(Long parentId);
    
    /**
     * 根据ID获取分类详情
     * 
     * @param id 分类ID
     * @return 分类详情
     */
    Category getCategoryById(Long id);
} 