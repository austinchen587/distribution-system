package com.example.promotion.controller;

import com.example.promotion.dto.CreatePromotionRequest;
import com.example.promotion.dto.PromotionDto;
import com.example.promotion.dto.PromotionAuditDto;
import com.example.promotion.service.PromotionService;
import com.example.common.dto.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 推广任务管理控制器
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@RestController
@RequestMapping("/api/promotions")
@Tag(name = "推广任务管理", description = "推广任务管理相关接口")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @PostMapping("/create")
    @Operation(summary = "创建推广任务", description = "创建新的推广任务")
    public CommonResult<PromotionDto> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        return promotionService.createPromotion(request);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取推广任务详情", description = "根据ID获取推广任务详细信息")
    public CommonResult<PromotionAuditDto> getPromotionById(
            @Parameter(description = "推广任务ID") @PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }
    
    @GetMapping("/list")
    @Operation(summary = "获取推广任务列表", description = "分页查询推广任务列表")
    public CommonResult<List<PromotionDto>> getPromotionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "创建者ID") @RequestParam(required = false) Long creatorId,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "审核状态") @RequestParam(required = false) String auditStatus) {
        return promotionService.getPromotionList(page, size, creatorId, status, auditStatus);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新推广任务", description = "更新推广任务信息")
    public CommonResult<Void> updatePromotion(
            @Parameter(description = "推广任务ID") @PathVariable Long id,
            @Valid @RequestBody CreatePromotionRequest request) {
        return promotionService.updatePromotion(id, request);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除推广任务", description = "删除推广任务")
    public CommonResult<Void> deletePromotion(
            @Parameter(description = "推广任务ID") @PathVariable Long id) {
        return promotionService.deletePromotion(id);
    }
    
    @PostMapping("/{id}/submit")
    @Operation(summary = "提交审核", description = "提交推广任务进行审核")
    public CommonResult<Void> submitForAudit(
            @Parameter(description = "推广任务ID") @PathVariable Long id) {
        return promotionService.submitForAudit(id);
    }
    
    @PostMapping("/{id}/audit")
    @Operation(summary = "审核推广任务", description = "审核推广任务")
    public CommonResult<Void> auditPromotion(
            @Parameter(description = "推广任务ID") @PathVariable Long id,
            @Parameter(description = "审核状态") @RequestParam String auditStatus,
            @Parameter(description = "审核意见") @RequestParam(required = false) String auditComment,
            @Parameter(description = "审核人ID") @RequestParam Long auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName) {
        return promotionService.auditPromotion(id, auditStatus, auditComment, auditorId, auditorName);
    }
    
    @PostMapping("/batch-audit")
    @Operation(summary = "批量审核", description = "批量审核推广任务")
    public CommonResult<Void> batchAuditPromotions(
            @Parameter(description = "推广任务ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "审核状态") @RequestParam String auditStatus,
            @Parameter(description = "审核人ID") @RequestParam Long auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName) {
        return promotionService.batchAuditPromotions(ids, auditStatus, auditorId, auditorName);
    }
    
    @GetMapping("/user/{creatorId}")
    @Operation(summary = "获取用户推广任务", description = "获取指定用户的推广任务列表")
    public CommonResult<List<PromotionDto>> getUserPromotions(
            @Parameter(description = "创建者ID") @PathVariable Long creatorId) {
        return promotionService.getUserPromotions(creatorId);
    }
    
    @GetMapping("/pending-audit")
    @Operation(summary = "获取待审核任务", description = "获取待审核的推广任务列表")
    public CommonResult<List<PromotionDto>> getPendingAuditPromotions() {
        return promotionService.getPendingAuditPromotions();
    }
}