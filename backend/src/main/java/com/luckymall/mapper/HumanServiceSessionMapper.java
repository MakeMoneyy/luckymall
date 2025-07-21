package com.luckymall.mapper;

import com.luckymall.entity.HumanServiceSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人工客服会话Mapper接口
 */
@Mapper
public interface HumanServiceSessionMapper {
    
    /**
     * 插入人工客服会话
     * @param session 会话
     * @return 影响行数
     */
    int insert(HumanServiceSession session);
    
    /**
     * 更新人工客服会话
     * @param session 会话
     * @return 影响行数
     */
    int update(HumanServiceSession session);
    
    /**
     * 根据会话ID查询
     * @param sessionId 会话ID
     * @return 人工客服会话
     */
    HumanServiceSession findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据用户ID和会话ID查询
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 人工客服会话
     */
    HumanServiceSession findByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);
    
    /**
     * 根据状态查询
     * @param status 状态
     * @return 人工客服会话列表
     */
    List<HumanServiceSession> findByStatus(@Param("status") String status);
    
    /**
     * 根据客服ID查询
     * @param staffId 客服ID
     * @return 人工客服会话列表
     */
    List<HumanServiceSession> findByStaffId(@Param("staffId") String staffId);
    
    /**
     * 更新会话状态
     * @param sessionId 会话ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") String status);
    
    /**
     * 分配客服
     * @param sessionId 会话ID
     * @param staffId 客服ID
     * @return 影响行数
     */
    int assignStaff(@Param("sessionId") String sessionId, @Param("staffId") String staffId);
} 