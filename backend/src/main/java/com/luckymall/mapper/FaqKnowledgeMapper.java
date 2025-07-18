package com.luckymall.mapper;

import com.luckymall.entity.FaqKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FaqKnowledgeMapper {
    
    List<FaqKnowledge> findByKeyword(@Param("keyword") String keyword);
    
    List<FaqKnowledge> findByCategory(@Param("category") String category);
    
    void incrementHitCount(Long id);
} 