package com.luckymall.service;

import com.luckymall.dto.AddAddressRequest;
import com.luckymall.entity.UserAddress;
import java.util.List;

/**
 * 用户地址服务接口
 */
public interface UserAddressService {
    
    /**
     * 添加用户地址
     */
    UserAddress addAddress(Long userId, AddAddressRequest request);
    
    /**
     * 根据ID查询用户地址
     */
    UserAddress getAddressById(Long addressId);
    
    /**
     * 根据用户ID查询地址列表
     */
    List<UserAddress> getAddressesByUserId(Long userId);
    
    /**
     * 根据用户ID查询默认地址
     */
    UserAddress getDefaultAddress(Long userId);
    
    /**
     * 更新用户地址
     */
    boolean updateAddress(Long addressId, Long userId, AddAddressRequest request);
    
    /**
     * 删除用户地址
     */
    boolean deleteAddress(Long addressId, Long userId);
    
    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long addressId, Long userId);
} 