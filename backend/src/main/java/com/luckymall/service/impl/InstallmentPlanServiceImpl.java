package com.luckymall.service.impl;

import com.luckymall.entity.InstallmentPlan;
import com.luckymall.mapper.InstallmentPlanMapper;
import com.luckymall.service.InstallmentPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分期方案服务实现类
 */
@Slf4j
@Service
public class InstallmentPlanServiceImpl implements InstallmentPlanService {

    @Autowired
    private InstallmentPlanMapper installmentPlanMapper;

    @Override
    public List<InstallmentPlan> getActiveInstallmentPlans() {
        return installmentPlanMapper.selectActiveInstallmentPlans();
    }

    @Override
    public List<InstallmentPlan> getAvailableInstallmentPlans(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }
        return installmentPlanMapper.selectAvailableInstallmentPlans(amount);
    }

    @Override
    public InstallmentPlan getInstallmentPlanById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("分期方案ID不能为空");
        }
        return installmentPlanMapper.selectInstallmentPlanById(id);
    }
} 