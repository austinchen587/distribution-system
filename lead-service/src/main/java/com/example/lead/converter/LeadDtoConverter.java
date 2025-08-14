package com.example.lead.converter;

import com.example.lead.dto.CreateLeadRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.LeadDetailsDto;
import com.example.data.entity.CustomerLead;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lead DTO 转换器：data-access 实体 <-> lead-service DTO
 */
public class LeadDtoConverter {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CustomerLead toEntity(CreateLeadRequest req) {
        CustomerLead e = new CustomerLead();
        e.setName(req.getName());
        e.setPhone(req.getPhone());
        e.setWechatId(req.getWechatId());
        e.setSource(req.getSource());
        e.setSourceDetail(req.getSourceDetail());
        e.setSalespersonId(req.getSalespersonId());
        e.setNotes(req.getNotes());
        // 默认状态
        e.setStatus(CustomerLead.LeadStatus.PENDING);
        e.setAuditStatus(CustomerLead.AuditStatus.PENDING_AUDIT);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    public static CustomerLeadDto toDto(CustomerLead e) {
        CustomerLeadDto dto = new CustomerLeadDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setPhone(e.getPhone());
        dto.setStatus(e.getStatus() != null ? e.getStatus().getCode() : null);
        dto.setAuditStatus(e.getAuditStatus() != null ? e.getAuditStatus().getCode() : null);
        dto.setSource(e.getSource());
        dto.setSalespersonId(e.getSalespersonId());
        // 时间格式
        if (e.getLastFollowUpAt() != null) {
            dto.setLastFollowUpAt(DF.format(e.getLastFollowUpAt()));
        }
        if (e.getCreatedAt() != null) {
            dto.setCreatedAt(DF.format(e.getCreatedAt()));
        }
        return dto;
    }

    public static LeadDetailsDto toDetails(CustomerLead e) {
        LeadDetailsDto details = new LeadDetailsDto();
        details.setLeadInfo(toDto(e));
        details.setWechatId(e.getWechatId());
        details.setSourceDetail(e.getSourceDetail());
        details.setNotes(e.getNotes());
        // 审核类字段暂空，后续审核模块接入时补充
        if (e.getUpdatedAt() != null) {
            details.setUpdatedAt(DF.format(e.getUpdatedAt()));
        }
        return details;
    }
}

