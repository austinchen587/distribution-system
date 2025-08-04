package com.example.data.mapper;

import com.example.data.entity.Deal;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 成交记录数据访问接口
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface DealMapper {
    
    /**
     * 插入新成交记录
     * 
     * @param deal 成交记录实体
     * @return 影响行数
     */
    @DataPermission(table = "deals", operation = OperationType.CREATE, description = "创建成交记录")
    @Insert("INSERT INTO deals (customer_lead_id, product_id, sales_id, sales_owner_id, deal_amount, status, " +
            "deal_at, created_at, updated_at) VALUES (#{customerLeadId}, #{productId}, #{salesId}, #{salesOwnerId}, " +
            "#{dealAmount}, #{status.code}, #{dealAt}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Deal deal);
    
    /**
     * 根据ID查找成交记录
     * 
     * @param id 成交记录ID
     * @return 成交记录实体
     */
    @Select("SELECT * FROM deals WHERE id = #{id}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<Deal> findById(@Param("id") Long id);
    
    /**
     * 根据客户资源ID查找成交记录列表
     * 
     * @param customerLeadId 客户资源ID
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE customer_lead_id = #{customerLeadId} ORDER BY deal_at DESC")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findByCustomerLeadId(@Param("customerLeadId") Long customerLeadId);
    
    /**
     * 根据商品ID查找成交记录列表
     * 
     * @param productId 商品ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE product_id = #{productId} ORDER BY deal_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findByProductId(@Param("productId") Long productId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据销售员ID查找成交记录列表
     * 
     * @param salesId 销售员ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE sales_id = #{salesId} ORDER BY deal_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findBySalesId(@Param("salesId") Long salesId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据业绩归属者ID查找成交记录列表
     * 
     * @param salesOwnerId 业绩归属者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE sales_owner_id = #{salesOwnerId} ORDER BY deal_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findBySalesOwnerId(@Param("salesOwnerId") Long salesOwnerId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据交易状态查找成交记录列表
     * 
     * @param status 交易状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE status = #{status.code} ORDER BY deal_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findByStatus(@Param("status") Deal.DealStatus status, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据成交时间范围查找成交记录列表
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE deal_at >= #{startDate} AND deal_at <= #{endDate} " +
            "ORDER BY deal_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findByDealDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                  @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据成交金额范围查找成交记录列表
     * 
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals WHERE deal_amount >= #{minAmount} AND deal_amount <= #{maxAmount} " +
            "ORDER BY deal_amount DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount,
                                @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有成交记录（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    @Select("SELECT * FROM deals ORDER BY deal_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Deal> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计成交记录总数
     * 
     * @return 成交记录总数
     */
    @Select("SELECT COUNT(*) FROM deals")
    long count();
    
    /**
     * 根据销售员ID统计成交记录数量
     * 
     * @param salesId 销售员ID
     * @return 成交记录数量
     */
    @Select("SELECT COUNT(*) FROM deals WHERE sales_id = #{salesId}")
    long countBySalesId(@Param("salesId") Long salesId);
    
    /**
     * 根据业绩归属者ID统计成交记录数量
     * 
     * @param salesOwnerId 业绩归属者ID
     * @return 成交记录数量
     */
    @Select("SELECT COUNT(*) FROM deals WHERE sales_owner_id = #{salesOwnerId}")
    long countBySalesOwnerId(@Param("salesOwnerId") Long salesOwnerId);
    
    /**
     * 根据交易状态统计成交记录数量
     * 
     * @param status 交易状态
     * @return 成交记录数量
     */
    @Select("SELECT COUNT(*) FROM deals WHERE status = #{status.code}")
    long countByStatus(@Param("status") Deal.DealStatus status);
    
    /**
     * 根据条件统计成交记录数量
     * 
     * @param salesId 销售员ID（可选）
     * @param productId 商品ID（可选）
     * @param status 交易状态（可选）
     * @param startDate 开始时间（可选）
     * @param endDate 结束时间（可选）
     * @return 成交记录数量
     */
    long countByConditions(@Param("salesId") Long salesId, @Param("productId") Long productId,
                          @Param("status") String status, @Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);
    
    /**
     * 计算销售员的成交总额
     * 
     * @param salesId 销售员ID
     * @param status 交易状态（只统计已完成的）
     * @return 成交总额
     */
    @Select("SELECT COALESCE(SUM(deal_amount), 0) FROM deals WHERE sales_id = #{salesId} AND status = #{status.code}")
    BigDecimal sumAmountBySalesId(@Param("salesId") Long salesId, @Param("status") Deal.DealStatus status);
    
    /**
     * 计算业绩归属者的成交总额
     * 
     * @param salesOwnerId 业绩归属者ID
     * @param status 交易状态（只统计已完成的）
     * @return 成交总额
     */
    @Select("SELECT COALESCE(SUM(deal_amount), 0) FROM deals WHERE sales_owner_id = #{salesOwnerId} AND status = #{status.code}")
    BigDecimal sumAmountBySalesOwnerId(@Param("salesOwnerId") Long salesOwnerId, @Param("status") Deal.DealStatus status);
    
    /**
     * 计算商品的成交总额
     * 
     * @param productId 商品ID
     * @param status 交易状态（只统计已完成的）
     * @return 成交总额
     */
    @Select("SELECT COALESCE(SUM(deal_amount), 0) FROM deals WHERE product_id = #{productId} AND status = #{status.code}")
    BigDecimal sumAmountByProductId(@Param("productId") Long productId, @Param("status") Deal.DealStatus status);
    
    /**
     * 更新成交记录信息
     * 
     * @param deal 成交记录实体
     * @return 影响行数
     */
    @Update("UPDATE deals SET customer_lead_id = #{customerLeadId}, product_id = #{productId}, sales_id = #{salesId}, " +
            "sales_owner_id = #{salesOwnerId}, deal_amount = #{dealAmount}, status = #{status.code}, " +
            "deal_at = #{dealAt}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Deal deal);
    
    /**
     * 更新交易状态
     * 
     * @param id 成交记录ID
     * @param status 新交易状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE deals SET status = #{status.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Deal.DealStatus status,
                    @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新业绩归属者
     * 
     * @param id 成交记录ID
     * @param salesOwnerId 新业绩归属者ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE deals SET sales_owner_id = #{salesOwnerId}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateSalesOwner(@Param("id") Long id, @Param("salesOwnerId") Long salesOwnerId,
                        @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据ID删除成交记录（硬删除）
     * 
     * @param id 成交记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM deals WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据条件查找成交记录列表
     * 
     * @param salesId 销售员ID（可选）
     * @param productId 商品ID（可选）
     * @param status 交易状态（可选）
     * @param startDate 开始时间（可选）
     * @param endDate 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 成交记录列表
     */
    List<Deal> findByConditions(@Param("salesId") Long salesId, @Param("productId") Long productId,
                               @Param("status") String status, @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 批量更新交易状态
     * 
     * @param ids 成交记录ID列表
     * @param status 新交易状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Deal.DealStatus status,
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 完成交易
     * 
     * @param id 成交记录ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE deals SET status = 'COMPLETED', updated_at = #{updatedAt} WHERE id = #{id}")
    int complete(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 退款交易
     * 
     * @param id 成交记录ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE deals SET status = 'REFUNDED', updated_at = #{updatedAt} WHERE id = #{id}")
    int refund(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据时间范围统计成交总额
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param status 交易状态
     * @return 成交总额
     */
    @Select("SELECT COALESCE(SUM(deal_amount), 0) FROM deals WHERE deal_at >= #{startDate} AND deal_at <= #{endDate} " +
            "AND status = #{status.code}")
    BigDecimal sumAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                   @Param("status") Deal.DealStatus status);
}