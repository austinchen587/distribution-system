package com.example.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户响应DTO
 * 
 * <p>用于返回用户信息的响应数据，包含用户的基本信息、角色权限和层级关系。
 * 该DTO排除了敏感信息（如密码），确保数据安全。
 * 
 * <p>响应字段：
 * <ul>
 *   <li>基本信息：ID、用户名、邮箱、手机号</li>
 *   <li>权限信息：角色、状态、等级、提成比例</li>
 *   <li>层级关系：上级用户ID和姓名</li>
 *   <li>时间信息：创建时间、更新时间、最后登录时间</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Data
@Schema(description = "用户响应信息")
public class UserResponse {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1001")
    private Long id;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
    
    /**
     * 用户角色
     */
    @Schema(description = "用户角色", example = "sales")
    private String role;
    
    /**
     * 用户状态
     */
    @Schema(description = "用户状态", example = "ACTIVE")
    private String status;
    
    /**
     * 用户等级
     */
    @Schema(description = "用户等级", example = "3")
    private Integer level;
    
    /**
     * 提成比例
     */
    @Schema(description = "提成比例", example = "0.15")
    private Double commissionRate;
    
    /**
     * 上级用户ID
     */
    @Schema(description = "上级用户ID", example = "1000")
    private Long parentId;
    
    /**
     * 上级用户姓名
     */
    @Schema(description = "上级用户姓名", example = "李经理")
    private String parentName;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-08-07 10:30:00")
    private String createdAt;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2025-08-07 15:20:00")
    private String updatedAt;
    
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间", example = "2025-08-07 09:15:00")
    private String lastLoginAt;
}
