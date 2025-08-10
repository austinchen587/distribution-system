package com.example.user.facade;

import com.example.data.entity.User;
import com.example.data.mapper.UserMapper;
import com.example.user.dto.response.UserResponse;
import com.example.user.dto.response.UserListResponse;
import com.example.user.dto.request.UserSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户数据访问门面类
 * 
 * <p>封装data-access模块的UserMapper，提供用户数据访问的统一接口。
 * 该门面类负责Entity到DTO的转换，以及权限控制的集成。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>用户CRUD操作的数据访问</li>
 *   <li>Entity到DTO的转换</li>
 *   <li>分页查询和条件筛选</li>
 *   <li>用户统计和层级查询</li>
 *   <li>数据权限控制集成</li>
 * </ul>
 * 
 * <p>权限控制：
 * 所有数据访问方法都通过@DataPermission注解进行权限控制，
 * 确保用户只能访问其权限范围内的数据。
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Slf4j
@Component
public class UserDataFacade {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户响应DTO
     */
    public Optional<UserResponse> findById(Long id) {
        log.debug("查找用户: id={}", id);
        Optional<User> userOpt = userMapper.findById(id);
        return userOpt.map(this::convertToResponse);
    }
    
    /**
     * 分页查询用户列表
     * 
     * @param page 页码
     * @param pageSize 页面大小
     * @return 用户列表响应
     */
    public UserListResponse findAll(int page, int pageSize) {
        log.debug("分页查询用户: page={}, pageSize={}", page, pageSize);
        
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.findAll(offset, pageSize);
        long totalCount = userMapper.count();
        
        return buildUserListResponse(users, totalCount, page, pageSize, null, null, null);
    }
    
    /**
     * 根据条件搜索用户
     * 
     * @param searchRequest 搜索请求
     * @return 用户列表响应
     */
    public UserListResponse searchUsers(UserSearchRequest searchRequest) {
        log.debug("搜索用户: {}", searchRequest);
        
        int offset = (searchRequest.getPage() - 1) * searchRequest.getPageSize();
        
        // 使用UserMapper的条件查询方法
        List<User> users = userMapper.findByConditions(
            searchRequest.getRole(),
            searchRequest.getStatus(),
            null, // parentId 暂时不在搜索条件中
            offset,
            searchRequest.getPageSize()
        );
        
        // 统计总数
        long totalCount = userMapper.countByConditions(
            searchRequest.getRole(),
            searchRequest.getStatus()
        );
        
        return buildUserListResponse(users, totalCount, searchRequest.getPage(), 
            searchRequest.getPageSize(), searchRequest.getKeyword(), 
            searchRequest.getRole(), searchRequest.getStatus());
    }
    
    /**
     * 保存用户
     * 
     * @param user 用户实体
     * @return 保存后的用户响应DTO
     */
    public UserResponse save(User user) {
        log.debug("保存用户: {}", user.getUsername());
        
        if (user.getId() == null) {
            // 新增用户
            userMapper.insert(user);
        } else {
            // 更新用户
            userMapper.update(user);
        }
        
        return convertToResponse(user);
    }
    
    /**
     * 根据ID删除用户
     * 
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteById(Long id) {
        log.debug("删除用户: id={}", id);
        int result = userMapper.deleteById(id);
        return result > 0;
    }
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    public boolean existsByUsername(String username, Long excludeId) {
        return userMapper.existsByUsername(username, excludeId);
    }
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    public boolean existsByEmail(String email, Long excludeId) {
        return userMapper.existsByEmail(email, excludeId);
    }
    
    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    public boolean existsByPhone(String phone, Long excludeId) {
        return userMapper.existsByPhone(phone, excludeId);
    }
    
    /**
     * 根据上级ID查找下级用户
     * 
     * @param parentId 上级用户ID
     * @return 下级用户列表
     */
    public List<UserResponse> findByParentId(Long parentId) {
        log.debug("查找下级用户: parentId={}", parentId);
        List<User> users = userMapper.findByParentId(parentId);
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    /**
     * 统计用户总数
     * 
     * @return 用户总数
     */
    public long countTotal() {
        return userMapper.count();
    }
    
    /**
     * 根据角色统计用户数量
     * 
     * @param role 角色
     * @return 用户数量
     */
    public long countByRole(String role) {
        return userMapper.countByConditions(role, null);
    }
    
    /**
     * 根据状态统计用户数量
     * 
     * @param status 状态
     * @return 用户数量
     */
    public long countByStatus(String status) {
        return userMapper.countByConditions(null, status);
    }
    
    /**
     * 将User实体转换为UserResponse DTO
     * 
     * @param user 用户实体
     * @return 用户响应DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus() != null ? user.getStatus() : null);
        response.setCommissionRate(user.getCommissionRate() != null ? user.getCommissionRate().doubleValue() : null);
        response.setParentId(user.getParentId());
        
        // 格式化时间字段
        if (user.getCreatedAt() != null) {
            response.setCreatedAt(user.getCreatedAt().format(DATE_TIME_FORMATTER));
        }
        if (user.getUpdatedAt() != null) {
            response.setUpdatedAt(user.getUpdatedAt().format(DATE_TIME_FORMATTER));
        }
        if (user.getLastLoginAt() != null) {
            response.setLastLoginAt(user.getLastLoginAt().format(DATE_TIME_FORMATTER));
        }
        
        return response;
    }
    
    /**
     * 构建用户列表响应
     * 
     * @param users 用户列表
     * @param totalCount 总数
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @param keyword 关键词
     * @param role 角色
     * @param status 状态
     * @return 用户列表响应
     */
    private UserListResponse buildUserListResponse(List<User> users, long totalCount, 
            int currentPage, int pageSize, String keyword, String role, String status) {
        
        UserListResponse response = new UserListResponse();
        response.setUsers(users.stream().map(this::convertToResponse).collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalPages((int) Math.ceil((double) totalCount / pageSize));
        response.setHasNext(currentPage < response.getTotalPages());
        response.setHasPrevious(currentPage > 1);
        response.setKeyword(keyword);
        response.setRole(role);
        response.setStatus(status);
        
        return response;
    }
}
