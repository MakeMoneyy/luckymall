package com.luckymall.service;

import com.luckymall.entity.InstallmentPlan;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分期方案服务接口
 */
public interface InstallmentPlanService {
    
    /**
     * 查询所有启用的分期方案
     */
    List<InstallmentPlan> getActiveInstallmentPlans();
    
    /**
     * 根据金额查询可用的分期方案
     */
    List<InstallmentPlan> getAvailableInstallmentPlans(BigDecimal amount);
    
    /**
     * 根据ID查询分期方案
     */
    InstallmentPlan getInstallmentPlanById(Long id);
} 