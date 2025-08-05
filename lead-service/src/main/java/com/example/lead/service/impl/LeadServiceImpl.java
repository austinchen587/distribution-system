package com.example.lead.service.impl;

import com.example.lead.dto.CreateLeadRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.LeadDetailsDto;
import com.example.lead.entity.CustomerLead;
import com.example.lead.mapper.CustomerLeadMapper;
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
import java.util.Date;
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
    private CustomerLeadMapper customerLeadMapper;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public CommonResult<CustomerLeadDto> createLead(CreateLeadRequest request) {
        try {
            // 检查重复
            CustomerLead existing = customerLeadMapper.selectByPhone(request.getPhone());
            if (existing != null) {
                return CommonResult.error(ErrorCode.CONFLICT.getCode(), "客资已存在");
            }
            
            // 创建客资
            CustomerLead customerLead = new CustomerLead();
            customerLead.setName(request.getName());
            customerLead.setPhone(request.getPhone());
            customerLead.setSource(request.getSource());
            customerLead.setSalespersonId(request.getSalespersonId());
            // TODO: 从用户服务获取销售员姓名
            customerLead.setSalespersonName("销售员" + request.getSalespersonId());
            customerLead.setStatus("PENDING");
            customerLead.setAuditStatus("PENDING_AUDIT");
            customerLead.setCreatedAt(new Date());
            customerLead.setUpdatedAt(new Date());
            
            int result = customerLeadMapper.insert(customerLead);
            if (result > 0) {
                CustomerLeadDto dto = convertToDto(customerLead);
                return CommonResult.success(dto);
            } else {
                return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "创建客资失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<LeadDetailsDto> getLeadById(Long id) {
        try {
            CustomerLead customerLead = customerLeadMapper.selectById(id);
            if (customerLead == null) {
                return CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在");
            }
            
            LeadDetailsDto dto = convertToDetailsDto(customerLead);
            return CommonResult.success(dto);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<CustomerLeadDto>> getLeadList(int page, int size, Long salespersonId, String status) {
        try {
            int offset = (page - 1) * size;
            List<CustomerLead> leads = customerLeadMapper.selectLeads(offset, size, salespersonId, status);
            int total = customerLeadMapper.countLeads(salespersonId, status);
            
            List<CustomerLeadDto> dtoList = new ArrayList<>();
            for (CustomerLead lead : leads) {
                dtoList.add(convertToDto(lead));
            }
            
            return CommonResult.success(dtoList);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Boolean> checkDuplicate(String phone) {
        try {
            CustomerLead existing = customerLeadMapper.selectByPhone(phone);
            return CommonResult.success(existing != null);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> updateLeadStatus(Long id, String status) {
        try {
            CustomerLead customerLead = customerLeadMapper.selectById(id);
            if (customerLead == null) {
                return CommonResult.error(ErrorCode.LEAD_001.getHttpCode(), "客资不存在");
            }
            
            customerLead.setStatus(status);
            customerLead.setUpdatedAt(new Date());
            
            int result = customerLeadMapper.update(customerLead);
            if (result > 0) {
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
            int result = customerLeadMapper.batchUpdateAuditStatus(ids, auditStatus);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "批量审核失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    /**
     * 转换为DTO
     */
    private CustomerLeadDto convertToDto(CustomerLead entity) {
        CustomerLeadDto dto = new CustomerLeadDto();
        BeanUtils.copyProperties(entity, dto);
        
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(dateFormat.format(entity.getCreatedAt()));
        }
        if (entity.getLastFollowUpAt() != null) {
            dto.setLastFollowUpAt(dateFormat.format(entity.getLastFollowUpAt()));
        }
        
        return dto;
    }
    
    /**
     * 转换为详情DTO
     */
    private LeadDetailsDto convertToDetailsDto(CustomerLead entity) {
        LeadDetailsDto dto = new LeadDetailsDto();
        
        // 创建基础信息DTO
        CustomerLeadDto leadInfo = convertToDto(entity);
        dto.setLeadInfo(leadInfo);
        
        // 设置详情字段 (使用默认值，因为CustomerLead实体中没有这些字段)
        dto.setWechatId(null); // TODO: 从请求参数中获取
        dto.setSourceDetail(null); // TODO: 从请求参数中获取  
        dto.setNotes(null); // TODO: 从请求参数中获取
        dto.setReferralCode(null); // TODO: 从请求参数中获取
        dto.setAuditComment(null); // TODO: 待实现审核功能
        dto.setRejectReason(null); // TODO: 待实现审核功能
        dto.setAuditorName(null); // TODO: 待实现审核功能
        
        // 审核时间暂时为空，待实现审核功能
        dto.setAuditedAt(null);
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(dateFormat.format(entity.getUpdatedAt()));
        }
        
        return dto;
    }
}