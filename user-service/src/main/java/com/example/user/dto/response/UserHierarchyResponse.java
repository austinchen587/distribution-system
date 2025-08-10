package com.example.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户层级关系响应DTO
 * 
 * <p>用于返回用户层级关系的树形结构数据，展示用户的上下级关系。
 * 该DTO支持递归的层级结构，可以展示完整的组织架构。
 * 
 * <p>层级信息：
 * <ul>
 *   <li>用户基本信息：ID、姓名、角色</li>
 *   <li>层级关系：上级用户、下级用户列表</li>
 *   <li>统计信息：下级用户数量、层级深度</li>
 *   <li>权限信息：管理权限范围</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Data
@Schema(description = "用户层级关系响应")
public class UserHierarchyResponse {
    
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
     * 下级用户列表
     */
    @Schema(description = "下级用户列表")
    private List<UserHierarchyResponse> children;
    
    /**
     * 直接下级用户数量
     */
    @Schema(description = "直接下级用户数量", example = "5")
    private Integer directChildrenCount;
    
    /**
     * 所有下级用户数量（包括间接下级）
     */
    @Schema(description = "所有下级用户数量", example = "25")
    private Integer totalChildrenCount;
    
    /**
     * 层级深度
     */
    @Schema(description = "层级深度", example = "2")
    private Integer depth;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-08-07 10:30:00")
    private String createdAt;
}
