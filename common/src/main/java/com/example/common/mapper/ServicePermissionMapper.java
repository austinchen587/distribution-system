package com.example.common.mapper;

import com.example.common.entity.ServiceDataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务数据权限配置数据访问层接口
 * 
 * <p>负责服务数据权限配置表（service_data_permissions）的数据访问操作，
 * 提供权限配置的增删改查功能。使用 MyBatis 框架实现数据持久化，
 * 对应的 SQL 映射文件为 ServicePermissionMapper.xml。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>权限配置的基本CRUD操作</li>
 *   <li>基于服务名、表名、操作类型的权限查询</li>
 *   <li>权限配置的批量操作</li>
 *   <li>权限配置的启用/禁用管理</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>权限检查器查询权限配置</li>
 *   <li>权限管理界面的配置操作</li>
 *   <li>权限配置的批量导入导出</li>
 *   <li>权限配置的审计和监控</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Mapper
public interface ServicePermissionMapper {
    
    /**
     * 根据ID查询权限配置
     *
     * @param id 权限配置ID
     * @return 权限配置信息
     */
    ServiceDataPermission selectById(@Param("id") Long id);
    
    /**
     * 根据服务名、表名、操作类型查询权限配置
     * 这是权限检查的核心方法
     *
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @return 权限配置信息，如果不存在返回null
     */
    ServiceDataPermission findPermission(@Param("serviceName") String serviceName,
                                        @Param("tableName") String tableName,
                                        @Param("operationType") String operationType);
    
    /**
     * 查询指定服务的所有权限配置
     *
     * @param serviceName 微服务名称
     * @return 权限配置列表
     */
    List<ServiceDataPermission> selectByServiceName(@Param("serviceName") String serviceName);
    
    /**
     * 查询指定表的所有权限配置
     *
     * @param tableName 数据表名称
     * @return 权限配置列表
     */
    List<ServiceDataPermission> selectByTableName(@Param("tableName") String tableName);
    
    /**
     * 查询指定服务和表的所有权限配置
     *
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @return 权限配置列表
     */
    List<ServiceDataPermission> selectByServiceAndTable(@Param("serviceName") String serviceName,
                                                        @Param("tableName") String tableName);
    
    /**
     * 查询所有启用的权限配置
     *
     * @return 启用的权限配置列表
     */
    List<ServiceDataPermission> selectAllEnabled();
    
    /**
     * 分页查询权限配置
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @param serviceName 服务名称（可选）
     * @param tableName 表名称（可选）
     * @param isEnabled 是否启用（可选）
     * @return 权限配置列表
     */
    List<ServiceDataPermission> selectWithPagination(@Param("offset") int offset,
                                                     @Param("limit") int limit,
                                                     @Param("serviceName") String serviceName,
                                                     @Param("tableName") String tableName,
                                                     @Param("isEnabled") Boolean isEnabled);
    
    /**
     * 统计权限配置总数
     *
     * @param serviceName 服务名称（可选）
     * @param tableName 表名称（可选）
     * @param isEnabled 是否启用（可选）
     * @return 权限配置总数
     */
    int countPermissions(@Param("serviceName") String serviceName,
                        @Param("tableName") String tableName,
                        @Param("isEnabled") Boolean isEnabled);
    
    /**
     * 插入权限配置
     *
     * @param permission 权限配置信息
     * @return 影响行数
     */
    int insert(ServiceDataPermission permission);
    
    /**
     * 批量插入权限配置
     *
     * @param permissions 权限配置列表
     * @return 影响行数
     */
    int batchInsert(@Param("permissions") List<ServiceDataPermission> permissions);
    
    /**
     * 更新权限配置
     *
     * @param permission 权限配置信息
     * @return 影响行数
     */
    int update(ServiceDataPermission permission);
    
    /**
     * 更新权限配置的启用状态
     *
     * @param id 权限配置ID
     * @param isEnabled 是否启用
     * @param updatedBy 更新人ID
     * @return 影响行数
     */
    int updateEnabledStatus(@Param("id") Long id,
                           @Param("isEnabled") Boolean isEnabled,
                           @Param("updatedBy") Long updatedBy);
    
    /**
     * 批量更新权限配置的启用状态
     *
     * @param ids 权限配置ID列表
     * @param isEnabled 是否启用
     * @param updatedBy 更新人ID
     * @return 影响行数
     */
    int batchUpdateEnabledStatus(@Param("ids") List<Long> ids,
                                @Param("isEnabled") Boolean isEnabled,
                                @Param("updatedBy") Long updatedBy);
    
    /**
     * 根据ID删除权限配置
     *
     * @param id 权限配置ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除权限配置
     *
     * @param ids 权限配置ID列表
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 删除指定服务的所有权限配置
     *
     * @param serviceName 微服务名称
     * @return 影响行数
     */
    int deleteByServiceName(@Param("serviceName") String serviceName);
    
    /**
     * 检查权限配置是否存在
     *
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @return 是否存在
     */
    boolean existsPermission(@Param("serviceName") String serviceName,
                            @Param("tableName") String tableName,
                            @Param("operationType") String operationType);
    
    /**
     * 获取所有不同的服务名称
     *
     * @return 服务名称列表
     */
    List<String> selectDistinctServiceNames();
    
    /**
     * 获取所有不同的表名称
     *
     * @return 表名称列表
     */
    List<String> selectDistinctTableNames();
    
    /**
     * 获取指定服务的权限统计信息
     *
     * @param serviceName 微服务名称
     * @return 权限统计信息（Map格式：权限级别 -> 数量）
     */
    List<java.util.Map<String, Object>> getPermissionStatsByService(@Param("serviceName") String serviceName);
}
