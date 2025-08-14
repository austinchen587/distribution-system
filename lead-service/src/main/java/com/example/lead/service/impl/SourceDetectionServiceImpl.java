package com.example.lead.service.impl;

import com.example.common.constants.ErrorCode;
import com.example.common.dto.CommonResult;
import com.example.lead.dto.SourceDetectionRequest;
import com.example.lead.dto.SourceValidationRequest;
import com.example.lead.service.SourceDetectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SourceDetectionServiceImpl implements SourceDetectionService {
    @Override
    public CommonResult<Map<String, Object>> detectSource(SourceDetectionRequest request) {
        try {
            // 最小占位实现：根据 utm/referrer 简单识别
            String detected = request.getUtmParams() != null && request.getUtmParams().get("utm_source") != null
                    ? request.getUtmParams().get("utm_source")
                    : (request.getReferrer() != null ? (request.getReferrer().contains("google") ? "google" : "website") : "website");
            Map<String,Object> data = java.util.Map.of(
                    "detectedSource", detected,
                    "confidence", 0.8,
                    "sourceDetail", request.getReferrer(),
                    "suggestedCategories", List.of("搜索", "社交"));
            return CommonResult.success(data);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.LEAD_010.getHttpCode(), "来源检测失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Map<String, Object>> validateSource(SourceValidationRequest request) {
        try {
            boolean valid = request.getSource() != null && !request.getSource().isEmpty();
            Map<String,Object> data = java.util.Map.of(
                    "isValid", valid,
                    "validatedSource", java.util.Map.of("id", request.getSource(), "name", request.getSource()),
                    "utmValidation", java.util.Map.of("isValid", true)
            );
            return CommonResult.success(data);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.LEAD_015.getHttpCode(), "来源验证失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Map<String, Object>> getSourceSuggestions(String keyword) {
        try {
            java.util.List<java.util.Map<String,Object>> suggestions = java.util.Arrays.asList(
                    java.util.Map.of("id", "website", "name", "官网"),
                    java.util.Map.of("id", "wechat", "name", "微信")
            );
            return CommonResult.success(java.util.Map.of("suggestions", suggestions));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Map<String, Object>> getLeadStatistics(String auditStatus, String status, String source, Long salespersonId, String startDate, String endDate) {
        try {
            // 最小占位统计
            return CommonResult.success(java.util.Map.of(
                    "totalCount", 0,
                    "pendingCount", 0,
                    "approvedCount", 0,
                    "rejectedCount", 0
            ));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
}

