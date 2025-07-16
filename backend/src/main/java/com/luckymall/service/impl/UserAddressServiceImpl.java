package com.luckymall.service.impl;

import com.luckymall.dto.AddAddressRequest;
import com.luckymall.entity.UserAddress;
import com.luckymall.mapper.UserAddressMapper;
import com.luckymall.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户地址服务实现类
 */
@Slf4j
@Service
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public UserAddress addAddress(Long userId, AddAddressRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (request == null) {
            throw new IllegalArgumentException("地址信息不能为空");
        }
        
        try {
            // 如果设置为默认地址，先清除用户的其他默认地址
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                userAddressMapper.clearDefaultAddress(userId);
            }
            
            // 创建用户地址
            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(userId);
            userAddress.setReceiverName(request.getReceiverName());
            userAddress.setReceiverPhone(request.getReceiverPhone());
            userAddress.setProvince(request.getProvince());
            userAddress.setCity(request.getCity());
            userAddress.setDistrict(request.getDistrict());
            userAddress.setDetailAddress(request.getDetailAddress());
            userAddress.setPostalCode(request.getPostalCode());
            userAddress.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
            
            // 插入地址
            int result = userAddressMapper.insertUserAddress(userAddress);
            if (result > 0) {
                log.info("用户{}添加地址成功，地址ID：{}", userId, userAddress.getId());
                return userAddress;
            } else {
                throw new RuntimeException("添加地址失败");
            }
            
        } catch (Exception e) {
            log.error("添加用户地址失败", e);
            throw new RuntimeException("添加地址失败: " + e.getMessage());
        }
    }

    @Override
    public UserAddress getAddressById(Long addressId) {
        if (addressId == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }
        return userAddressMapper.selectUserAddressById(addressId);
    }

    @Override
    public List<UserAddress> getAddressesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userAddressMapper.selectUserAddressesByUserId(userId);
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userAddressMapper.selectDefaultAddressByUserId(userId);
    }

    @Override
    public boolean updateAddress(Long addressId, Long userId, AddAddressRequest request) {
        if (addressId == null || userId == null || request == null) {
            return false;
        }
        
        try {
            // 验证地址是否属于当前用户
            UserAddress existingAddress = userAddressMapper.selectUserAddressById(addressId);
            if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
                throw new RuntimeException("地址不存在或无权限修改");
            }
            
            // 如果设置为默认地址，先清除用户的其他默认地址
            if (Boolean.TRUE.equals(request.getIsDefault()) && existingAddress.getIsDefault() != 1) {
                userAddressMapper.clearDefaultAddress(userId);
            }
            
            // 更新地址信息
            UserAddress updateAddress = new UserAddress();
            updateAddress.setId(addressId);
            updateAddress.setReceiverName(request.getReceiverName());
            updateAddress.setReceiverPhone(request.getReceiverPhone());
            updateAddress.setProvince(request.getProvince());
            updateAddress.setCity(request.getCity());
            updateAddress.setDistrict(request.getDistrict());
            updateAddress.setDetailAddress(request.getDetailAddress());
            updateAddress.setPostalCode(request.getPostalCode());
            updateAddress.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
            
            int result = userAddressMapper.updateUserAddress(updateAddress);
            if (result > 0) {
                log.info("用户{}更新地址{}成功", userId, addressId);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            log.error("更新用户地址失败", e);
            throw new RuntimeException("更新地址失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAddress(Long addressId, Long userId) {
        if (addressId == null || userId == null) {
            return false;
        }
        
        try {
            // 验证地址是否属于当前用户
            UserAddress existingAddress = userAddressMapper.selectUserAddressById(addressId);
            if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
                throw new RuntimeException("地址不存在或无权限删除");
            }
            
            int result = userAddressMapper.deleteUserAddress(addressId);
            if (result > 0) {
                log.info("用户{}删除地址{}成功", userId, addressId);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            log.error("删除用户地址失败", e);
            throw new RuntimeException("删除地址失败: " + e.getMessage());
        }
    }

    @Override
    public boolean setDefaultAddress(Long addressId, Long userId) {
        if (addressId == null || userId == null) {
            return false;
        }
        
        try {
            // 验证地址是否属于当前用户
            UserAddress existingAddress = userAddressMapper.selectUserAddressById(addressId);
            if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
                throw new RuntimeException("地址不存在或无权限修改");
            }
            
            // 先清除用户的所有默认地址
            userAddressMapper.clearDefaultAddress(userId);
            
            // 设置指定地址为默认地址
            int result = userAddressMapper.setDefaultAddress(addressId);
            if (result > 0) {
                log.info("用户{}设置默认地址{}成功", userId, addressId);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            log.error("设置默认地址失败", e);
            throw new RuntimeException("设置默认地址失败: " + e.getMessage());
        }
    }
} 