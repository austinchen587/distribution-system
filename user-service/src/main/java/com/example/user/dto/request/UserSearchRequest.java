package com.example.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * 用户搜索请求DTO
 *
 * <p>用于接收用户搜索和筛选的请求参数，支持多维度的用户查询功能。
 * 该DTO提供灵活的搜索条件组合，支持分页和排序。
 *
 * <p>搜索功能：
 * <ul>
 *   <li>关键词搜索：匹配用户名、邮箱、手机号</li>
 *   <li>角色筛选：按用户角色过滤</li>
 *   <li>状态筛选：按用户状态过滤</li>
 *   <li>等级筛选：按用户等级过滤</li>
 *   <li>时间范围：按创建时间范围过滤</li>
 *   <li>父级筛选：按上级用户ID过滤（用于下级查询）</li>
 *   <li>分页支持：页码和页面大小</li>
 * </ul>
 *
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Data
@Schema(description = "用户搜索请求")
public class UserSearchRequest {

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "页码，从1开始", example = "1")
    private Integer page = 1;

    /**
     * 每页数量
     */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能大于100")
    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = 20;

    /**
     * 搜索关键词，匹配用户名、邮箱、手机号
     */
    @Schema(description = "搜索关键词", example = "张三")
    private String keyword;

    /**
     * 上级用户ID，用于查询某个用户的直接下级
     */
    @Min(value = 1, message = "parentId 必须为正整数")
    @Schema(description = "上级用户ID", example = "1001")
    private Long parentId;

    /**
     * 角色筛选
     */
    @Pattern(regexp = "^(super_admin|director|leader|sales|agent)$",
             message = "角色必须是：super_admin、director、leader、sales、agent之一")
    @Schema(description = "角色筛选", example = "sales",
            allowableValues = {"super_admin", "director", "leader", "sales", "agent"})
    private String role;

    /**
     * 状态筛选
     */
    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$",
             message = "状态必须是：ACTIVE、INACTIVE、SUSPENDED之一")
    @Schema(description = "状态筛选", example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;

    /**
     * 等级筛选
     */
    @Min(value = 1, message = "等级不能小于1")
    @Max(value = 10, message = "等级不能大于10")
    @Schema(description = "等级筛选", example = "3")
    private Integer level;

    /**
     * 创建时间起始日期，格式：YYYY-MM-DD
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式必须是YYYY-MM-DD")
    @Schema(description = "创建时间起始日期", example = "2025-01-01")
    private String dateFrom;

    /**
     * 创建时间结束日期，格式：YYYY-MM-DD
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式必须是YYYY-MM-DD")
    @Schema(description = "创建时间结束日期", example = "2025-12-31")
    private String dateTo;

    /**
     * 排序字段
     */
    @Pattern(regexp = "^(id|username|email|phone|role|status|level|createdAt|updatedAt)$",
             message = "排序字段必须是有效的字段名")
    @Schema(description = "排序字段", example = "createdAt",
            allowableValues = {"id", "username", "email", "phone", "role", "status", "level", "createdAt", "updatedAt"})
    private String sortBy = "createdAt";

    /**
     * 排序方向
     */
    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向必须是ASC或DESC")
    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortOrder = "DESC";
}
