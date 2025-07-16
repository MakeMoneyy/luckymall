package com.luckymall.controller;

import com.luckymall.common.Result;
import com.luckymall.dto.AddAddressRequest;
import com.luckymall.entity.UserAddress;
import com.luckymall.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
@Validated
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 添加用户地址
     */
    @PostMapping("/{userId}")
    public Result<UserAddress> addAddress(@PathVariable Long userId,
                                        @Valid @RequestBody AddAddressRequest request) {
        try {
            UserAddress address = userAddressService.addAddress(userId, request);
            return Result.success(address);
        } catch (Exception e) {
            log.error("添加地址失败", e);
            return Result.error("添加地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询地址列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<UserAddress>> getAddressesByUserId(@PathVariable Long userId) {
        try {
            List<UserAddress> addresses = userAddressService.getAddressesByUserId(userId);
            return Result.success(addresses);
        } catch (Exception e) {
            log.error("查询用户地址失败", e);
            return Result.error("查询地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询默认地址
     */
    @GetMapping("/user/{userId}/default")
    public Result<UserAddress> getDefaultAddress(@PathVariable Long userId) {
        try {
            UserAddress address = userAddressService.getDefaultAddress(userId);
            if (address == null) {
                return Result.error("暂无默认地址");
            }
            return Result.success(address);
        } catch (Exception e) {
            log.error("查询默认地址失败", e);
            return Result.error("查询默认地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询地址详情
     */
    @GetMapping("/{addressId}")
    public Result<UserAddress> getAddressById(@PathVariable Long addressId) {
        try {
            UserAddress address = userAddressService.getAddressById(addressId);
            if (address == null) {
                return Result.error("地址不存在");
            }
            return Result.success(address);
        } catch (Exception e) {
            log.error("查询地址失败", e);
            return Result.error("查询地址失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户地址
     */
    @PutMapping("/{addressId}")
    public Result<String> updateAddress(@PathVariable Long addressId,
                                      @RequestParam Long userId,
                                      @Valid @RequestBody AddAddressRequest request) {
        try {
            boolean success = userAddressService.updateAddress(addressId, userId, request);
            if (success) {
                return Result.success("地址更新成功");
            } else {
                return Result.error("地址更新失败");
            }
        } catch (Exception e) {
            log.error("更新地址失败", e);
            return Result.error("更新地址失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户地址
     */
    @DeleteMapping("/{addressId}")
    public Result<String> deleteAddress(@PathVariable Long addressId,
                                      @RequestParam Long userId) {
        try {
            boolean success = userAddressService.deleteAddress(addressId, userId);
            if (success) {
                return Result.success("地址删除成功");
            } else {
                return Result.error("地址删除失败");
            }
        } catch (Exception e) {
            log.error("删除地址失败", e);
            return Result.error("删除地址失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/{addressId}/default")
    public Result<String> setDefaultAddress(@PathVariable Long addressId,
                                          @RequestParam Long userId) {
        try {
            boolean success = userAddressService.setDefaultAddress(addressId, userId);
            if (success) {
                return Result.success("设置默认地址成功");
            } else {
                return Result.error("设置默认地址失败");
            }
        } catch (Exception e) {
            log.error("设置默认地址失败", e);
            return Result.error("设置默认地址失败: " + e.getMessage());
        }
    }
} 