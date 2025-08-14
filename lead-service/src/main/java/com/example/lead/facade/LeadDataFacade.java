package com.example.lead.facade;

import com.example.data.entity.CustomerLead;
import com.example.data.mapper.CustomerLeadMapper;
import com.example.lead.converter.LeadDtoConverter;
import com.example.lead.dto.CreateLeadRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.LeadDetailsDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import com.example.lead.dto.UpdateLeadRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LeadDataFacade {

    private final CustomerLeadMapper leadMapper;

    public LeadDataFacade(CustomerLeadMapper leadMapper) {
        this.leadMapper = leadMapper;
    }

    public Optional<LeadDetailsDto> findDetailsById(Long id) {
        return leadMapper.findById(id).map(LeadDtoConverter::toDetails);
    }

    public boolean existsByPhone(String phone, Long excludeId) {
        Optional<CustomerLead> opt = leadMapper.findByPhone(phone);
        return opt.filter(e -> excludeId == null || !excludeId.equals(e.getId())).isPresent();
    }

    public CustomerLeadDto create(CreateLeadRequest req) {
        CustomerLead e = LeadDtoConverter.toEntity(req);
        leadMapper.insert(e);
        return LeadDtoConverter.toDto(e);
    }

    public boolean updateStatus(Long id, String statusCode) {
        CustomerLead.LeadStatus status = CustomerLead.LeadStatus.fromCode(statusCode);
        return leadMapper.updateFollowUp(id, status, LocalDateTime.now(), LocalDateTime.now()) > 0;
    }

    public boolean batchUpdateAuditStatus(List<Long> ids, String auditStatusCode) {
        CustomerLead.AuditStatus as = CustomerLead.AuditStatus.fromCode(auditStatusCode);
        return leadMapper.batchUpdateAuditStatus(ids, as, LocalDateTime.now()) > 0;
    }
    public boolean updateAuditStatus(Long id, String auditStatusCode) {
        CustomerLead.AuditStatus as = CustomerLead.AuditStatus.fromCode(auditStatusCode);
        return leadMapper.updateAuditStatus(id, as, java.time.LocalDateTime.now()) > 0;
    }


    // 向后兼容的重载：不含 keyword/source 参数
    public List<CustomerLeadDto> findPage(Integer page, Integer size, Long salespersonId, String status, String auditStatus) {
        return findPage(page, size, salespersonId, status, auditStatus, null, null, null, null, null, null);
    }
    public boolean updateLead(Long id, UpdateLeadRequest req) {
        return leadMapper.findById(id).map(e -> {
            if (StringUtils.hasText(req.getName())) e.setName(req.getName());
            if (StringUtils.hasText(req.getPhone())) e.setPhone(req.getPhone());
            if (StringUtils.hasText(req.getWechatId())) e.setWechatId(req.getWechatId());
            // email 字段暂未入库，保留请求但不持久化
            if (StringUtils.hasText(req.getNotes())) e.setNotes(req.getNotes());
            e.setUpdatedAt(LocalDateTime.now());
            return leadMapper.update(e) > 0;
        }).orElse(false);
    }

    public boolean deleteLead(Long id) {
        return leadMapper.deleteById(id) > 0;
    }

    public com.example.lead.dto.PageResult<CustomerLeadDto> findPageWithCount(Integer page, Integer size,
            Long salespersonId, String status, String auditStatus, String keyword, String source, String startDate, String endDate, String sortBy, String sortOrder) {
        java.util.List<CustomerLeadDto> data = findPage(page, size, salespersonId, status, auditStatus, keyword, source, startDate, endDate, sortBy, sortOrder);
        long total = countByConditions(salespersonId, status, auditStatus, keyword, source, startDate, endDate);
        return new com.example.lead.dto.PageResult<>(data, total, page == null ? 1 : page, size == null ? 10 : size);
    }

    public List<CustomerLeadDto> findPage(Integer page, Integer size, Long salespersonId, String status, String auditStatus,
                                          String keyword, String source, String startDate, String endDate, String sortBy, String sortOrder) {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : size;
        int offset = (p - 1) * s;
        List<CustomerLead> list = leadMapper.findByConditions(salespersonId, status, auditStatus, keyword, source, startDate, endDate, sortBy, sortOrder, offset, s);
        return list.stream().map(LeadDtoConverter::toDto).collect(Collectors.toList());
    }

    public long countByConditions(Long salespersonId, String status, String auditStatus, String keyword, String source, String startDate, String endDate) {
        return leadMapper.countByConditions(salespersonId, status, auditStatus, keyword, source, startDate, endDate);
    }

}

