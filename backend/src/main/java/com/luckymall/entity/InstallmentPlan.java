package com.luckymall.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分期方案实体类
 */
@Data
public class InstallmentPlan {
    
    /**
     * 分期方案ID
     */
    private Long id;
    
    /**
     * 方案名称
     */
    private String planName;
    
    /**
     * 分期期数
     */
    private Integer installmentCount;
    
    /**
     * 利率（%）
     */
    private BigDecimal interestRate;
    
    /**
     * 最小分期金额
     */
    private BigDecimal minAmount;
    
    /**
     * 最大分期金额
     */
    private BigDecimal maxAmount;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 计算每期金额
     */
    public BigDecimal calculateMonthlyAmount(BigDecimal totalAmount) {
        if (totalAmount == null || installmentCount == null || installmentCount <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 简单等额本息计算（免息情况下就是平均分配）
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) == 0) {
            return totalAmount.divide(new BigDecimal(installmentCount), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        // 有息分期计算（这里简化处理）
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalWithInterest = totalAmount.multiply(BigDecimal.ONE.add(monthlyRate.multiply(new BigDecimal(installmentCount))));
        return totalWithInterest.divide(new BigDecimal(installmentCount), 2, BigDecimal.ROUND_HALF_UP);
    }
} 