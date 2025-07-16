package com.luckymall.controller;

import com.luckymall.entity.Category;
import com.luckymall.service.CategoryService;
import com.luckymall.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据父分类ID获取子分类
     */
    @GetMapping("/parent/{parentId}")
    public Result<List<Category>> getCategoriesByParentId(@PathVariable Long parentId) {
        try {
            List<Category> categories = categoryService.getCategoriesByParentId(parentId);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取子分类失败 - 父分类ID: {}", parentId, e);
            return Result.error("获取子分类失败: " + e.getMessage());
        }
    }

    /**
     * 获取顶级分类（父分类ID为null的分类）
     */
    @GetMapping("/top")
    public Result<List<Category>> getTopCategories() {
        try {
            List<Category> categories = categoryService.getCategoriesByParentId(null);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取顶级分类失败", e);
            return Result.error("获取顶级分类失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return Result.success(category);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取分类详情失败 - ID: {}", id, e);
            return Result.error("获取分类详情失败: " + e.getMessage());
        }
    }
} 