package com.example.auth.dto.user;

// Removed direct entity dependency to avoid circular dependency
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * 用户DTO转换工具类
 * 负责Entity与DTO之间的转换
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class UserDtoConverter {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Note: Direct entity conversion methods removed to avoid circular dependency
    // These methods should be implemented in the service layer where both
    // common and data-access modules are available
    
    /**
     * 创建用户DTO（基于参数构建，不依赖实体）
     * 
     * @param id 用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param phone 手机号
     * @param realName 真实姓名
     * @param role 角色
     * @param status 状态
     * @param isActive 是否活跃
     * @param createdAt 创建时间
     * @param updatedAt 更新时间
     * @return UserDto
     */
    public static UserDto createUserDto(Long id, String username, String email, String phone, 
            String realName, String role, String status, Boolean isActive, 
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setUsername(username);
        dto.setRole(role);
        dto.setStatus(status);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setRealName(realName);
        dto.setIsActive(isActive);
        
        if (createdAt != null) {
            dto.setCreatedAt(createdAt.format(DATE_TIME_FORMATTER));
        }
        if (updatedAt != null) {
            dto.setUpdatedAt(updatedAt.format(DATE_TIME_FORMATTER));
        }
        
        return dto;
    }
    
    /**
     * 创建用户详情DTO（基于参数构建，不依赖实体）
     * 
     * @param userDto 基础用户DTO
     * @param realName 真实姓名
     * @param wechatId 微信ID
     * @param invitationCode 邀请码
     * @param lastLoginAt 最后登录时间
     * @param agentLevelName 代理等级名称
     * @param commissionRate 佣金率
     * @param baseSalary 基础工资
     * @param currentGmv 当前GMV
     * @param totalGmv 累计GMV
     * @return UserDetailsDto
     */
    public static UserDetailsDto createUserDetailsDto(UserDto userDto, String realName, String wechatId,
            String invitationCode, LocalDateTime lastLoginAt, String agentLevelName,
            java.math.BigDecimal commissionRate, java.math.BigDecimal baseSalary,
            java.math.BigDecimal currentGmv, java.math.BigDecimal totalGmv) {
        UserDetailsDto dto = new UserDetailsDto();
        
        // 设置基础用户信息
        dto.setUserInfo(userDto);
        
        // 设置详细信息
        dto.setRealName(realName);
        dto.setWechatId(wechatId);
        dto.setInvitationCode(invitationCode);
        
        // 设置时间信息
        if (lastLoginAt != null) {
            dto.setLastLoginAt(lastLoginAt.format(DATE_TIME_FORMATTER));
        }
        
        // 设置代理等级信息
        dto.setAgentLevelName(agentLevelName);
        dto.setCommissionRate(commissionRate);
        dto.setBaseSalary(baseSalary);
        
        // 设置业绩信息
        dto.setCurrentGmv(currentGmv);
        dto.setTotalGmv(totalGmv);
        
        return dto;
    }
    
    /**
     * 验证创建用户请求
     * 
     * @param request CreateUserRequest
     * @return 验证是否通过
     */
    public static boolean validateCreateUserRequest(CreateUserRequest request) {
        if (request == null) {
            return false;
        }
        
        // 基本验证逻辑
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return false;
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return false;
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            return false;
        }
        if (request.getPhone() == null || request.getPhone().length() != 11) {
            return false;
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证更新用户请求
     * 
     * @param request UpdateUserRequest
     * @return 验证是否通过
     */
    public static boolean validateUpdateUserRequest(UpdateUserRequest request) {
        if (request == null) {
            return false;
        }
        
        // 至少需要一个非空字段
        return request.getUsername() != null || 
               request.getEmail() != null || 
               request.getPhone() != null || 
               request.getRole() != null || 
               request.getStatus() != null || 
               request.getRealName() != null || 
               request.getWechatId() != null;
    }
    
    /**
     * 验证用户是否有权限查看详细信息
     * 
     * @param currentUserRole 当前用户角色
     * @param targetUserId 目标用户ID
     * @param currentUserId 当前用户ID
     * @return 是否有权限
     */
    public static boolean canViewUserDetails(String currentUserRole, Long targetUserId, Long currentUserId) {
        // super_admin和director可以查看所有用户详细信息
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole)) {
            return true;
        }
        
        // 用户可以查看自己的详细信息
        if (currentUserId != null && currentUserId.equals(targetUserId)) {
            return true;
        }
        
        // leader可以查看下级的详细信息（这里简化处理，实际需要查询层级关系）
        if ("leader".equals(currentUserRole)) {
            return true; // 需要进一步的权限验证逻辑
        }
        
        return false;
    }
}