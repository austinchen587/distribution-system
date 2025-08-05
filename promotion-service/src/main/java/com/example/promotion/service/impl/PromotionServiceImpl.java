package com.example.promotion.service.impl;

import com.example.promotion.dto.CreatePromotionRequest;
import com.example.promotion.dto.PromotionDto;
import com.example.promotion.dto.PromotionAuditDto;
import com.example.promotion.entity.Promotion;
import com.example.promotion.mapper.PromotionMapper;
import com.example.promotion.service.PromotionService;
import com.example.common.dto.CommonResult;
import com.example.common.constants.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 推广任务服务实现类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@Service
public class PromotionServiceImpl implements PromotionService {
    
    @Autowired
    private PromotionMapper promotionMapper;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public CommonResult<PromotionDto> createPromotion(CreatePromotionRequest request) {
        try {
            // 创建推广任务实体
            Promotion promotion = new Promotion(
                request.getTitle(),
                request.getDescription(), 
                request.getPlatform(),
                "CONTENT", // 默认推广类型为内容推广
                request.getAgentId()
            );
            promotion.setCreatorName("代理商" + request.getAgentId()); // 默认创建者名称
            
            // 插入数据库
            int result = promotionMapper.insert(promotion);
            if (result > 0) {
                PromotionDto dto = convertToDto(promotion);
                return CommonResult.success(dto);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "创建推广任务失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<PromotionAuditDto> getPromotionById(Long id) {
        try {
            Promotion promotion = promotionMapper.selectById(id);
            if (promotion == null) {
                return CommonResult.error(ErrorCode.NOT_FOUND.getHttpCode(), "推广任务不存在");
            }
            
            PromotionAuditDto dto = convertToAuditDto(promotion);
            return CommonResult.success(dto);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<PromotionDto>> getPromotionList(int page, int size, Long creatorId, String status, String auditStatus) {
        try {
            int offset = (page - 1) * size;
            List<Promotion> promotions = promotionMapper.selectPromotions(offset, size, creatorId, status, auditStatus);
            int total = promotionMapper.countPromotions(creatorId, status, auditStatus);
            
            List<PromotionDto> dtoList = new ArrayList<>();
            for (Promotion promotion : promotions) {
                dtoList.add(convertToDto(promotion));
            }
            
            return CommonResult.success(dtoList);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> updatePromotion(Long id, CreatePromotionRequest request) {
        try {
            Promotion promotion = promotionMapper.selectById(id);
            if (promotion == null) {
                return CommonResult.error(ErrorCode.NOT_FOUND.getHttpCode(), "推广任务不存在");
            }
            
            // 只有草稿状态的任务才能编辑
            if (!"DRAFT".equals(promotion.getStatus())) {
                return CommonResult.error(ErrorCode.FORBIDDEN.getHttpCode(), "只有草稿状态的任务才能编辑");
            }
            
            // 更新字段
            promotion.setTitle(request.getTitle());
            promotion.setDescription(request.getDescription());
            promotion.setPlatform(request.getPlatform());
            promotion.setType("CONTENT"); // 固定为内容推广
            promotion.setUpdatedAt(new Date());
            
            int result = promotionMapper.update(promotion);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "更新推广任务失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> deletePromotion(Long id) {
        try {
            Promotion promotion = promotionMapper.selectById(id);
            if (promotion == null) {
                return CommonResult.error(ErrorCode.NOT_FOUND.getHttpCode(), "推广任务不存在");
            }
            
            // 只有草稿状态的任务才能删除
            if (!"DRAFT".equals(promotion.getStatus())) {
                return CommonResult.error(ErrorCode.FORBIDDEN.getHttpCode(), "只有草稿状态的任务才能删除");
            }
            
            int result = promotionMapper.deleteById(id);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "删除推广任务失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> submitForAudit(Long id) {
        try {
            Promotion promotion = promotionMapper.selectById(id);
            if (promotion == null) {
                return CommonResult.error(ErrorCode.NOT_FOUND.getHttpCode(), "推广任务不存在");
            }
            
            if (!"DRAFT".equals(promotion.getStatus())) {
                return CommonResult.error(ErrorCode.FORBIDDEN.getHttpCode(), "只有草稿状态的任务才能提交审核");
            }
            
            promotion.setStatus("SUBMITTED");
            promotion.setAuditStatus("PENDING_AUDIT");
            promotion.setUpdatedAt(new Date());
            
            int result = promotionMapper.update(promotion);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "提交审核失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> auditPromotion(Long id, String auditStatus, String auditComment, Long auditorId, String auditorName) {
        try {
            Promotion promotion = promotionMapper.selectById(id);
            if (promotion == null) {
                return CommonResult.error(ErrorCode.NOT_FOUND.getHttpCode(), "推广任务不存在");
            }
            
            if (!"PENDING_AUDIT".equals(promotion.getAuditStatus())) {
                return CommonResult.error(ErrorCode.FORBIDDEN.getHttpCode(), "只有待审核状态的任务才能审核");
            }
            
            promotion.setAuditStatus(auditStatus);
            promotion.setAuditComment(auditComment);
            promotion.setAuditorId(auditorId);
            promotion.setAuditorName(auditorName);
            promotion.setAuditedAt(new Date());
            promotion.setUpdatedAt(new Date());
            
            // 根据审核结果更新任务状态
            if ("APPROVED".equals(auditStatus)) {
                promotion.setStatus("APPROVED");
            } else if ("REJECTED".equals(auditStatus)) {
                promotion.setStatus("REJECTED");
                promotion.setRejectReason(auditComment);
            }
            
            int result = promotionMapper.update(promotion);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "审核失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> batchAuditPromotions(List<Long> ids, String auditStatus, Long auditorId, String auditorName) {
        try {
            int result = promotionMapper.batchUpdateAuditStatus(ids, auditStatus, auditorId, auditorName);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.error(ErrorCode.OPERATION_FAILED.getHttpCode(), "批量审核失败");
            }
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<PromotionDto>> getUserPromotions(Long creatorId) {
        try {
            List<Promotion> promotions = promotionMapper.selectByCreatorId(creatorId);
            List<PromotionDto> dtoList = new ArrayList<>();
            for (Promotion promotion : promotions) {
                dtoList.add(convertToDto(promotion));
            }
            return CommonResult.success(dtoList);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<PromotionDto>> getPendingAuditPromotions() {
        try {
            List<Promotion> promotions = promotionMapper.selectByAuditStatus("PENDING_AUDIT");
            List<PromotionDto> dtoList = new ArrayList<>();
            for (Promotion promotion : promotions) {
                dtoList.add(convertToDto(promotion));
            }
            return CommonResult.success(dtoList);
        } catch (Exception e) {
            return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpCode(), "系统错误: " + e.getMessage());
        }
    }
    
    /**
     * 转换为DTO
     */
    private PromotionDto convertToDto(Promotion entity) {
        PromotionDto dto = new PromotionDto();
        dto.setId(entity.getId());
        dto.setAgentId(entity.getCreatorId());
        dto.setTitle(entity.getTitle());
        dto.setPlatform(entity.getPlatform());
        dto.setAuditStatus(entity.getAuditStatus());
        
        if (entity.getCreatedAt() != null) {
            dto.setSubmittedAt(dateFormat.format(entity.getCreatedAt()));
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(dateFormat.format(entity.getUpdatedAt()));
        }
        
        return dto;
    }
    
    /**
     * 转换为审核DTO
     */
    private PromotionAuditDto convertToAuditDto(Promotion entity) {
        PromotionAuditDto dto = new PromotionAuditDto();
        dto.setId(entity.getId());
        dto.setAgentId(entity.getCreatorId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPlatform(entity.getPlatform());
        dto.setAuditStatus(entity.getAuditStatus());
        
        // 设置审核详情字段
        dto.setManualAuditComment(entity.getAuditComment());
        dto.setManualAuditorUsername(entity.getAuditorName());
        
        if (entity.getCreatedAt() != null) {
            dto.setSubmittedAt(dateFormat.format(entity.getCreatedAt()));
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(dateFormat.format(entity.getUpdatedAt()));
        }
        if (entity.getAuditedAt() != null) {
            dto.setManualAuditTime(dateFormat.format(entity.getAuditedAt()));
        }
        
        return dto;
    }
}