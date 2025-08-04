package com.example.data.mapper;

import com.example.data.entity.Commission;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 佣金记录数据访问接口
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface CommissionMapper {
    
    /**
     * 插入新佣金记录
     * 
     * @param commission 佣金记录实体
     * @return 影响行数
     */
    @DataPermission(table = "commissions", operation = OperationType.CREATE, description = "创建佣金记录")
    @Insert("INSERT INTO commissions (user_id, deal_id, commission_level, commission_rate, commission_amount, " +
            "settlement_month, status, created_at, updated_at) VALUES (#{userId}, #{dealId}, #{commissionLevel.code}, " +
            "#{commissionRate}, #{commissionAmount}, #{settlementMonth}, #{status.code}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Commission commission);
    
    /**
     * 根据ID查找佣金记录
     * 
     * @param id 佣金记录ID
     * @return 佣金记录实体
     */
    @Select("SELECT * FROM commissions WHERE id = #{id}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<Commission> findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查找佣金记录列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据成交记录ID查找佣金记录列表
     * 
     * @param dealId 成交记录ID
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE deal_id = #{dealId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findByDealId(@Param("dealId") Long dealId);
    
    /**
     * 根据佣金类型查找佣金记录列表
     * 
     * @param commissionLevel 佣金类型
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE commission_level = #{commissionLevel.code} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findByCommissionLevel(@Param("commissionLevel") Commission.CommissionLevel commissionLevel,
                                          @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据结算状态查找佣金记录列表
     * 
     * @param status 结算状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE status = #{status.code} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findByStatus(@Param("status") Commission.SettlementStatus status,
                                 @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据结算月份查找佣金记录列表
     * 
     * @param settlementMonth 结算月份（格式：YYYY-MM）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE settlement_month = #{settlementMonth} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findBySettlementMonth(@Param("settlementMonth") String settlementMonth,
                                          @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找待结算的佣金记录
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findPendingSettlement(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找已支付的佣金记录
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE status = 'PAID' ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findPaidCommissions(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有佣金记录（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计佣金记录总数
     * 
     * @return 佣金记录总数
     */
    @Select("SELECT COUNT(*) FROM commissions")
    long count();
    
    /**
     * 根据用户ID统计佣金记录数量
     * 
     * @param userId 用户ID
     * @return 佣金记录数量
     */
    @Select("SELECT COUNT(*) FROM commissions WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 根据结算状态统计佣金记录数量
     * 
     * @param status 结算状态
     * @return 佣金记录数量
     */
    @Select("SELECT COUNT(*) FROM commissions WHERE status = #{status.code}")
    long countByStatus(@Param("status") Commission.SettlementStatus status);
    
    /**
     * 根据结算月份统计佣金记录数量
     * 
     * @param settlementMonth 结算月份
     * @return 佣金记录数量
     */
    @Select("SELECT COUNT(*) FROM commissions WHERE settlement_month = #{settlementMonth}")
    long countBySettlementMonth(@Param("settlementMonth") String settlementMonth);
    
    /**
     * 根据条件统计佣金记录数量
     * 
     * @param userId 用户ID（可选）
     * @param commissionLevel 佣金类型（可选）
     * @param status 结算状态（可选）
     * @param settlementMonth 结算月份（可选）
     * @return 佣金记录数量
     */
    long countByConditions(@Param("userId") Long userId, @Param("commissionLevel") String commissionLevel,
                          @Param("status") String status, @Param("settlementMonth") String settlementMonth);
    
    /**
     * 计算用户的佣金总额
     * 
     * @param userId 用户ID
     * @param status 结算状态（可选）
     * @return 佣金总额
     */
    BigDecimal sumCommissionByUserId(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 计算结算月份的佣金总额
     * 
     * @param settlementMonth 结算月份
     * @param status 结算状态（可选）
     * @return 佣金总额
     */
    @Select("SELECT COALESCE(SUM(commission_amount), 0) FROM commissions WHERE settlement_month = #{settlementMonth} " +
            "AND (#{status} IS NULL OR status = #{status})")
    BigDecimal sumCommissionBySettlementMonth(@Param("settlementMonth") String settlementMonth, @Param("status") String status);
    
    /**
     * 更新佣金记录信息
     * 
     * @param commission 佣金记录实体
     * @return 影响行数
     */
    @Update("UPDATE commissions SET user_id = #{userId}, deal_id = #{dealId}, commission_level = #{commissionLevel.code}, " +
            "commission_rate = #{commissionRate}, commission_amount = #{commissionAmount}, " +
            "settlement_month = #{settlementMonth}, status = #{status.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Commission commission);
    
    /**
     * 更新结算状态
     * 
     * @param id 佣金记录ID
     * @param status 新结算状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE commissions SET status = #{status.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Commission.SettlementStatus status,
                    @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 标记为已支付
     * 
     * @param id 佣金记录ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE commissions SET status = 'PAID', updated_at = #{updatedAt} WHERE id = #{id}")
    int markAsPaid(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 取消佣金
     * 
     * @param id 佣金记录ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE commissions SET status = 'CANCELLED', updated_at = #{updatedAt} WHERE id = #{id}")
    int cancel(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 批量更新结算状态
     * 
     * @param ids 佣金记录ID列表
     * @param status 新结算状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Commission.SettlementStatus status,
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 批量标记为已支付
     * 
     * @param ids 佣金记录ID列表
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int batchMarkAsPaid(@Param("ids") List<Long> ids, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据ID删除佣金记录（硬删除）
     * 
     * @param id 佣金记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM commissions WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据条件查找佣金记录列表
     * 
     * @param userId 用户ID（可选）
     * @param commissionLevel 佣金类型（可选）
     * @param status 结算状态（可选）
     * @param settlementMonth 结算月份（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    List<Commission> findByConditions(@Param("userId") Long userId, @Param("commissionLevel") String commissionLevel,
                                     @Param("status") String status, @Param("settlementMonth") String settlementMonth,
                                     @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找直接佣金记录
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE user_id = #{userId} AND commission_level = 'DIRECT' " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findDirectCommissions(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找间接佣金记录
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM commissions WHERE user_id = #{userId} AND commission_level = 'INDIRECT' " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "commissionLevel", column = "commission_level", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Commission> findIndirectCommissions(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
}