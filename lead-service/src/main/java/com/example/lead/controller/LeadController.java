package com.example.lead.controller;

import com.example.lead.dto.CreateLeadRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.LeadDetailsDto;
import com.example.lead.service.LeadService;
import com.example.common.dto.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 客资管理控制器
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@RestController
@RequestMapping("/api/leads")
@Tag(name = "客资管理", description = "客资管理相关接口")
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    @PostMapping("/create")
    @Operation(summary = "创建客资", description = "创建新的客户线索")
    public CommonResult<CustomerLeadDto> createLead(@Valid @RequestBody CreateLeadRequest request) {
        return leadService.createLead(request);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取客资详情", description = "根据ID获取客资详细信息")
    public CommonResult<LeadDetailsDto> getLeadById(
            @Parameter(description = "客资ID") @PathVariable Long id) {
        return leadService.getLeadById(id);
    }
    
    @GetMapping("/list")
    @Operation(summary = "获取客资列表", description = "分页查询客资列表")
    public CommonResult<List<CustomerLeadDto>> getLeadList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "销售ID") @RequestParam(required = false) Long salespersonId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return leadService.getLeadList(page, size, salespersonId, status);
    }
    
    @GetMapping("/check-duplicate")
    @Operation(summary = "检查重复", description = "检查手机号是否已存在")
    public CommonResult<Boolean> checkDuplicate(
            @Parameter(description = "手机号") @RequestParam String phone) {
        return leadService.checkDuplicate(phone);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "更新客资状态", description = "更新客资跟进状态")
    public CommonResult<Void> updateLeadStatus(
            @Parameter(description = "客资ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        return leadService.updateLeadStatus(id, status);
    }
    
    @PostMapping("/batch-audit")
    @Operation(summary = "批量审核", description = "批量审核客资")
    public CommonResult<Void> batchAuditLeads(
            @Parameter(description = "客资ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "审核状态") @RequestParam String auditStatus) {
        return leadService.batchAuditLeads(ids, auditStatus);
    }
}