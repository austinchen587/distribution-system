package com.example.lead.service.impl;

import com.example.lead.dto.CreateLeadRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.LeadDetailsDto;
import com.example.lead.facade.LeadDataFacade;
import com.example.lead.service.LeadService;
import com.example.common.dto.CommonResult;
import com.example.common.constants.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 客资管理服务实现类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@Service
@Transactional
public class LeadServiceImpl implements LeadService {
    
    @Autowired
    private LeadDataFacade leadDataFacade;
    @Autowired(required = false)
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redis;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public CommonResult<CustomerLeadDto> createLead(CreateLeadRequest request) {
        try {
            // 检查重复
            boolean exists = leadDataFacade.existsByPhone(request.getPhone(), null);
            if (exists) {
                return CommonResult.error(ErrorCode.CONFLICT.getCode(), "客资已存在");
            }
            
            // 创建客资
            CustomerLeadDto dto = leadDataFacade.create(request);
            // 写入成功后，删除该手机号的查重缓存并递增列表缓存版本号
            if (redis != null) {
                try { redis.delete("lead:exists:phone:" + request.getPhone()); } catch (Exception ignore) {}
                try { redis.opsForValue().increment("lead:list:ver"); } catch (Exception ignore) {}
            }
            return CommonResult.success(dto);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<LeadDetailsDto> getLeadById(Long id) {
        try {
            return leadDataFacade.findDetailsById(id)
                    .map(CommonResult::success)
                    .orElseGet(() -> CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在"));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<com.example.lead.dto.PageResult<CustomerLeadDto>> getLeadList(int page, int size, Long salespersonId,
            String status, String auditStatus, String keyword, String source,
            String startDate, String endDate, String sortBy, String sortOrder) {
        try {
            com.example.lead.dto.PageResult<CustomerLeadDto> pageResult = leadDataFacade.findPageWithCount(page, size, salespersonId,
                    status, auditStatus, keyword, source, startDate, endDate, sortBy, sortOrder);
            return CommonResult.success(pageResult);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Boolean> checkDuplicate(String phone) {
        try {
            return CommonResult.success(leadDataFacade.existsByPhone(phone, null));
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> updateLeadStatus(Long id, String status) {
        try {
            boolean ok = leadDataFacade.updateStatus(id, status);
            if (ok) {
                if (redis != null) try { redis.opsForValue().increment("lead:list:ver"); } catch (Exception ignore) {}
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "更新客资状态失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> batchAuditLeads(List<Long> ids, String auditStatus) {
        try {
            boolean ok = leadDataFacade.batchUpdateAuditStatus(ids, auditStatus);
            if (ok) {
                if (redis != null) try { redis.opsForValue().increment("lead:list:ver"); } catch (Exception ignore) {}
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "批量审核失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateLead(Long id, com.example.lead.dto.UpdateLeadRequest request) {
        try {
            boolean ok = leadDataFacade.updateLead(id, request);
            if (ok) {
                if (redis != null) try { redis.opsForValue().increment("lead:list:ver"); } catch (Exception ignore) {}
                return CommonResult.success(null);
            }
            return CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在");
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteLead(Long id) {
        try {
            boolean ok = leadDataFacade.deleteLead(id);
            if (ok) {
                if (redis != null) try { redis.opsForValue().increment("lead:list:ver"); } catch (Exception ignore) {}
                return CommonResult.success(null);
            }
            return CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在");
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    

}