package com.example.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量用户操作请求DTO
 * 
 * <p>用于接收批量用户操作的请求参数，支持批量删除、状态更新等操作。
 * 该DTO提供安全的批量操作功能，包含操作类型验证和用户ID列表验证。
 * 
 * <p>支持的批量操作：
 * <ul>
 *   <li>DELETE：批量删除用户</li>
 *   <li>ACTIVATE：批量激活用户</li>
 *   <li>DEACTIVATE：批量停用用户</li>
 *   <li>SUSPEND：批量暂停用户</li>
 * </ul>
 * 
 * <p>安全限制：
 * <ul>
 *   <li>单次操作最多100个用户</li>
 *   <li>需要相应的权限验证</li>
 *   <li>操作会记录详细的审计日志</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Data
@Schema(description = "批量用户操作请求")
public class BatchUserRequest {
    
    /**
     * 操作类型
     */
    @NotNull(message = "操作类型不能为空")
    @Pattern(regexp = "^(DELETE|ACTIVATE|DEACTIVATE|SUSPEND)$", 
             message = "操作类型必须是：DELETE、ACTIVATE、DEACTIVATE、SUSPEND之一")
    @Schema(description = "操作类型", required = true, example = "ACTIVATE", 
            allowableValues = {"DELETE", "ACTIVATE", "DEACTIVATE", "SUSPEND"})
    private String operation;
    
    /**
     * 用户ID列表
     */
    @NotEmpty(message = "用户ID列表不能为空")
    @Size(min = 1, max = 100, message = "单次操作用户数量必须在1-100之间")
    @Schema(description = "用户ID列表", required = true, example = "[1001, 1002, 1003]")
    private List<Long> userIds;
    
    /**
     * 操作原因（可选）
     */
    @Size(max = 500, message = "操作原因不能超过500字符")
    @Schema(description = "操作原因", example = "批量激活新注册用户")
    private String reason;
}
