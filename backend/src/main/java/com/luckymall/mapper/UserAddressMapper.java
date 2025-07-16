package com.luckymall.mapper;

import com.luckymall.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户地址Mapper接口
 */
@Mapper
public interface UserAddressMapper {
    
    /**
     * 插入用户地址
     */
    int insertUserAddress(UserAddress userAddress);
    
    /**
     * 根据ID查询用户地址
     */
    UserAddress selectUserAddressById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询地址列表
     */
    List<UserAddress> selectUserAddressesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询默认地址
     */
    UserAddress selectDefaultAddressByUserId(@Param("userId") Long userId);
    
    /**
     * 更新用户地址
     */
    int updateUserAddress(UserAddress userAddress);
    
    /**
     * 删除用户地址
     */
    int deleteUserAddress(@Param("id") Long id);
    
    /**
     * 取消用户的所有默认地址
     */
    int clearDefaultAddress(@Param("userId") Long userId);
    
    /**
     * 设置默认地址
     */
    int setDefaultAddress(@Param("id") Long id);
} 