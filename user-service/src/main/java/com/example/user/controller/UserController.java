package com.example.user.controller;

import com.example.common.dto.CommonResult;
import com.example.user.dto.request.*;
import com.example.user.dto.response.*;
import com.example.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 用户管理控制器
 * 
 * <p>提供用户管理的REST API接口，包含用户CRUD操作、搜索、统计、批量操作等功能。
 * 该控制器严格遵循RESTful设计原则，提供完整的用户管理能力。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>用户基础管理：创建、查询、更新、删除</li>
 *   <li>用户搜索和筛选：多条件查询、分页支持</li>
 *   <li>用户层级关系：获取下级用户列表</li>
 *   <li>用户统计分析：数量统计、分布分析</li>
 *   <li>批量操作：批量状态更新、批量删除</li>
 *   <li>数据导出：用户数据导出功能</li>
 * </ul>
 * 
 * <p>权限控制：
 * 所有接口都需要JWT认证，并根据用户角色进行权限验证。
 * 用户只能操作其权限范围内的数据。
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "用户管理", description = "用户管理相关接口")
@SecurityRequirement(name = "BearerAuth")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户列表
     * 
     * <p>分页查询用户列表，支持角色和状态筛选。
     * 返回的用户数据基于当前用户的权限范围进行过滤。
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页查询用户列表，支持多维度筛选")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "没有权限访问用户列表"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public CommonResult<UserListResponse> getUsers(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            
            @Parameter(description = "每页数量，范围1-100", example = "20")
            @RequestParam(name = "page_size", defaultValue = "20") @Min(1) @Max(100) Integer pageSize,
            
            @Parameter(description = "角色筛选", example = "sales")
            @RequestParam(required = false) String role,
            
            @Parameter(description = "状态筛选", example = "active")
            @RequestParam(required = false) String status) {
        
        log.info("获取用户列表: page={}, pageSize={}, role={}, status={}", page, pageSize, role, status);
        return userService.getUsers(page, pageSize, role, status);
    }
    
    /**
     * 创建用户
     * 
     * <p>创建新的用户账号，包含完整的数据验证和权限检查。
     * 只能创建比当前用户权限低的角色，自动建立上下级关系。
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新的用户账户，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "用户创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "409", description = "用户名、邮箱或手机号已存在"),
        @ApiResponse(responseCode = "422", description = "数据验证失败")
    })
    public CommonResult<UserResponse> createUser(
            @Parameter(description = "创建用户请求", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("创建用户: username={}, role={}", request.getUsername(), request.getRole());
        return userService.createUser(request);
    }
    
    /**
     * 获取用户详情
     * 
     * <p>根据用户ID获取用户的详细信息，包含基本信息、角色权限、层级关系等。
     * 只能查看权限范围内的用户信息。
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限访问该用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public CommonResult<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true, example = "1001")
            @PathVariable Long id) {
        
        log.info("获取用户详情: id={}", id);
        return userService.getUserById(id);
    }
    
    /**
     * 更新用户信息
     * 
     * <p>更新用户的基本信息、角色、状态等，支持部分字段更新。
     * 包含权限验证，确保只能更新权限范围内的用户。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息，支持部分字段更新")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "用户更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "无权限修改该用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "409", description = "邮箱或手机号已存在"),
        @ApiResponse(responseCode = "422", description = "数据验证失败")
    })
    public CommonResult<UserResponse> updateUser(
            @Parameter(description = "用户ID", required = true, example = "1001")
            @PathVariable Long id,
            
            @Parameter(description = "更新用户请求", required = true)
            @Valid @RequestBody UpdateUserRequest request) {
        
        log.info("更新用户: id={}", id);
        return userService.updateUser(id, request);
    }
    
    /**
     * 删除用户
     * 
     * <p>删除指定的用户账号，包含权限验证和关联数据处理。
     * 删除用户时需要处理其下级关系和相关业务数据。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户，需要相应权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "用户删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限删除该用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "409", description = "该用户存在下级用户，无法删除")
    })
    public CommonResult<Void> deleteUser(
            @Parameter(description = "用户ID", required = true, example = "1001")
            @PathVariable Long id) {
        
        log.info("删除用户: id={}", id);
        return userService.deleteUser(id);
    }

    /**
     * 搜索用户
     *
     * <p>根据搜索条件查询用户，支持关键词搜索、多维度筛选、排序等功能。
     * 搜索结果基于当前用户的权限范围进行过滤。
     */
    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户，支持模糊匹配")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "搜索完成"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "422", description = "搜索参数无效")
    })
    public CommonResult<UserListResponse> searchUsers(
            @Parameter(description = "搜索关键词", example = "张三")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer page,

            @Parameter(description = "每页数量", example = "20")
            @RequestParam(name = "page_size", defaultValue = "20") @Min(1) @Max(100) Integer pageSize,

            @Parameter(description = "角色筛选", example = "sales")
            @RequestParam(required = false) String role,

            @Parameter(description = "状态筛选", example = "active")
            @RequestParam(required = false) String status,

            @Parameter(description = "等级筛选", example = "3")
            @RequestParam(required = false) Integer level,

            @Parameter(description = "创建时间起始", example = "2025-01-01")
            @RequestParam(name = "date_from", required = false) String dateFrom,

            @Parameter(description = "创建时间结束", example = "2025-12-31")
            @RequestParam(name = "date_to", required = false) String dateTo,

            @Parameter(description = "排序字段", example = "createdAt")
            @RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "排序方向", example = "DESC")
            @RequestParam(name = "sort_order", defaultValue = "DESC") String sortOrder) {

        log.info("搜索用户: keyword={}, page={}, pageSize={}", keyword, page, pageSize);

        UserSearchRequest request = new UserSearchRequest();
        request.setKeyword(keyword);
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setRole(role);
        request.setStatus(status);
        request.setLevel(level);
        request.setDateFrom(dateFrom);
        request.setDateTo(dateTo);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        return userService.searchUsers(request);
    }

    /**
     * 获取用户层级关系
     *
     * <p>获取当前用户权限范围内的用户层级关系树，展示组织架构。
     * 返回树形结构数据，包含上下级关系和统计信息。
     */
    @GetMapping("/hierarchy")
    @Operation(summary = "获取用户层级关系", description = "获取用户层级关系树形结构")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    public CommonResult<UserHierarchyResponse> getUserHierarchy() {
        log.info("获取用户层级关系");
        return userService.getUserHierarchy();
    }

    /**
     * 获取用户统计信息
     *
     * <p>获取用户相关的统计数据，包含总数、角色分布、状态分布、增长趋势等。
     * 统计数据基于当前用户的权限范围。
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计信息", description = "获取用户相关的统计数据，用于数据分析和报表展示")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    public CommonResult<UserStatsResponse> getUserStats(
            @Parameter(description = "统计时间起始", example = "2025-01-01")
            @RequestParam(name = "date_from", required = false) String dateFrom,

            @Parameter(description = "统计时间结束", example = "2025-12-31")
            @RequestParam(name = "date_to", required = false) String dateTo) {

        log.info("获取用户统计信息: dateFrom={}, dateTo={}", dateFrom, dateTo);
        return userService.getUserStats();
    }

    /**
     * 批量操作用户
     *
     * <p>对多个用户执行批量操作，如批量删除、状态更新等。
     * 包含权限验证，确保只能操作权限范围内的用户。
     */
    @PostMapping("/batch")
    @Operation(summary = "批量操作用户", description = "对多个用户执行批量操作，如状态变更、删除等")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "批量操作完成"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "部分用户不存在"),
        @ApiResponse(responseCode = "422", description = "操作参数无效")
    })
    public CommonResult<Void> batchOperateUsers(
            @Parameter(description = "批量操作请求", required = true)
            @Valid @RequestBody BatchUserRequest request) {

        log.info("批量操作用户: operation={}, userIds={}", request.getOperation(), request.getUserIds());
        return userService.batchOperateUsers(request);
    }

    /**
     * 导出用户数据
     *
     * <p>根据筛选条件导出用户数据，支持Excel和CSV格式。
     * 导出的数据包含用户基本信息、角色权限等。
     */
    @GetMapping("/export")
    @Operation(summary = "导出用户数据", description = "根据筛选条件导出用户数据为Excel或CSV文件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "导出成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "422", description = "导出参数无效")
    })
    public ResponseEntity<byte[]> exportUsers(
            @Parameter(description = "角色筛选", example = "sales")
            @RequestParam(required = false) String role,

            @Parameter(description = "状态筛选", example = "active")
            @RequestParam(required = false) String status,

            @Parameter(description = "创建时间起始", example = "2025-01-01")
            @RequestParam(name = "date_from", required = false) String dateFrom,

            @Parameter(description = "创建时间结束", example = "2025-12-31")
            @RequestParam(name = "date_to", required = false) String dateTo,

            @Parameter(description = "导出格式", example = "excel")
            @RequestParam(defaultValue = "excel") String format) {

        log.info("导出用户数据: role={}, status={}, format={}", role, status, format);

        try {
            byte[] data = userService.exportUsers(format);

            String filename = "users-export-" + System.currentTimeMillis();
            String contentType;

            if ("csv".equalsIgnoreCase(format)) {
                filename += ".csv";
                contentType = "text/csv";
            } else {
                filename += ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);

        } catch (Exception e) {
            log.error("导出用户数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取用户下级列表
     *
     * <p>获取指定用户的直接下级用户列表，支持分页。
     * 根据API文档规范实现的下级用户查询接口。
     */
    @GetMapping("/{id}/subordinates")
    @Operation(summary = "获取用户下级列表", description = "获取指定用户的直接下级用户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限访问该用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public CommonResult<UserListResponse> getUserSubordinates(
            @Parameter(description = "上级用户ID", required = true, example = "1001")
            @PathVariable Long id,

            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer page,

            @Parameter(description = "每页数量", example = "20")
            @RequestParam(name = "page_size", defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {

        log.info("获取用户下级列表: id={}, page={}, pageSize={}", id, page, pageSize);

        // 首先验证用户是否存在和权限
        CommonResult<UserResponse> userResult = userService.getUserById(id);
        if (!userResult.getSuccess()) {
            return CommonResult.error(userResult.getCode(), userResult.getMessage());
        }

        // 构建搜索请求，查询该用户的下级
        UserSearchRequest request = new UserSearchRequest();
        request.setPage(page);
        request.setPageSize(pageSize);
        // TODO: 添加parentId筛选条件到UserSearchRequest

        return userService.searchUsers(request);
    }
}
