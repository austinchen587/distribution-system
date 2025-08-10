package com.example.user.service;

import com.example.common.dto.CommonResult;
import com.example.user.dto.request.*;
import com.example.user.dto.response.*;

/**
 * 用户管理服务接口
 * 
 * <p>定义用户管理的核心业务逻辑接口，包含用户CRUD操作、层级关系管理、
 * 统计分析等功能。该接口遵循RESTful设计原则，提供完整的用户管理能力。
 * 
 * <p>主要功能模块：
 * <ul>
 *   <li>用户基础管理：创建、查询、更新、删除</li>
 *   <li>用户搜索和筛选：多条件查询、分页支持</li>
 *   <li>用户层级关系：上下级关系管理</li>
 *   <li>用户统计分析：数量统计、分布分析</li>
 *   <li>批量操作：批量状态更新、批量删除</li>
 *   <li>数据导出：用户数据导出功能</li>
 * </ul>
 * 
 * <p>权限控制：
 * 所有业务方法都包含权限验证，确保用户只能操作其权限范围内的数据。
 * 上级用户可以管理下级用户，下级用户无法访问上级或平级数据。
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
public interface UserService {
    
    /**
     * 分页查询用户列表
     * 
     * <p>根据分页参数和筛选条件查询用户列表，支持角色和状态筛选。
     * 返回的用户数据基于当前用户的权限范围进行过滤。
     * 
     * @param page 页码，从1开始
     * @param pageSize 每页大小，范围1-100
     * @param role 角色筛选，可选
     * @param status 状态筛选，可选
     * @return 用户列表响应，包含分页信息
     */
    CommonResult<UserListResponse> getUsers(int page, int pageSize, String role, String status);
    
    /**
     * 创建新用户
     * 
     * <p>创建新的用户账号，包含完整的数据验证和权限检查。
     * 只能创建比当前用户权限低的角色，自动建立上下级关系。
     * 
     * @param request 创建用户请求，包含用户基本信息
     * @return 创建成功的用户信息
     * @throws com.example.common.exception.BusinessException 当数据验证失败或权限不足时
     */
    CommonResult<UserResponse> createUser(CreateUserRequest request);
    
    /**
     * 获取用户详情
     * 
     * <p>根据用户ID获取用户的详细信息，包含基本信息、角色权限、层级关系等。
     * 只能查看权限范围内的用户信息。
     * 
     * @param id 用户ID
     * @return 用户详细信息
     * @throws com.example.common.exception.BusinessException 当用户不存在或无权限访问时
     */
    CommonResult<UserResponse> getUserById(Long id);
    
    /**
     * 更新用户信息
     * 
     * <p>更新用户的基本信息、角色、状态等，支持部分字段更新。
     * 包含权限验证，确保只能更新权限范围内的用户。
     * 
     * @param id 用户ID
     * @param request 更新用户请求，包含需要更新的字段
     * @return 更新后的用户信息
     * @throws com.example.common.exception.BusinessException 当用户不存在、权限不足或数据验证失败时
     */
    CommonResult<UserResponse> updateUser(Long id, UpdateUserRequest request);
    
    /**
     * 删除用户
     * 
     * <p>删除指定的用户账号，包含权限验证和关联数据处理。
     * 删除用户时需要处理其下级关系和相关业务数据。
     * 
     * @param id 用户ID
     * @return 删除操作结果
     * @throws com.example.common.exception.BusinessException 当用户不存在、权限不足或存在关联数据时
     */
    CommonResult<Void> deleteUser(Long id);
    
    /**
     * 获取用户层级关系
     * 
     * <p>获取当前用户权限范围内的用户层级关系树，展示组织架构。
     * 返回树形结构数据，包含上下级关系和统计信息。
     * 
     * @return 用户层级关系树
     */
    CommonResult<UserHierarchyResponse> getUserHierarchy();
    
    /**
     * 获取用户统计信息
     * 
     * <p>获取用户相关的统计数据，包含总数、角色分布、状态分布、增长趋势等。
     * 统计数据基于当前用户的权限范围。
     * 
     * @return 用户统计信息
     */
    CommonResult<UserStatsResponse> getUserStats();
    
    /**
     * 搜索用户
     * 
     * <p>根据搜索条件查询用户，支持关键词搜索、多维度筛选、排序等功能。
     * 搜索结果基于当前用户的权限范围进行过滤。
     * 
     * @param request 搜索请求，包含搜索条件和分页参数
     * @return 搜索结果列表
     */
    CommonResult<UserListResponse> searchUsers(UserSearchRequest request);
    
    /**
     * 批量操作用户
     * 
     * <p>对多个用户执行批量操作，如批量删除、状态更新等。
     * 包含权限验证，确保只能操作权限范围内的用户。
     * 
     * @param request 批量操作请求，包含操作类型和用户ID列表
     * @return 批量操作结果
     * @throws com.example.common.exception.BusinessException 当权限不足或操作失败时
     */
    CommonResult<Void> batchOperateUsers(BatchUserRequest request);
    
    /**
     * 导出用户数据
     * 
     * <p>导出当前用户权限范围内的用户数据，支持多种格式。
     * 导出的数据包含用户基本信息、角色权限等。
     * 
     * @param format 导出格式，如"excel"、"csv"
     * @return 导出文件的字节数组
     * @throws com.example.common.exception.BusinessException 当权限不足或导出失败时
     */
    byte[] exportUsers(String format);
}
