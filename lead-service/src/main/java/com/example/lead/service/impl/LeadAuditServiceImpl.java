package com.example.lead.service.impl;

import com.example.common.constants.ErrorCode;
import com.example.common.dto.CommonResult;
import com.example.common.enums.UserRole;
import com.example.common.utils.UserContextHolder;
import com.example.lead.dto.BatchAuditRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.PageResult;
import com.example.lead.facade.LeadDataFacade;
import com.example.lead.service.LeadAuditService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LeadAuditServiceImpl implements LeadAuditService {

    private final LeadDataFacade facade;

    public LeadAuditServiceImpl(LeadDataFacade facade) {
        this.facade = facade;
    }

    private boolean hasAuditPermission(Long salespersonId) {
        String roleCode = UserContextHolder.getCurrentUserRole();
        if (!StringUtils.hasText(roleCode)) return false;
        UserRole role = UserRole.fromCode(roleCode);
        switch (role) {
            case SUPER_ADMIN:
            case DIRECTOR:
            case LEADER:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CommonResult<CustomerLeadDto> auditOne(Long leadId, String decision, String comment, String rejectReason) {
        if (!hasAuditPermission(null)) {
            return CommonResult.error(ErrorCode.LEAD_007.getHttpCode(), "权限不足");
        }
        try {
            String status = "APPROVED".equalsIgnoreCase(decision) ? "APPROVED" : "REJECTED";
            boolean ok = facade.updateAuditStatus(leadId, status);
            if (!ok) return CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在");
            return facade.findDetailsById(leadId)
                    .map(d -> CommonResult.success(d.getLeadInfo()))
                    .orElseGet(() -> CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在"));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> batchAudit(BatchAuditRequest request) {
        if (!hasAuditPermission(null)) {
            return CommonResult.error(ErrorCode.LEAD_007.getHttpCode(), "权限不足");
        }
        try {
            boolean ok = facade.batchUpdateAuditStatus(request.getIds(), request.getAuditStatus());
            if (ok) return CommonResult.success(null);
            return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "批量审核失败");
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<PageResult<CustomerLeadDto>> listPending(int page, int pageSize, String keyword, Long salespersonId) {
        try {
            PageResult<CustomerLeadDto> pr = facade.findPageWithCount(page, pageSize, salespersonId, null, "PENDING_AUDIT", keyword, null, null, null, null, null);
            return CommonResult.success(pr);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<PageResult<CustomerLeadDto>> listAll(int page, int pageSize, String keyword, String auditStatus, Long salespersonId) {
        try {
            PageResult<CustomerLeadDto> pr = facade.findPageWithCount(page, pageSize, salespersonId, null, auditStatus, keyword, null, null, null, null, null);
            return CommonResult.success(pr);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Boolean> checkPermission(Long leadId, Long salespersonId) {
        try {
            return CommonResult.success(hasAuditPermission(salespersonId));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Object> auditScope() {
        try {
            String role = UserContextHolder.getCurrentUserRole();
            return CommonResult.success(java.util.Map.of("role", role));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<PageResult<CustomerLeadDto>> getAuditRecords(int page, int pageSize, String keyword, String auditStatus,
                                                                     Long salespersonId, String startDate, String endDate,
                                                                     String sortBy, String sortOrder) {
        try {
            PageResult<CustomerLeadDto> pr = facade.findPageWithCount(page, pageSize, salespersonId, null, auditStatus, keyword, null, startDate, endDate, sortBy, sortOrder);
            return CommonResult.success(pr);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<java.util.Map<String, Object>> getAuditStatistics(String status, String source, Long salespersonId,
                                                                          String startDate, String endDate) {
        try {
            // 最小占位统计：统计不同审核状态数量（待完善）
            java.util.Map<String, Object> stats = java.util.Map.of(
                    "pending", 0,
                    "approved", 0,
                    "rejected", 0
            );
            return CommonResult.success(stats);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
}

