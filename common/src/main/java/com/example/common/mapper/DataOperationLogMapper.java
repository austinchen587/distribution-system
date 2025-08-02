package com.example.common.mapper;

import com.example.common.entity.DataOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据操作审计日志数据访问层接口
 * 
 * <p>负责数据操作审计日志表（data_operation_logs）的数据访问操作，
 * 提供审计日志的记录、查询、统计功能。使用 MyBatis 框架实现数据持久化，
 * 对应的 SQL 映射文件为 DataOperationLogMapper.xml。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>审计日志的记录和批量记录</li>
 *   <li>基于多维度条件的日志查询</li>
 *   <li>日志统计和分析功能</li>
 *   <li>日志清理和归档管理</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>数据操作拦截器记录审计日志</li>
 *   <li>安全审计和合规检查</li>
 *   <li>系统性能监控和分析</li>
 *   <li>数据变更历史追踪</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Mapper
public interface DataOperationLogMapper {
    
    /**
     * 根据ID查询审计日志
     *
     * @param id 日志ID
     * @return 审计日志信息
     */
    DataOperationLog selectById(@Param("id") Long id);
    
    /**
     * 根据请求ID查询相关的所有审计日志
     *
     * @param requestId 请求唯一标识
     * @return 审计日志列表
     */
    List<DataOperationLog> selectByRequestId(@Param("requestId") String requestId);
    
    /**
     * 查询指定服务的审计日志
     *
     * @param serviceName 微服务名称
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 审计日志列表
     */
    List<DataOperationLog> selectByService(@Param("serviceName") String serviceName,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("limit") Integer limit);
    
    /**
     * 查询指定用户的审计日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 审计日志列表
     */
    List<DataOperationLog> selectByUser(@Param("userId") Long userId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       @Param("limit") Integer limit);
    
    /**
     * 查询指定表的审计日志
     *
     * @param tableName 数据表名称
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 审计日志列表
     */
    List<DataOperationLog> selectByTable(@Param("tableName") String tableName,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime,
                                        @Param("limit") Integer limit);
    
    /**
     * 分页查询审计日志
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @param serviceName 服务名称（可选）
     * @param tableName 表名称（可选）
     * @param operationType 操作类型（可选）
     * @param status 操作状态（可选）
     * @param userId 用户ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 审计日志列表
     */
    List<DataOperationLog> selectWithPagination(@Param("offset") int offset,
                                               @Param("limit") int limit,
                                               @Param("serviceName") String serviceName,
                                               @Param("tableName") String tableName,
                                               @Param("operationType") String operationType,
                                               @Param("status") String status,
                                               @Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计审计日志总数
     *
     * @param serviceName 服务名称（可选）
     * @param tableName 表名称（可选）
     * @param operationType 操作类型（可选）
     * @param status 操作状态（可选）
     * @param userId 用户ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 审计日志总数
     */
    int countLogs(@Param("serviceName") String serviceName,
                  @Param("tableName") String tableName,
                  @Param("operationType") String operationType,
                  @Param("status") String status,
                  @Param("userId") Long userId,
                  @Param("startTime") LocalDateTime startTime,
                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 插入审计日志
     *
     * @param log 审计日志信息
     * @return 影响行数
     */
    int insert(DataOperationLog log);
    
    /**
     * 批量插入审计日志
     *
     * @param logs 审计日志列表
     * @return 影响行数
     */
    int batchInsert(@Param("logs") List<DataOperationLog> logs);
    
    /**
     * 删除指定时间之前的审计日志
     *
     * @param beforeTime 时间阈值
     * @return 删除的记录数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 删除指定服务的审计日志
     *
     * @param serviceName 微服务名称
     * @return 删除的记录数
     */
    int deleteByService(@Param("serviceName") String serviceName);
    
    /**
     * 查询慢查询日志
     *
     * @param threshold 执行时间阈值（毫秒）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 慢查询日志列表
     */
    List<DataOperationLog> selectSlowQueries(@Param("threshold") int threshold,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("limit") Integer limit);
    
    /**
     * 查询失败的操作日志
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 失败操作日志列表
     */
    List<DataOperationLog> selectFailedOperations(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("limit") Integer limit);
    
    /**
     * 查询被拒绝的操作日志
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 被拒绝操作日志列表
     */
    List<DataOperationLog> selectDeniedOperations(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("limit") Integer limit);
    
    /**
     * 统计操作类型分布
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 操作类型统计（Map格式：操作类型 -> 数量）
     */
    List<Map<String, Object>> getOperationTypeStats(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计服务操作分布
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 服务操作统计（Map格式：服务名 -> 数量）
     */
    List<Map<String, Object>> getServiceOperationStats(@Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计操作状态分布
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 操作状态统计（Map格式：状态 -> 数量）
     */
    List<Map<String, Object>> getOperationStatusStats(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计每小时操作数量
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 每小时操作统计（Map格式：小时 -> 数量）
     */
    List<Map<String, Object>> getHourlyOperationStats(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取平均执行时间统计
     *
     * @param serviceName 服务名称（可选）
     * @param tableName 表名称（可选）
     * @param operationType 操作类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 平均执行时间（毫秒）
     */
    Double getAverageExecutionTime(@Param("serviceName") String serviceName,
                                  @Param("tableName") String tableName,
                                  @Param("operationType") String operationType,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);
}
