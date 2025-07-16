package com.luckymall.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 添加地址请求DTO
 */
@Data
public class AddAddressRequest {
    
    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;
    
    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;
    
    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;
    
    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    private String district;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 是否设为默认地址
     */
    private Boolean isDefault = false;
} 