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
     * 分类描述
     */
    private String description;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 