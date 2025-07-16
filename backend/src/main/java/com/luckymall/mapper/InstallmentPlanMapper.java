package com.luckymall.mapper;

import com.luckymall.entity.InstallmentPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分期方案Mapper接口
 */
@Mapper
public interface InstallmentPlanMapper {
    
    /**
     * 查询所有启用的分期方案
     */
    List<InstallmentPlan> selectActiveInstallmentPlans();
    
    /**
     * 根据金额查询可用的分期方案
     */
    List<InstallmentPlan> selectAvailableInstallmentPlans(@Param("amount") BigDecimal amount);
    
    /**
     * 根据ID查询分期方案
     */
    InstallmentPlan selectInstallmentPlanById(@Param("id") Long id);
    
    /**
     * 插入分期方案
     */
    int insertInstallmentPlan(InstallmentPlan installmentPlan);
    
    /**
     * 更新分期方案
     */
    int updateInstallmentPlan(InstallmentPlan installmentPlan);
    
    /**
     * 删除分期方案
     */
    int deleteInstallmentPlan(@Param("id") Long id);
} 