package com.example.lead.mapper;

import com.example.lead.entity.CustomerLead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户线索Mapper接口
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@Mapper
public interface CustomerLeadMapper {
    
    /**
     * 根据ID查询客资
     * 
     * @param id 客资ID
     * @return 客资信息
     */
    CustomerLead selectById(@Param("id") Long id);
    
    /**
     * 根据手机号查询客资
     * 
     * @param phone 手机号
     * @return 客资信息
     */
    CustomerLead selectByPhone(@Param("phone") String phone);
    
    /**
     * 分页查询客资列表
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @param salespersonId 销售ID (可选)
     * @param status 状态 (可选)
     * @return 客资列表
     */
    List<CustomerLead> selectLeads(@Param("offset") int offset, 
                                  @Param("limit") int limit,
                                  @Param("salespersonId") Long salespersonId,
                                  @Param("status") String status);
    
    /**
     * 查询客资总数
     * 
     * @param salespersonId 销售ID (可选)
     * @param status 状态 (可选)
     * @return 总数
     */
    int countLeads(@Param("salespersonId") Long salespersonId,
                   @Param("status") String status);
    
    /**
     * 插入客资
     * 
     * @param customerLead 客资信息
     * @return 影响行数
     */
    int insert(CustomerLead customerLead);
    
    /**
     * 更新客资
     * 
     * @param customerLead 客资信息
     * @return 影响行数
     */
    int update(CustomerLead customerLead);
    
    /**
     * 删除客资
     * 
     * @param id 客资ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量更新审核状态
     * 
     * @param ids 客资ID列表
     * @param auditStatus 审核状态
     * @return 影响行数
     */
    int batchUpdateAuditStatus(@Param("ids") List<Long> ids, 
                              @Param("auditStatus") String auditStatus);
}