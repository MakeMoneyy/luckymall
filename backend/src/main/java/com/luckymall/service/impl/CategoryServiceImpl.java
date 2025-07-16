package com.luckymall.service.impl;

import com.luckymall.entity.Category;
import com.luckymall.mapper.CategoryMapper;
import com.luckymall.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        log.debug("获取所有分类列表");
        return categoryMapper.selectAll();
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        log.debug("根据父分类ID获取子分类 - 父分类ID: {}", parentId);
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public Category getCategoryById(Long id) {
        log.debug("根据ID获取分类详情 - ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        return category;
    }
} 