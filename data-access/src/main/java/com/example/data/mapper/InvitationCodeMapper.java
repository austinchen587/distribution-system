package com.example.data.mapper;

import com.example.data.entity.InvitationCode;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 邀请码管理数据访问接口
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface InvitationCodeMapper {
    
    /**
     * 插入新邀请码
     * 
     * @param invitationCode 邀请码实体
     * @return 影响行数
     */
    @DataPermission(table = "invitation_codes", operation = OperationType.CREATE, description = "创建邀请码")
    @Insert("INSERT INTO invitation_codes (user_id, code, target_role, status, usage_count, max_uses, expires_at, " +
            "created_at, updated_at) VALUES (#{userId}, #{code}, #{targetRole}, #{status.code}, #{usageCount}, " +
            "#{maxUses}, #{expiresAt}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InvitationCode invitationCode);
    
    /**
     * 根据ID查找邀请码
     * 
     * @param id 邀请码ID
     * @return 邀请码实体
     */
    @Select("SELECT * FROM invitation_codes WHERE id = #{id}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<InvitationCode> findById(@Param("id") Long id);
    
    /**
     * 根据邀请码字符串查找邀请码
     * 
     * @param code 邀请码字符串
     * @return 邀请码实体
     */
    @Select("SELECT * FROM invitation_codes WHERE code = #{code}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<InvitationCode> findByCode(@Param("code") String code);
    
    /**
     * 根据创建者ID查找邀请码列表
     * 
     * @param userId 创建者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据目标角色查找邀请码列表
     * 
     * @param targetRole 目标角色
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes WHERE target_role = #{targetRole} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findByTargetRole(@Param("targetRole") String targetRole, 
                                         @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据状态查找邀请码列表
     * 
     * @param status 邀请码状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes WHERE status = #{status.code} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findByStatus(@Param("status") InvitationCode.InvitationCodeStatus status,
                                     @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找可用的邀请码（状态为ACTIVE且未过期且未达到使用上限）
     * 
     * @param currentTime 当前时间
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes WHERE status = 'ACTIVE' " +
            "AND (expires_at IS NULL OR expires_at > #{currentTime}) " +
            "AND (max_uses IS NULL OR usage_count < max_uses) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findAvailableCodes(@Param("currentTime") LocalDateTime currentTime,
                                           @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找已过期的邀请码
     * 
     * @param currentTime 当前时间
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes WHERE expires_at IS NOT NULL AND expires_at <= #{currentTime} " +
            "ORDER BY expires_at ASC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findExpiredCodes(@Param("currentTime") LocalDateTime currentTime,
                                         @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找使用次数已达上限的邀请码
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes WHERE max_uses IS NOT NULL AND usage_count >= max_uses " +
            "ORDER BY updated_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findExhaustedCodes(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有邀请码（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    @Select("SELECT * FROM invitation_codes ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<InvitationCode> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有目标角色
     * 
     * @return 角色列表
     */
    @Select("SELECT DISTINCT target_role FROM invitation_codes WHERE target_role IS NOT NULL ORDER BY target_role")
    List<String> findAllTargetRoles();
    
    /**
     * 统计邀请码总数
     * 
     * @return 邀请码总数
     */
    @Select("SELECT COUNT(*) FROM invitation_codes")
    long count();
    
    /**
     * 根据创建者ID统计邀请码数量
     * 
     * @param userId 创建者ID
     * @return 邀请码数量
     */
    @Select("SELECT COUNT(*) FROM invitation_codes WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 根据状态统计邀请码数量
     * 
     * @param status 邀请码状态
     * @return 邀请码数量
     */
    @Select("SELECT COUNT(*) FROM invitation_codes WHERE status = #{status.code}")
    long countByStatus(@Param("status") InvitationCode.InvitationCodeStatus status);
    
    /**
     * 根据目标角色统计邀请码数量
     * 
     * @param targetRole 目标角色
     * @return 邀请码数量
     */
    @Select("SELECT COUNT(*) FROM invitation_codes WHERE target_role = #{targetRole}")
    long countByTargetRole(@Param("targetRole") String targetRole);
    
    /**
     * 根据条件统计邀请码数量
     * 
     * @param userId 创建者ID（可选）
     * @param targetRole 目标角色（可选）
     * @param status 状态（可选）
     * @return 邀请码数量
     */
    long countByConditions(@Param("userId") Long userId, @Param("targetRole") String targetRole, 
                          @Param("status") String status);
    
    /**
     * 更新邀请码信息
     * 
     * @param invitationCode 邀请码实体
     * @return 影响行数
     */
    @Update("UPDATE invitation_codes SET user_id = #{userId}, code = #{code}, target_role = #{targetRole}, " +
            "status = #{status.code}, usage_count = #{usageCount}, max_uses = #{maxUses}, " +
            "expires_at = #{expiresAt}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(InvitationCode invitationCode);
    
    /**
     * 更新邀请码状态
     * 
     * @param id 邀请码ID
     * @param status 新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE invitation_codes SET status = #{status.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") InvitationCode.InvitationCodeStatus status,
                    @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 增加使用次数
     * 
     * @param id 邀请码ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE invitation_codes SET usage_count = usage_count + 1, updated_at = #{updatedAt} WHERE id = #{id}")
    int incrementUsage(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 激活邀请码
     * 
     * @param id 邀请码ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE invitation_codes SET status = 'ACTIVE', updated_at = #{updatedAt} WHERE id = #{id}")
    int activate(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 停用邀请码
     * 
     * @param id 邀请码ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE invitation_codes SET status = 'INACTIVE', updated_at = #{updatedAt} WHERE id = #{id}")
    int deactivate(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 批量更新邀请码状态
     * 
     * @param ids 邀请码ID列表
     * @param status 新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") InvitationCode.InvitationCodeStatus status,
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据ID删除邀请码（硬删除）
     * 
     * @param id 邀请码ID
     * @return 影响行数
     */
    @Delete("DELETE FROM invitation_codes WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查邀请码是否存在
     * 
     * @param code 邀请码字符串
     * @param excludeId 排除的邀请码ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM invitation_codes WHERE code = #{code} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByCode(@Param("code") String code, @Param("excludeId") Long excludeId);
    
    /**
     * 根据条件查找邀请码列表
     * 
     * @param userId 创建者ID（可选）
     * @param targetRole 目标角色（可选）
     * @param status 状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    List<InvitationCode> findByConditions(@Param("userId") Long userId, @Param("targetRole") String targetRole,
                                         @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 清理过期的邀请码（将状态改为INACTIVE）
     * 
     * @param currentTime 当前时间
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE invitation_codes SET status = 'INACTIVE', updated_at = #{updatedAt} " +
            "WHERE expires_at IS NOT NULL AND expires_at <= #{currentTime} AND status = 'ACTIVE'")
    int cleanupExpiredCodes(@Param("currentTime") LocalDateTime currentTime, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据关键词搜索邀请码
     * 
     * @param keyword 关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邀请码列表
     */
    List<InvitationCode> searchInvitationCodes(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 检查邀请码是否可以使用
     * 
     * @param code 邀请码字符串
     * @param currentTime 当前时间
     * @return 是否可以使用
     */
    @Select("SELECT COUNT(*) > 0 FROM invitation_codes WHERE code = #{code} AND status = 'ACTIVE' " +
            "AND (expires_at IS NULL OR expires_at > #{currentTime}) " +
            "AND (max_uses IS NULL OR usage_count < max_uses)")
    boolean canBeUsed(@Param("code") String code, @Param("currentTime") LocalDateTime currentTime);
}