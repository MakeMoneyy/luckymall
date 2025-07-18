package com.example.smartcustomerservice.mapper;

import com.example.smartcustomerservice.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper {
    
    /**
     * 保存聊天消息
     * @param chatMessage 聊天消息
     * @return 影响行数
     */
    @Insert("INSERT INTO chat_message(session_id, user_id, content, role, cache_hit, response_time_ms) " +
            "VALUES(#{sessionId}, #{userId}, #{content}, #{role}, #{cacheHit}, #{responseTimeMs})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage chatMessage);
    
    /**
     * 根据会话ID查询聊天历史
     * @param sessionId 会话ID
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatMessage> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据用户ID查询聊天历史
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_message WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<ChatMessage> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 统计用户消息数量
     * @param userId 用户ID
     * @return 消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计会话消息数量
     * @param sessionId 会话ID
     * @return 消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE session_id = #{sessionId}")
    int countBySessionId(@Param("sessionId") String sessionId);
} 