package com.example.promotion.mapper;

import com.example.promotion.entity.Promotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推广任务数据访问层
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@Mapper
public interface PromotionMapper {
    
    /**
     * 插入推广任务
     */
    int insert(Promotion promotion);
    
    /**
     * 根据ID查询推广任务
     */
    Promotion selectById(Long id);
    
    /**
     * 分页查询推广任务列表
     */
    List<Promotion> selectPromotions(@Param("offset") int offset, 
                                   @Param("size") int size,
                                   @Param("creatorId") Long creatorId,
                                   @Param("status") String status,
                                   @Param("auditStatus") String auditStatus);
    
    /**
     * 统计推广任务数量
     */
    int countPromotions(@Param("creatorId") Long creatorId,
                       @Param("status") String status,
                       @Param("auditStatus") String auditStatus);
    
    /**
     * 更新推广任务
     */
    int update(Promotion promotion);
    
    /**
     * 删除推广任务
     */
    int deleteById(Long id);
    
    /**
     * 批量更新审核状态
     */
    int batchUpdateAuditStatus(@Param("ids") List<Long> ids, 
                              @Param("auditStatus") String auditStatus,
                              @Param("auditorId") Long auditorId,
                              @Param("auditorName") String auditorName);
    
    /**
     * 根据创建者ID查询推广任务
     */
    List<Promotion> selectByCreatorId(Long creatorId);
    
    /**
     * 根据审核状态查询推广任务
     */
    List<Promotion> selectByAuditStatus(String auditStatus);
}