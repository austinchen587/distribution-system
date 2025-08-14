package com.example.lead.facade;

import com.example.data.entity.CustomerLead;
import com.example.data.mapper.CustomerLeadMapper;
import com.example.lead.converter.LeadDtoConverter;
import com.example.lead.dto.CreateLeadRequest;
import com.example.lead.dto.CustomerLeadDto;
import com.example.lead.dto.LeadDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeUnit;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import com.example.lead.dto.UpdateLeadRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LeadDataFacade {

    private final CustomerLeadMapper leadMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redis;

    public LeadDataFacade(CustomerLeadMapper leadMapper) {
        this.leadMapper = leadMapper;
    }

    public Optional<LeadDetailsDto> findDetailsById(Long id) {
        return leadMapper.findById(id).map(LeadDtoConverter::toDetails);
    }

    public boolean existsByPhone(String phone, Long excludeId) {
        // 先查缓存
        if (redis != null) {
            String key = "lead:exists:phone:" + phone;
            Object v = redis.opsForValue().get(key);
            if (v != null) {
                boolean exists = "1".equals(v.toString());
                if (!exists) return false; // 缓存明确为不存在时直接返回
                // 存在时如需排除ID，仍需DB核对
                if (excludeId == null) return true;
            }
        }
        Optional<CustomerLead> opt = leadMapper.findByPhone(phone);
        boolean present = opt.filter(e -> excludeId == null || !excludeId.equals(e.getId())).isPresent();
        if (redis != null) {
            String key = "lead:exists:phone:" + phone;
            redis.opsForValue().set(key, present ? "1" : "0", 5, TimeUnit.MINUTES);
        }
        return present;
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
        // 轻量分页缓存（短TTL + 版本号）
        String ver = "1";
        String verKey = "lead:list:ver";
        List<CustomerLeadDto> data;
        long total;
        boolean fromCache = false;
        if (redis != null) {
            Object v = redis.opsForValue().get(verKey);
            if (v != null) ver = v.toString();
            String raw = String.format("p=%s,s=%s,sp=%s,st=%s,as=%s,kw=%s,src=%s,sd=%s,ed=%s,sb=%s,so=%s",
                    page,size,salespersonId,status,auditStatus,keyword,source,startDate,endDate,sortBy,sortOrder);
            String k = "lead:list:v" + ver + ":" + org.springframework.util.DigestUtils.md5DigestAsHex(raw.getBytes());
            Object cached = redis.opsForValue().get(k);
            if (cached instanceof com.example.lead.dto.PageResult) {
                @SuppressWarnings("unchecked")
                com.example.lead.dto.PageResult<CustomerLeadDto> pr = (com.example.lead.dto.PageResult<CustomerLeadDto>) cached;
                return pr;
            }
        }
        // miss → DB
        data = findPage(page, size, salespersonId, status, auditStatus, keyword, source, startDate, endDate, sortBy, sortOrder);
        total = countByConditions(salespersonId, status, auditStatus, keyword, source, startDate, endDate);
        com.example.lead.dto.PageResult<CustomerLeadDto> pr = new com.example.lead.dto.PageResult<>(data, total, page == null ? 1 : page, size == null ? 10 : size);
        if (redis != null) {
            String raw = String.format("p=%s,s=%s,sp=%s,st=%s,as=%s,kw=%s,src=%s,sd=%s,ed=%s,sb=%s,so=%s",
                    page,size,salespersonId,status,auditStatus,keyword,source,startDate,endDate,sortBy,sortOrder);
            String k = "lead:list:v" + ver + ":" + org.springframework.util.DigestUtils.md5DigestAsHex(raw.getBytes());
            redis.opsForValue().set(k, pr, 60, TimeUnit.SECONDS);
        }
        return pr;
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

