package com.example.lead.service;

import com.example.common.dto.CommonResult;
import com.example.lead.dto.SourceDetectionRequest;
import com.example.lead.dto.SourceValidationRequest;

import java.util.List;
import java.util.Map;

public interface SourceDetectionService {
    CommonResult<Map<String,Object>> detectSource(SourceDetectionRequest request);
    CommonResult<Map<String,Object>> validateSource(SourceValidationRequest request);
    CommonResult<Map<String,Object>> getSourceSuggestions(String keyword);
    CommonResult<Map<String,Object>> getLeadStatistics(String auditStatus, String status, String source, Long salespersonId, String startDate, String endDate);
}

