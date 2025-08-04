package com.example.data.mapper;

import com.example.data.entity.Promotion;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 推广任务数据访问接口
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface PromotionMapper {
    
    /**
     * 插入新推广任务
     * 
     * @param promotion 推广任务实体
     * @return 影响行数
     */
    @DataPermission(table = "promotions", operation = OperationType.CREATE, description = "创建推广任务")
    @Insert("INSERT INTO promotions (agent_id, title, description, platform, content_url, tags, expected_reward, " +
            "actual_reward, audit_status, submitted_at, updated_at) VALUES (#{agentId}, #{title}, #{description}, " +
            "#{platform}, #{contentUrl}, #{tags}, #{expectedReward}, #{actualReward}, #{auditStatus.code}, " +
            "#{submittedAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Promotion promotion);
    
    /**
     * 根据ID查找推广任务
     * 
     * @param id 推广任务ID
     * @return 推广任务实体
     */
    @Select("SELECT * FROM promotions WHERE id = #{id}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<Promotion> findById(@Param("id") Long id);
    
    /**
     * 根据代理ID查找推广任务列表
     * 
     * @param agentId 代理ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE agent_id = #{agentId} ORDER BY submitted_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findByAgentId(@Param("agentId") Long agentId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据审核状态查找推广任务列表
     * 
     * @param auditStatus 审核状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE audit_status = #{auditStatus.code} ORDER BY submitted_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findByAuditStatus(@Param("auditStatus") Promotion.PromotionAuditStatus auditStatus,
                                     @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据平台查找推广任务列表
     * 
     * @param platform 推广平台
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE platform = #{platform} ORDER BY submitted_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findByPlatform(@Param("platform") String platform, 
                                  @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找待机器审核的推广任务
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE audit_status = 'PENDING_MACHINE_AUDIT' ORDER BY submitted_at ASC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findPendingMachineAudit(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找待人工审核的推广任务
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE audit_status = 'PENDING_MANUAL_AUDIT' ORDER BY submitted_at ASC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findPendingManualAudit(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找已通过审核的推广任务
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE audit_status = 'APPROVED' ORDER BY submitted_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findApproved(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有推广任务（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions ORDER BY submitted_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有平台
     * 
     * @return 平台列表
     */
    @Select("SELECT DISTINCT platform FROM promotions WHERE platform IS NOT NULL ORDER BY platform")
    List<String> findAllPlatforms();
    
    /**
     * 统计推广任务总数
     * 
     * @return 推广任务总数
     */
    @Select("SELECT COUNT(*) FROM promotions")
    long count();
    
    /**
     * 根据代理ID统计推广任务数量
     * 
     * @param agentId 代理ID
     * @return 推广任务数量
     */
    @Select("SELECT COUNT(*) FROM promotions WHERE agent_id = #{agentId}")
    long countByAgentId(@Param("agentId") Long agentId);
    
    /**
     * 根据审核状态统计推广任务数量
     * 
     * @param auditStatus 审核状态
     * @return 推广任务数量
     */
    @Select("SELECT COUNT(*) FROM promotions WHERE audit_status = #{auditStatus.code}")
    long countByAuditStatus(@Param("auditStatus") Promotion.PromotionAuditStatus auditStatus);
    
    /**
     * 根据条件统计推广任务数量
     * 
     * @param agentId 代理ID（可选）
     * @param platform 平台（可选）
     * @param auditStatus 审核状态（可选）
     * @return 推广任务数量
     */
    long countByConditions(@Param("agentId") Long agentId, @Param("platform") String platform, 
                          @Param("auditStatus") String auditStatus);
    
    /**
     * 更新推广任务信息
     * 
     * @param promotion 推广任务实体
     * @return 影响行数
     */
    @Update("UPDATE promotions SET agent_id = #{agentId}, title = #{title}, description = #{description}, " +
            "platform = #{platform}, content_url = #{contentUrl}, tags = #{tags}, expected_reward = #{expectedReward}, " +
            "actual_reward = #{actualReward}, audit_status = #{auditStatus.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Promotion promotion);
    
    /**
     * 更新审核状态
     * 
     * @param id 推广任务ID
     * @param auditStatus 新审核状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE promotions SET audit_status = #{auditStatus.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus") Promotion.PromotionAuditStatus auditStatus,
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新实际奖励
     * 
     * @param id 推广任务ID
     * @param actualReward 实际奖励
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE promotions SET actual_reward = #{actualReward}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateActualReward(@Param("id") Long id, @Param("actualReward") BigDecimal actualReward,
                          @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 批量更新审核状态
     * 
     * @param ids 推广任务ID列表
     * @param auditStatus 新审核状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int batchUpdateAuditStatus(@Param("ids") List<Long> ids, @Param("auditStatus") Promotion.PromotionAuditStatus auditStatus,
                              @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据ID删除推广任务（硬删除）
     * 
     * @param id 推广任务ID
     * @return 影响行数
     */
    @Delete("DELETE FROM promotions WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查内容URL是否存在
     * 
     * @param contentUrl 内容URL
     * @param excludeId 排除的推广任务ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM promotions WHERE content_url = #{contentUrl} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByContentUrl(@Param("contentUrl") String contentUrl, @Param("excludeId") Long excludeId);
    
    /**
     * 根据关键词搜索推广任务
     * 
     * @param keyword 关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    List<Promotion> searchPromotions(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据条件查找推广任务列表
     * 
     * @param agentId 代理ID（可选）
     * @param platform 平台（可选）
     * @param auditStatus 审核状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    List<Promotion> findByConditions(@Param("agentId") Long agentId, @Param("platform") String platform,
                                    @Param("auditStatus") String auditStatus, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据时间范围查找推广任务
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 推广任务列表
     */
    @Select("SELECT * FROM promotions WHERE submitted_at >= #{startDate} AND submitted_at <= #{endDate} " +
            "ORDER BY submitted_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "auditStatus", column = "audit_status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Promotion> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                   @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计代理的奖励总额
     * 
     * @param agentId 代理ID
     * @param auditStatus 审核状态（只统计已通过的）
     * @return 奖励总额
     */
    @Select("SELECT COALESCE(SUM(actual_reward), 0) FROM promotions WHERE agent_id = #{agentId} AND audit_status = #{auditStatus.code}")
    BigDecimal sumRewardByAgentId(@Param("agentId") Long agentId, @Param("auditStatus") Promotion.PromotionAuditStatus auditStatus);
}