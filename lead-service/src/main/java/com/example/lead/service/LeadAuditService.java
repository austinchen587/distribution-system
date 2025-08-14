package com.example.lead.service;

import com.example.common.dto.CommonResult;
import com.example.lead.dto.BatchAuditRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.PageResult;

import java.util.List;

public interface LeadAuditService {
    CommonResult<CustomerLeadDto> auditOne(Long leadId, String decision, String comment, String rejectReason);
    CommonResult<Void> batchAudit(BatchAuditRequest request);
    CommonResult<PageResult<CustomerLeadDto>> listPending(int page, int pageSize, String keyword, Long salespersonId);
    CommonResult<PageResult<CustomerLeadDto>> listAll(int page, int pageSize, String keyword, String auditStatus, Long salespersonId);
    CommonResult<Boolean> checkPermission(Long leadId, Long salespersonId);
    CommonResult<Object> auditScope();

    CommonResult<PageResult<CustomerLeadDto>> getAuditRecords(int page, int pageSize, String keyword, String auditStatus,
                                                              Long salespersonId, String startDate, String endDate,
                                                              String sortBy, String sortOrder);

    CommonResult<java.util.Map<String, Object>> getAuditStatistics(String status, String source, Long salespersonId,
                                                                   String startDate, String endDate);
}

