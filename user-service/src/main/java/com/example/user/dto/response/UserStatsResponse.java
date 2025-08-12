package com.example.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 用户统计响应DTO
 *
 * <p>用于返回用户统计分析数据，包含用户数量、角色分布、状态分布等统计信息。
 * 该DTO提供全面的用户数据分析，支持管理决策和业务监控。
 *
 * <p>统计维度：
 * <ul>
 *   <li>总体统计：总用户数、活跃用户数、新增用户数</li>
 *   <li>角色分布：各角色的用户数量和占比</li>
 *   <li>状态分布：各状态的用户数量和占比</li>
 *   <li>等级分布：各等级的用户数量分布</li>
 *   <li>时间趋势：最近一段时间的用户增长趋势</li>
 * </ul>
 *
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Data
@Schema(description = "用户统计响应")
public class UserStatsResponse {

    /**
     * 总用户数
     */
    @Schema(description = "总用户数", example = "1500")
    private Long totalUsers;

    /**
     * 活跃用户数
     */
    @Schema(description = "活跃用户数", example = "1350")
    private Long activeUsers;

    /**
     * 今日新增用户数
     */
    @Schema(description = "今日新增用户数", example = "25")
    private Long todayNewUsers;

    /**
     * 本周新增用户数
     */
    @Schema(description = "本周新增用户数", example = "120")
    private Long weekNewUsers;

    /**
     * 本月新增用户数
     */
    @Schema(description = "本月新增用户数", example = "450")
    private Long monthNewUsers;

    /**
     * 角色分布统计
     * Key: 角色名称, Value: 用户数量
     */
    @Schema(description = "角色分布统计")
    private Map<String, Long> roleDistribution;

    /**
     * 状态分布统计
     * Key: 状态名称, Value: 用户数量
     */
    @Schema(description = "状态分布统计")
    private Map<String, Long> statusDistribution;

    /**
     * 等级分布统计
     * Key: 等级, Value: 用户数量
     */
    @Schema(description = "等级分布统计")
    private Map<String, Long> levelDistribution;

    /**
     * 最近7天新增用户趋势
     * Key: 日期(YYYY-MM-DD), Value: 新增用户数
     */
    @Schema(description = "最近7天新增用户趋势")
    private Map<String, Long> weeklyTrend;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间", example = "2025-08-07 16:30:00")
    private String statisticsTime;

    /**
     * 最近30天新增用户趋势
     * Key: 日期(YYYY-MM-DD), Value: 新增用户数
     */
    @Schema(description = "最近30天新增用户趋势")
    private Map<String, Long> monthlyTrend;


    /**
     * 最近7天新增用户趋势（按角色分组）
     * Key: 角色, Value: { 日期->数量 }
     */
    @Schema(description = "最近7天新增趋势（按角色分组）")
    private Map<String, Map<String, Long>> weeklyTrendByRole;

    /**
     * 最近7天新增用户趋势（按状态分组）
     */
    @Schema(description = "最近7天新增趋势（按状态分组）")
    private Map<String, Map<String, Long>> weeklyTrendByStatus;

    /**
     * 最近30天新增用户趋势（按角色分组）
     */
    @Schema(description = "最近30天新增趋势（按角色分组）")
    private Map<String, Map<String, Long>> monthlyTrendByRole;

    /**
     * 最近30天新增用户趋势（按状态分组）
     */
    @Schema(description = "最近30天新增趋势（按状态分组）")
    private Map<String, Map<String, Long>> monthlyTrendByStatus;

}
