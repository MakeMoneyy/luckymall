package com.luckymall.mapper;

import com.luckymall.entity.CustomerServiceChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerServiceChatMapper {
    
    void insert(CustomerServiceChat chat);
    
    List<CustomerServiceChat> findByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);
    
    List<CustomerServiceChat> findByUserId(Long userId);
} 