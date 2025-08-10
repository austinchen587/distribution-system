package com.example.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户列表响应DTO
 * 
 * <p>用于返回用户列表查询结果，包含用户数据和分页信息。
 * 该DTO提供完整的分页元数据，便于前端进行分页展示和导航。
 * 
 * <p>响应内容：
 * <ul>
 *   <li>用户数据：用户信息列表</li>
 *   <li>分页信息：总数、页码、页面大小、总页数</li>
 *   <li>查询信息：查询条件和排序信息</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Data
@Schema(description = "用户列表响应")
public class UserListResponse {
    
    /**
     * 用户列表
     */
    @Schema(description = "用户列表")
    private List<UserResponse> users;
    
    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "150")
    private Long totalCount;
    
    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Integer currentPage;
    
    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20")
    private Integer pageSize;
    
    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "8")
    private Integer totalPages;
    
    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    /**
     * 查询关键词
     */
    @Schema(description = "查询关键词", example = "张三")
    private String keyword;
    
    /**
     * 角色筛选
     */
    @Schema(description = "角色筛选", example = "sales")
    private String role;
    
    /**
     * 状态筛选
     */
    @Schema(description = "状态筛选", example = "ACTIVE")
    private String status;
}
