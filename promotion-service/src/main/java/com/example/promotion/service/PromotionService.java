package com.example.promotion.service;

import com.example.promotion.dto.CreatePromotionRequest;
import com.example.promotion.dto.PromotionDto;
import com.example.promotion.dto.PromotionAuditDto;
import com.example.common.dto.CommonResult;

import java.util.List;

/**
 * 推广任务服务接口
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
public interface PromotionService {
    
    /**
     * 创建推广任务
     */
    CommonResult<PromotionDto> createPromotion(CreatePromotionRequest request);
    
    /**
     * 根据ID获取推广任务详情
     */
    CommonResult<PromotionAuditDto> getPromotionById(Long id);
    
    /**
     * 分页查询推广任务列表
     */
    CommonResult<List<PromotionDto>> getPromotionList(int page, int size, Long creatorId, String status, String auditStatus);
    
    /**
     * 更新推广任务
     */
    CommonResult<Void> updatePromotion(Long id, CreatePromotionRequest request);
    
    /**
     * 删除推广任务
     */
    CommonResult<Void> deletePromotion(Long id);
    
    /**
     * 提交推广任务进行审核
     */
    CommonResult<Void> submitForAudit(Long id);
    
    /**
     * 审核推广任务
     */
    CommonResult<Void> auditPromotion(Long id, String auditStatus, String auditComment, Long auditorId, String auditorName);
    
    /**
     * 批量审核推广任务
     */
    CommonResult<Void> batchAuditPromotions(List<Long> ids, String auditStatus, Long auditorId, String auditorName);
    
    /**
     * 获取用户的推广任务列表
     */
    CommonResult<List<PromotionDto>> getUserPromotions(Long creatorId);
    
    /**
     * 获取待审核的推广任务列表
     */
    CommonResult<List<PromotionDto>> getPendingAuditPromotions();
}