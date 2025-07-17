package com.luckymall.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
public class Category {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 分类级别
     */
    private Integer level;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 状态 1:启用 0:禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 