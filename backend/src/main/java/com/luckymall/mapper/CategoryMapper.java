package com.luckymall.mapper;

import com.luckymall.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类数据访问层接口
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 查询所有分类
     * 
     * @return 分类列表
     */
    List<Category> selectAll();
    
    /**
     * 根据父分类ID查询子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据ID查询分类
     * 
     * @param id 分类ID
     * @return 分类信息
     */
    Category selectById(@Param("id") Long id);
} 