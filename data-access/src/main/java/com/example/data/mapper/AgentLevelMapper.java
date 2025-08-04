package com.example.data.mapper;

import com.example.data.entity.AgentLevel;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 代理级别数据访问接口
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface AgentLevelMapper {
    
    /**
     * 插入新代理级别
     * 
     * @param agentLevel 代理级别实体
     * @return 影响行数
     */
    @DataPermission(table = "agent_levels", operation = OperationType.CREATE, description = "创建代理级别")
    @Insert("INSERT INTO agent_levels (level_name, min_gmv, max_gmv, commission_rate, description, created_at, updated_at) " +
            "VALUES (#{levelName}, #{minGmv}, #{maxGmv}, #{commissionRate}, #{description}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AgentLevel agentLevel);
    
    /**
     * 根据ID查找代理级别
     * 
     * @param id 代理级别ID
     * @return 代理级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "根据ID查询代理级别")
    @Select("SELECT * FROM agent_levels WHERE id = #{id}")
    Optional<AgentLevel> findById(@Param("id") Long id);
    
    /**
     * 根据级别名称查找代理级别
     * 
     * @param levelName 级别名称
     * @return 代理级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "根据名称查询代理级别")
    @Select("SELECT * FROM agent_levels WHERE level_name = #{levelName}")
    Optional<AgentLevel> findByLevelName(@Param("levelName") String levelName);
    
    /**
     * 查找所有代理级别（按GMV升序）
     * 
     * @return 代理级别列表
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "查询所有代理级别")
    @Select("SELECT * FROM agent_levels ORDER BY min_gmv ASC")
    List<AgentLevel> findAll();
    
    /**
     * 根据GMV查找对应的代理级别
     * 
     * @param gmv GMV金额
     * @return 代理级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "根据GMV查询代理级别")
    @Select("SELECT * FROM agent_levels WHERE #{gmv} >= min_gmv AND (max_gmv IS NULL OR #{gmv} <= max_gmv) ORDER BY min_gmv DESC LIMIT 1")
    Optional<AgentLevel> findByGmv(@Param("gmv") BigDecimal gmv);
    
    /**
     * 查找入门级别（最低GMV要求）
     * 
     * @return 入门级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "查询入门级别")
    @Select("SELECT * FROM agent_levels ORDER BY min_gmv ASC LIMIT 1")
    Optional<AgentLevel> findEntryLevel();
    
    /**
     * 查找最高级别（最高GMV要求）
     * 
     * @return 最高级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "查询最高级别")
    @Select("SELECT * FROM agent_levels ORDER BY min_gmv DESC LIMIT 1")
    Optional<AgentLevel> findTopLevel();
    
    /**
     * 统计代理级别总数
     * 
     * @return 代理级别总数
     */
    @DataPermission(table = "agent_levels", operation = OperationType.STATS, description = "统计代理级别总数")
    @Select("SELECT COUNT(*) FROM agent_levels")
    long count();
    
    /**
     * 更新代理级别信息
     * 
     * @param agentLevel 代理级别实体
     * @return 影响行数
     */
    @DataPermission(table = "agent_levels", operation = OperationType.UPDATE, description = "更新代理级别信息")
    @Update("UPDATE agent_levels SET level_name = #{levelName}, min_gmv = #{minGmv}, max_gmv = #{maxGmv}, " +
            "commission_rate = #{commissionRate}, description = #{description}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(AgentLevel agentLevel);
    
    /**
     * 更新佣金率
     * 
     * @param id 代理级别ID
     * @param commissionRate 新佣金率
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @DataPermission(table = "agent_levels", operation = OperationType.UPDATE, description = "更新代理级别佣金率")
    @Update("UPDATE agent_levels SET commission_rate = #{commissionRate}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateCommissionRate(@Param("id") Long id, @Param("commissionRate") BigDecimal commissionRate, 
                            @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据ID删除代理级别（硬删除）
     * 
     * @param id 代理级别ID
     * @return 影响行数
     */
    @DataPermission(table = "agent_levels", operation = OperationType.DELETE, description = "删除代理级别")
    @Delete("DELETE FROM agent_levels WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查级别名称是否存在
     * 
     * @param levelName 级别名称
     * @param excludeId 排除的级别ID（用于更新时检查）
     * @return 是否存在
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "检查级别名称是否存在")
    @Select("SELECT COUNT(*) > 0 FROM agent_levels WHERE level_name = #{levelName} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByLevelName(@Param("levelName") String levelName, @Param("excludeId") Long excludeId);
    
    /**
     * 检查GMV范围是否冲突
     * 
     * @param minGmv 最小GMV
     * @param maxGmv 最大GMV
     * @param excludeId 排除的级别ID（用于更新时检查）
     * @return 是否冲突
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "检查GMV范围是否冲突")
    boolean checkGmvRangeConflict(@Param("minGmv") BigDecimal minGmv, @Param("maxGmv") BigDecimal maxGmv, 
                                 @Param("excludeId") Long excludeId);
    
    /**
     * 根据佣金率范围查找代理级别
     * 
     * @param minRate 最小佣金率
     * @param maxRate 最大佣金率
     * @return 代理级别列表
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "按佣金率范围查询代理级别")
    @Select("SELECT * FROM agent_levels WHERE commission_rate >= #{minRate} AND commission_rate <= #{maxRate} " +
            "ORDER BY commission_rate ASC")
    List<AgentLevel> findByCommissionRateRange(@Param("minRate") BigDecimal minRate, @Param("maxRate") BigDecimal maxRate);
    
    /**
     * 查找高于指定GMV的下一个级别
     * 
     * @param currentGmv 当前GMV
     * @return 下一个级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "查询下一个级别")
    @Select("SELECT * FROM agent_levels WHERE min_gmv > #{currentGmv} ORDER BY min_gmv ASC LIMIT 1")
    Optional<AgentLevel> findNextLevel(@Param("currentGmv") BigDecimal currentGmv);
    
    /**
     * 查找低于指定GMV的上一个级别
     * 
     * @param currentGmv 当前GMV
     * @return 上一个级别实体
     */
    @DataPermission(table = "agent_levels", operation = OperationType.READ, description = "查询上一个级别")
    @Select("SELECT * FROM agent_levels WHERE min_gmv < #{currentGmv} ORDER BY min_gmv DESC LIMIT 1")
    Optional<AgentLevel> findPreviousLevel(@Param("currentGmv") BigDecimal currentGmv);
}